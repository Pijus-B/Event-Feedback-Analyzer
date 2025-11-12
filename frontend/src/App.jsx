import { useEffect, useMemo, useRef, useState } from 'react';
import { api } from './api';
import './index.css';

function Card({ title, children }) {
    return (
        <div className="card">
            {title && <h2>{title}</h2>}
            {children}
        </div>
    );
}

function Field({ label, children, hint }) {
    return (
        <label className="field">
            <div className="label">
                {label} {hint && <span className="hint">({hint})</span>}
            </div>
            {children}
        </label>
    );
}

function Spinner({ size = 16 }) {
    const style = { width: size, height: size };
    return <span className="spinner" style={style} aria-hidden="true" />;
}

function useToast() {
    const [toasts, setToasts] = useState([]);
    const timeoutsRef = useRef({});

    const push = (message, type = 'success', ms = 3000) => {
        const id = Date.now() + Math.random();
        setToasts((t) => [...t, { id, message, type }]);
        timeoutsRef.current[id] = setTimeout(() => {
            setToasts((t) => t.filter((x) => x.id !== id));
            delete timeoutsRef.current[id];
        }, ms);
    };

    const remove = (id) => {
        clearTimeout(timeoutsRef.current[id]);
        delete timeoutsRef.current[id];
        setToasts((t) => t.filter((x) => x.id !== id));
    };

    useEffect(() => () => {
        Object.values(timeoutsRef.current).forEach(clearTimeout);
    }, []);

    const Toasts = () => (
        <div className="toasts">
            {toasts.map((t) => (
                <div
                    key={t.id}
                    className={`toast ${t.type}`}
                    role="status"
                    onClick={() => remove(t.id)}
                    title="Click to dismiss"
                >
                    {t.message}
                </div>
            ))}
        </div>
    );

    return { push, Toasts };
}

function Ring({ label, value, raw, total }) {
    const deg = Math.min(360, Math.round((value / 100) * 360));
    const bg = `conic-gradient(#98a4ff ${deg}deg, #1a1a1f ${deg}deg)`; // light slice + dark remainder

    return (
        <div className="ring">
            <div className="gauge" style={{ background: bg }}>
                <div className="gaugeInner">{Math.max(0, Math.min(100, value))}%</div>
            </div>
            <div className="ringLabel">{label}</div>
            <div className="ringSub">{raw} of {total}</div>
        </div>
    );
}


export default function App() {
    const [tab, setTab] = useState('create');

    const [title, setTitle] = useState('');
    const [desc, setDesc] = useState('');
    const [createBusy, setCreateBusy] = useState(false);

    const [fbEventId, setFbEventId] = useState('');
    const [fbText, setFbText] = useState('');
    const [fbBusy, setFbBusy] = useState(false);

    const [sumEventId, setSumEventId] = useState('');
    const [summary, setSummary] = useState(null);
    const [sumBusy, setSumBusy] = useState(false);

    const { push: notify, Toasts } = useToast();

    const canCreate = title.trim().length >= 3 && desc.trim().length >= 3;
    const canFeedback = String(fbEventId).trim() !== '' && fbText.trim().length >= 3;
    const canSummary = String(sumEventId).trim() !== '';

    async function doCreate(e) {
        e.preventDefault();
        if (!canCreate) return;
        setCreateBusy(true);
        try {
            const id = await api.createEvent(title.trim(), desc.trim());
            const newId = typeof id === 'string' ? parseInt(id, 10) : id;
            setTitle('');
            setDesc('');
            setFbEventId(String(newId));
            setSumEventId(String(newId));
            notify(`Event #${newId} created`, 'success');
        } catch (err) {
            notify(err.message, 'error');
        } finally {
            setCreateBusy(false);
        }
    }

    async function doFeedback(e) {
        e.preventDefault();
        if (!canFeedback) return;
        setFbBusy(true);
        try {
            await api.createFeedback(Number(fbEventId), fbText.trim());
            setFbText('');
            notify('Feedback submitted', 'success');
        } catch (err) {
            notify(err.message, 'error');
        } finally {
            setFbBusy(false);
        }
    }

    async function doSummary(e) {
        e.preventDefault();
        if (!canSummary) return;
        setSumBusy(true);
        try {
            const s = await api.getSummary(Number(sumEventId));
            setSummary(s);
            notify('Summary loaded', 'success');
        } catch (err) {
            setSummary(null);
            notify(err.message, 'error');
        } finally {
            setSumBusy(false);
        }
    }

    const percentages = useMemo(() => {
        if (!summary) return { pos: 0, neu: 0, neg: 0, total: 0 };
        const total = Math.max(
            1,
            summary.total ?? (summary.positive + summary.neutral + summary.negative)
        );
        return {
            pos: Math.round((summary.positive / total) * 100),
            neu: Math.round((summary.neutral / total) * 100),
            neg: Math.round((summary.negative / total) * 100),
            total,
        };
    }, [summary]);

    return (
        <div className="wrap">
            <div className="brand">Event Feedback Analyzer</div>

            <div className="tabs">
                <button className={tab === 'create' ? 'active' : ''} onClick={() => setTab('create')}>
                    Create Event
                </button>
                <button className={tab === 'feedback' ? 'active' : ''} onClick={() => setTab('feedback')}>
                    Submit Feedback
                </button>
                <button className={tab === 'summary' ? 'active' : ''} onClick={() => setTab('summary')}>
                    Summary
                </button>
            </div>

            {tab === 'create' && (
                <Card title="New Event">
                    <form onSubmit={doCreate} className="form">
                        <Field label="Title" hint="min 3 characters">
                            <input value={title} onChange={(e) => setTitle(e.target.value)} required />
                        </Field>
                        <Field label="Description" hint="min 3 characters">
                            <textarea rows={3} value={desc} onChange={(e) => setDesc(e.target.value)} required />
                        </Field>
                        <button type="submit" disabled={!canCreate || createBusy}>
                            {createBusy ? (
                                <>
                                    <Spinner /> <span className="btnText">Creating…</span>
                                </>
                            ) : (
                                'Create'
                            )}
                        </button>
                    </form>
                </Card>
            )}

            {tab === 'feedback' && (
                <Card title="Submit Feedback">
                    <form onSubmit={doFeedback} className="form">
                        <Field label="Event ID">
                            <input
                                inputMode="numeric"
                                value={fbEventId}
                                onChange={(e) => setFbEventId(e.target.value)}
                                required
                            />
                        </Field>
                        <Field label="Feedback text" hint="min 3 characters">
              <textarea
                  rows={3}
                  value={fbText}
                  onChange={(e) => setFbText(e.target.value)}
                  required
              />
                        </Field>
                        <button type="submit" disabled={!canFeedback || fbBusy}>
                            {fbBusy ? (
                                <>
                                    <Spinner /> <span className="btnText">Sending…</span>
                                </>
                            ) : (
                                'Send'
                            )}
                        </button>
                    </form>
                </Card>
            )}

            {tab === 'summary' && (
                <Card title="Event Sentiment Summary">
                    <form onSubmit={doSummary} className="form">
                        <Field label="Event ID">
                            <input
                                inputMode="numeric"
                                value={sumEventId}
                                onChange={(e) => setSumEventId(e.target.value)}
                                required
                            />
                        </Field>
                        <button type="submit" disabled={!canSummary || sumBusy}>
                            {sumBusy ? (
                                <>
                                    <Spinner /> <span className="btnText">Loading…</span>
                                </>
                            ) : (
                                'Load'
                            )}
                        </button>
                    </form>

                    {summary && (
                        <div className="stats">
                            <Ring
                                label="Positive"
                                value={percentages.pos}
                                raw={summary.positive}
                                total={percentages.total}
                            />
                            <Ring
                                label="Neutral"
                                value={percentages.neu}
                                raw={summary.neutral}
                                total={percentages.total}
                            />
                            <Ring
                                label="Negative"
                                value={percentages.neg}
                                raw={summary.negative}
                                total={percentages.total}
                            />
                        </div>
                    )}
                </Card>
            )}

            <Toasts />
        </div>
    );
}

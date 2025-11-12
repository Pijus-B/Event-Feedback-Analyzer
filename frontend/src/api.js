export const API_BASE = 'https://event-feedback-analyzer-analyzer-java-328172629172.europe-west1.run.app/api';

async function request(path, init = {}) {
    const res = await fetch(`${API_BASE}${path}`, {
        headers: { 'Content-Type': 'application/json', ...(init.headers || {}) },
        ...init,
    });

    if (!res.ok) {
        let detail = '';
        try { detail = await res.text(); } catch {}
        throw new Error(`HTTP ${res.status} ${res.statusText}${detail ? ` — ${detail}` : ''}`);
    }

    const ct = res.headers.get('content-type') || '';
    if (ct.includes('application/json')) return res.json();
    return res.text();
}

export const api = {
    createEvent: (title, description) =>
        request('/events', { method: 'POST', body: JSON.stringify({ title, description }) }),

    createFeedback: (eventId, text) =>
        request(`/events/${eventId}/feedback`, {
            method: 'POST',
            body: JSON.stringify({ content: text }),
        }),

    getSummary: (eventId) => request(`/events/${eventId}/summary`),
};

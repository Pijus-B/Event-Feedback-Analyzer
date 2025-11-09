package com.efa.analyzer.model;

import jakarta.persistence.*;
import com.efa.analyzer.enums.Sentiment;
import java.time.OffsetDateTime;

@Entity
@Table(name = "feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(length = 4000, nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Sentiment sentiment;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = OffsetDateTime.now();
    }

    public Integer getId() { return id;}

    public Feedback setId(Integer id) {
        this.id = id;

        return this;
    }

    public Event getEventId() { return event;}

    public Feedback setEventId(Event event) {
        this.event = event;

        return this;
    }

    public String getContent() { return content; }

    public Feedback setContent(String content) {
        this.content = content;

        return this;
    }

    public Sentiment getSentiment() { return sentiment; }

    public Feedback setSentiment(Sentiment sentiment) {
        this.sentiment = sentiment;

        return this;
    }

    public OffsetDateTime getCreatedAt() { return createdAt;}

    public Feedback setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;

        return this;
    }
}

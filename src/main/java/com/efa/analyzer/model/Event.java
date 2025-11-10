package com.efa.analyzer.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "events")
public class Event {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false)
  private String title;

  @Column(length = 500)
  private String description;

  @Column(name = "created_at", nullable = false, updatable = false)
  private OffsetDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    if (createdAt == null) {
      createdAt = OffsetDateTime.now();
    }
  }

  public Integer getId() {
    return id;
  }

  public Event setId(Integer id) {
    this.id = id;

    return this;
  }

  public String getTitle() {
    return title;
  }

  public Event setTitle(String title) {
    this.title = title;

    return this;
  }

  public String getDescription() {
    return description;
  }

  public Event setDescription(String description) {
    this.description = description;

    return this;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public Event setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;

    return this;
  }
}

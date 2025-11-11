package com.efa.analyzer.backend.repository;

import com.efa.analyzer.backend.enums.Sentiment;
import com.efa.analyzer.backend.model.Feedback;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
  List<Feedback> findByEventId(Integer eventId);

  long countByEventIdAndSentiment(Integer eventId, Sentiment sentiment);
}

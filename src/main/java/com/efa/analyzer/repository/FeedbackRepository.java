package com.efa.analyzer.repository;

import com.efa.analyzer.enums.Sentiment;
import com.efa.analyzer.model.Feedback;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
  List<Feedback> findByEvent_Id(Integer eventId);

  long countByEvent_IdAndSentiment(Integer eventId, Sentiment sentiment);
}

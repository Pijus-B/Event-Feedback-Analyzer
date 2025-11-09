package com.efa.analyzer.repository;

import com.efa.analyzer.model.Feedback;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {}

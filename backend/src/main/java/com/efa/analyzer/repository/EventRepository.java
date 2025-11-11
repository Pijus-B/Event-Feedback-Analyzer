package com.efa.analyzer.backend.repository;

import com.efa.analyzer.backend.model.Event;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
  List<Event> findAllByTitle(String title);
}

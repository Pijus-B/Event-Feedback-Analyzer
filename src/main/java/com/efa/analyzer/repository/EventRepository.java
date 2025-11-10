package com.efa.analyzer.repository;

import com.efa.analyzer.model.Event;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
  Optional<Event> findByName(String name);

  boolean existsByName(String name);
}

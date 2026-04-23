package tn.esprit.spring.crud_event.repo;

import tn.esprit.spring.crud_event.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByOrganizerid(Long organizerid);
}
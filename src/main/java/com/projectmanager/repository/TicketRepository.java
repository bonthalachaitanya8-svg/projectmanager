package com.projectmanager.repository;

import com.projectmanager.model.Ticket;
import com.projectmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findBySubmittedByOrderByCreatedAtDesc(User user);
    List<Ticket> findAllByOrderByCreatedAtDesc();
    long countByStatus(String status);
}

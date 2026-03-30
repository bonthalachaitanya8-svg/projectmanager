package com.projectmanager.service;

import com.projectmanager.model.Ticket;
import com.projectmanager.model.User;
import com.projectmanager.repository.TicketRepository;
import com.projectmanager.repository.UserRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    public TicketService(TicketRepository ticketRepository,
                         UserRepository userRepository,
                         JavaMailSender mailSender) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }

    public Ticket submitTicket(String username, String subject, String message, String priority) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Ticket ticket = new Ticket();
        ticket.setSubject(subject);
        ticket.setMessage(message);
        ticket.setPriority(priority);
        ticket.setSubmittedBy(user);
        ticketRepository.save(ticket);

        // Send email notification to admin
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(System.getenv("ADMIN_EMAIL"));
            mail.setSubject("[Project Manager] New Ticket: " + subject);
            mail.setText(
                "New ticket submitted by: " + user.getFullName() + " (@" + username + ")\n\n" +
                "Priority: " + priority + "\n" +
                "Subject: " + subject + "\n\n" +
                "Message:\n" + message + "\n\n" +
                "Login to admin panel to respond."
            );
            mailSender.send(mail);
        } catch (Exception e) {
            System.out.println("Email sending failed: " + e.getMessage());
        }

        return ticket;
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Ticket> getTicketsByUsername(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return ticketRepository.findBySubmittedByOrderByCreatedAtDesc(user);
    }

    public Ticket getById(Long id) {
        return ticketRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ticket not found"));
    }

    public void replyToTicket(Long ticketId, String reply, String status) {
        Ticket ticket = getById(ticketId);
        ticket.setAdminReply(reply);
        ticket.setStatus(status);
        ticket.setUpdatedAt(LocalDateTime.now());
        ticketRepository.save(ticket);

        // Send reply email to user
        try {
            User user = ticket.getSubmittedBy();
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                SimpleMailMessage mail = new SimpleMailMessage();
                mail.setTo(user.getEmail());
                mail.setSubject("[Project Manager] Reply to your ticket: " + ticket.getSubject());
                mail.setText(
                    "Hello " + user.getFullName() + ",\n\n" +
                    "Your ticket has been updated.\n\n" +
                    "Status: " + status + "\n\n" +
                    "Admin Reply:\n" + reply + "\n\n" +
                    "Project Manager Team"
                );
                mailSender.send(mail);
            }
        } catch (Exception e) {
            System.out.println("Reply email failed: " + e.getMessage());
        }
    }

    public void deleteTicket(Long id) {
        ticketRepository.deleteById(id);
    }

    public long countByStatus(String status) {
        return ticketRepository.countByStatus(status);
    }
}

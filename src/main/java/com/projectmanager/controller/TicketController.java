package com.projectmanager.controller;

import com.projectmanager.service.TicketService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    // User - show contact form
    @GetMapping("/user/contact")
    public String contactForm() {
        return "user/contact";
    }

    // User - submit ticket
    @PostMapping("/user/contact")
    public String submitTicket(@RequestParam String subject,
                               @RequestParam String message,
                               @RequestParam(defaultValue = "MEDIUM") String priority,
                               Authentication auth,
                               RedirectAttributes ra) {
        ticketService.submitTicket(auth.getName(), subject, message, priority);
        ra.addFlashAttribute("successMsg", "Your ticket has been submitted! Admin will respond soon.");
        return "redirect:/user/tickets";
    }

    // User - view their tickets
    @GetMapping("/user/tickets")
    public String userTickets(Authentication auth, Model model) {
        model.addAttribute("tickets", ticketService.getTicketsByUsername(auth.getName()));
        return "user/tickets";
    }

    // Admin - view all tickets
    @GetMapping("/admin/tickets")
    public String adminTickets(Model model) {
        model.addAttribute("tickets", ticketService.getAllTickets());
        model.addAttribute("openCount", ticketService.countByStatus("OPEN"));
        model.addAttribute("inProgressCount", ticketService.countByStatus("IN_PROGRESS"));
        model.addAttribute("resolvedCount", ticketService.countByStatus("RESOLVED"));
        return "admin/tickets";
    }

    // Admin - view single ticket and reply
    @GetMapping("/admin/ticket/{id}")
    public String viewTicket(@PathVariable Long id, Model model) {
        model.addAttribute("ticket", ticketService.getById(id));
        return "admin/ticket-detail";
    }

    // Admin - reply to ticket
    @PostMapping("/admin/ticket/{id}/reply")
    public String replyTicket(@PathVariable Long id,
                              @RequestParam String reply,
                              @RequestParam String status,
                              RedirectAttributes ra) {
        ticketService.replyToTicket(id, reply, status);
        ra.addFlashAttribute("successMsg", "Reply sent successfully!");
        return "redirect:/admin/tickets";
    }

    // Admin - delete ticket
    @GetMapping("/admin/ticket/{id}/delete")
    public String deleteTicket(@PathVariable Long id, RedirectAttributes ra) {
        ticketService.deleteTicket(id);
        ra.addFlashAttribute("successMsg", "Ticket deleted.");
        return "redirect:/admin/tickets";
    }
}

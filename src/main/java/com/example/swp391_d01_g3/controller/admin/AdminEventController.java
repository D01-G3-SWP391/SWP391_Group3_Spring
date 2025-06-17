package com.example.swp391_d01_g3.controller.admin;

import com.example.swp391_d01_g3.model.Event;
import com.example.swp391_d01_g3.service.admin.IAdminEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Controller
@RequestMapping("/Admin")
public class AdminEventController {

    @Autowired
    private IAdminEventService adminEventService;

    private static final Logger logger = LoggerFactory.getLogger(AdminEventController.class);

    // 1. CONFIRM CAREER EVENTS - View pending events for confirmation
    @GetMapping("/PendingEvents")
    public String showPendingEvents(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    @RequestParam(required = false) String keyword,
                                    Model model) {
        try {
            Page<Event> eventPage;

            if (page < 0) page = 0;
            if (size <= 0 || size > 50) size = 10;

            if (keyword != null && !keyword.trim().isEmpty()) {
                eventPage = adminEventService.searchPendingEvents(keyword.trim(), page, size);
                model.addAttribute("keyword", keyword);
            } else {
                eventPage = adminEventService.getPendingEvents(page, size);
            }

            model.addAttribute("eventList", eventPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", eventPage.getTotalPages());
            model.addAttribute("totalItems", eventPage.getTotalElements());
            model.addAttribute("hasNext", eventPage.hasNext());
            model.addAttribute("hasPrevious", eventPage.hasPrevious());
            model.addAttribute("keyword", keyword != null ? keyword : "");

            return "admin/pendingEvents";

        } catch (Exception e) {
            logger.error("Error loading pending events: ", e);
            model.addAttribute("error", "Error loading pending events: " + e.getMessage());

            // Safe defaults
            model.addAttribute("eventList", Collections.emptyList());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("totalItems", 0);
            model.addAttribute("hasNext", false);
            model.addAttribute("hasPrevious", false);
            model.addAttribute("keyword", "");

            return "admin/pendingEvents";
        }
    }

    // View event details for confirmation
    @GetMapping("/ConfirmEvent/{id}")
    @Transactional
    public String viewEventForConfirmation(@PathVariable("id") Integer eventId, Model model) {
        try {
            Event event = adminEventService.getEventForConfirmation(eventId);
            if (event == null) {
                return "redirect:/Admin/PendingEvents";
            }

            // Force lazy loading
            if (event.getEmployer() != null) {
                event.getEmployer().getCompanyName();
                if (event.getEmployer().getAccount() != null) {
                    event.getEmployer().getAccount().getEmail();
                }
            }

            model.addAttribute("event", event);
            return "admin/confirmEventDetails";
        } catch (Exception e) {
            logger.error("Error loading event for confirmation: ", e);
            return "redirect:/Admin/PendingEvents";
        }
    }

    // Confirm (Approve) event
    @PostMapping("/ConfirmEvent/{id}")
    public String confirmEvent(@PathVariable("id") Integer eventId,
                               RedirectAttributes redirectAttributes) {
        try {
            adminEventService.confirmEvent(eventId);
            redirectAttributes.addFlashAttribute("success", "Event confirmed and approved successfully");
        } catch (Exception e) {
            logger.error("Error confirming event: ", e);
            redirectAttributes.addFlashAttribute("error", "Error confirming event: " + e.getMessage());
        }
        return "redirect:/Admin/PendingEvents";
    }

    // Reject event
    @PostMapping("/RejectEvent/{id}")
    public String rejectEvent(@PathVariable("id") Integer eventId,
                              @RequestParam(required = false) String reason,
                              RedirectAttributes redirectAttributes) {
        try {
            adminEventService.rejectEvent(eventId, reason);
            redirectAttributes.addFlashAttribute("success", "Event rejected successfully");
        } catch (Exception e) {
            logger.error("Error rejecting event: ", e);
            redirectAttributes.addFlashAttribute("error", "Error rejecting event: " + e.getMessage());
        }
        return "redirect:/Admin/PendingEvents";
    }

    // 2. VIEW CAREER EVENT LIST - View all events
    @GetMapping("/Events")
    public String showAllEvents(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                @RequestParam(required = false) String keyword,
                                @RequestParam(required = false) String status,
                                Model model) {
        try {
            Page<Event> eventPage;
            Event.ApprovalStatus approvalStatus = null;

            if (page < 0) page = 0;
            if (size <= 0 || size > 50) size = 10;

            if (status != null && !status.isEmpty()) {
                try {
                    approvalStatus = Event.ApprovalStatus.valueOf(status.toUpperCase());
                } catch (IllegalArgumentException e) {
                    logger.warn("Invalid status parameter: {}", status);
                }
            }

            eventPage = adminEventService.searchAllEvents(keyword, approvalStatus, page, size);

            model.addAttribute("eventList", eventPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", eventPage.getTotalPages());
            model.addAttribute("totalItems", eventPage.getTotalElements());
            model.addAttribute("hasNext", eventPage.hasNext());
            model.addAttribute("hasPrevious", eventPage.hasPrevious());
            model.addAttribute("selectedStatus", status);
            model.addAttribute("keyword", keyword);
            model.addAttribute("approvalStatuses", Event.ApprovalStatus.values());

            return "admin/viewEvents";

        } catch (Exception e) {
            logger.error("Error loading events: ", e);
            model.addAttribute("error", "Error loading events: " + e.getMessage());

            // Safe defaults
            model.addAttribute("eventList", Collections.emptyList());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("totalItems", 0);
            model.addAttribute("hasNext", false);
            model.addAttribute("hasPrevious", false);
            model.addAttribute("selectedStatus", "");
            model.addAttribute("keyword", "");
            model.addAttribute("approvalStatuses", Event.ApprovalStatus.values());

            return "admin/viewEvents";
        }
    }

    // View event details
    @GetMapping("/viewEventDetails/{id}")
    @Transactional
    public String viewEventDetails(@PathVariable("id") Integer eventId, Model model) {
        try {
            Event event = adminEventService.getEventById(eventId);
            if (event == null) {
                return "redirect:/Admin/Events";
            }

            // Force lazy loading
            if (event.getEmployer() != null) {
                event.getEmployer().getCompanyName();
                if (event.getEmployer().getAccount() != null) {
                    event.getEmployer().getAccount().getEmail();
                }
            }

            model.addAttribute("event", event);
            return "admin/viewEventDetails";
        } catch (Exception e) {
            logger.error("Error loading event details: ", e);
            return "redirect:/Admin/Events";
        }
    }

    // 3. UPDATE CAREER EVENT - Show edit form
    @GetMapping("/EditEvent/{id}")
    @Transactional
    public String showEditForm(@PathVariable("id") Integer eventId, Model model) {
        try {
            Event event = adminEventService.getEventById(eventId);
            if (event == null) {
                return "redirect:/Admin/Events";
            }

            // Force lazy loading
            if (event.getEmployer() != null) {
                event.getEmployer().getCompanyName();
            }

            model.addAttribute("event", event);
            return "admin/editEvent";
        } catch (Exception e) {
            logger.error("Error loading event for edit: ", e);
            return "redirect:/Admin/Events";
        }
    }

    // Update event
    @PostMapping("/UpdateEvent/{id}")
    public String updateEvent(@PathVariable("id") Integer eventId,
                              @ModelAttribute Event eventDetails,
                              RedirectAttributes redirectAttributes) {
        try {
            adminEventService.updateEvent(eventId, eventDetails);
            redirectAttributes.addFlashAttribute("success", "Event updated successfully");
        } catch (Exception e) {
            logger.error("Error updating event: ", e);
            redirectAttributes.addFlashAttribute("error", "Error updating event: " + e.getMessage());
        }
        return "redirect:/Admin/Events";
    }

    // 4. DELETE CAREER EVENT
    @PostMapping("/DeleteEvent/{id}")
    public String deleteEvent(@PathVariable("id") Integer eventId,
                              RedirectAttributes redirectAttributes) {
        try {
            adminEventService.deleteEvent(eventId);
            redirectAttributes.addFlashAttribute("success", "Event đã được xóa thành công!");
        } catch (Exception e) {
            logger.error("Error deleting event: ", e);
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa event: " + e.getMessage());
        }
        return "redirect:/Admin/Events";
    }

}

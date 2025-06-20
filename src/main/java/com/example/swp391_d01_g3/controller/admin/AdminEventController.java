package com.example.swp391_d01_g3.controller.admin;

import com.example.swp391_d01_g3.model.Event;
import com.example.swp391_d01_g3.service.event.IEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;

@Controller
@RequestMapping("/Admin")
public class AdminEventController {

    @Autowired
    private IEventService eventService;

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

            Pageable pageable = PageRequest.of(page, size);

            if (keyword != null && !keyword.trim().isEmpty()) {
                eventPage = eventService.searchEventsByKeywordAndStatus(keyword.trim(), Event.ApprovalStatus.PENDING, pageable);
                model.addAttribute("keyword", keyword);
            } else {
                eventPage = eventService.findByApprovalStatus(Event.ApprovalStatus.PENDING, pageable);
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
            Event event = eventService.findById(eventId);
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
            eventService.approveEvent(eventId, null);
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
            eventService.rejectEvent(eventId, null);
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

            Pageable pageable = PageRequest.of(page, size);

            if (status != null && !status.isEmpty()) {
                try {
                    approvalStatus = Event.ApprovalStatus.valueOf(status.toUpperCase());
                } catch (IllegalArgumentException e) {
                    logger.warn("Invalid status parameter: {}", status);
                }
            }

            // Search logic
            if (keyword != null && !keyword.trim().isEmpty() && approvalStatus != null) {
                eventPage = eventService.searchEventsByKeywordAndStatus(keyword, approvalStatus, pageable);
            } else if (keyword != null && !keyword.trim().isEmpty()) {
                eventPage = eventService.searchEvents(keyword, pageable);
            } else if (approvalStatus != null) {
                eventPage = eventService.findByApprovalStatus(approvalStatus, pageable);
            } else {
                eventPage = eventService.findAll(pageable);
            }

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
    @GetMapping("/Event/{id}")
    @Transactional
    public String viewEventDetails(@PathVariable("id") Integer eventId, Model model) {
        try {
            Event event = eventService.findById(eventId);
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
            Event event = eventService.findById(eventId);
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
            Event existingEvent = eventService.findById(eventId);
            if (existingEvent == null) {
                redirectAttributes.addFlashAttribute("error", "Event not found");
                return "redirect:/Admin/Events";
            }

            // Update fields
            existingEvent.setEventTitle(eventDetails.getEventTitle());
            existingEvent.setEventDescription(eventDetails.getEventDescription());
            existingEvent.setEventDate(eventDetails.getEventDate());
            existingEvent.setEventLocation(eventDetails.getEventLocation());
            existingEvent.setRegistrationDeadline(eventDetails.getRegistrationDeadline());
            existingEvent.setMaxParticipants(eventDetails.getMaxParticipants());
            existingEvent.setContactEmail(eventDetails.getContactEmail());

            eventService.save(existingEvent);
            redirectAttributes.addFlashAttribute("success", "Event updated successfully");
        } catch (Exception e) {
            logger.error("Error updating event: ", e);
            redirectAttributes.addFlashAttribute("error", "Error updating event: " + e.getMessage());
        }
        return "redirect:/Admin/Events";
    }

//    // 4. DELETE CAREER EVENT
//    @PostMapping("/DeleteEvent/{id}")
//    public String deleteEvent(@PathVariable("id") Integer eventId,
//                              RedirectAttributes redirectAttributes) {
//        try {
//            eventService.deleteEventWithRegistrations(eventId);
//            redirectAttributes.addFlashAttribute("success", "Event deleted successfully");
//        } catch (Exception e) {
//            logger.error("Error deleting event: ", e);
//            redirectAttributes.addFlashAttribute("error", "Error deleting event: " + e.getMessage());
//        }
//        return "redirect:/Admin/Events";
//    }

    // 5. CHANGE EVENT STATUS
//    @PostMapping("/ChangeEventStatus/{id}")
//    public String changeEventStatus(@PathVariable("id") Integer eventId,
//                                    @RequestParam("newStatus") String newStatus,
//                                    RedirectAttributes redirectAttributes) {
//        try {
//            Event event = eventService.findById(eventId);
//            if (event == null) {
//                redirectAttributes.addFlashAttribute("error", "Event not found");
//                return "redirect:/Admin/Events";
//            }
//
//            Event.EventStatus eventStatus = Event.EventStatus.valueOf(newStatus.toUpperCase());
//            Event.EventStatus oldStatus = event.getEventStatus();
//            event.setEventStatus(eventStatus);
//            eventService.save(event);
//
//            logger.info("Changed event status from {} to {} for event ID: {}", oldStatus, eventStatus, eventId);
//            redirectAttributes.addFlashAttribute("success", "Event status changed successfully to " + eventStatus);
//        } catch (IllegalArgumentException e) {
//            logger.error("Invalid status parameter: {}", newStatus);
//            redirectAttributes.addFlashAttribute("error", "Invalid status: " + newStatus);
//        } catch (Exception e) {
//            logger.error("Error changing event status: ", e);
//            redirectAttributes.addFlashAttribute("error", "Error changing event status: " + e.getMessage());
//        }
//        return "redirect:/Admin/Events";
//    }

    // 6. TOGGLE APPROVAL STATUS
    @PostMapping("/ToggleApprovalStatus/{id}")
    public String toggleApprovalStatus(@PathVariable("id") Integer eventId,
                                       @RequestParam("newApprovalStatus") String newApprovalStatus,
                                       RedirectAttributes redirectAttributes) {
        try {
            Event.ApprovalStatus approvalStatus = Event.ApprovalStatus.valueOf(newApprovalStatus.toUpperCase());

            if (approvalStatus == Event.ApprovalStatus.APPROVED) {
                eventService.approveEvent(eventId, null);
                redirectAttributes.addFlashAttribute("success", "Event approved successfully");
            } else if (approvalStatus == Event.ApprovalStatus.REJECTED) {
                eventService.rejectEvent(eventId, null);
                redirectAttributes.addFlashAttribute("success", "Event rejected successfully");
            } else {
                // For PENDING status
                Event event = eventService.findById(eventId);
                if (event != null) {
                    event.setApprovalStatus(approvalStatus);
                    eventService.save(event);
                    redirectAttributes.addFlashAttribute("success", "Event set to pending status");
                }
            }
        } catch (Exception e) {
            logger.error("Error changing approval status: ", e);
            redirectAttributes.addFlashAttribute("error", "Error changing approval status: " + e.getMessage());
        }
        return "redirect:/Admin/Events";
    }
}

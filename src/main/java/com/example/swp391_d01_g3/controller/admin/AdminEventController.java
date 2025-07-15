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

@Controller
@RequestMapping("/Admin")
public class AdminEventController {

    @Autowired
    private IAdminEventService adminEventService;

    private static final Logger logger = LoggerFactory.getLogger(AdminEventController.class);

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

            // Parse status parameter
            if (status != null && !status.isEmpty()) {
                try {
                    approvalStatus = Event.ApprovalStatus.valueOf(status.toUpperCase());
                } catch (IllegalArgumentException e) {
                    logger.warn("Invalid status parameter: {}", status);
                }
            }

            // Search logic
            if (keyword != null && !keyword.trim().isEmpty() && approvalStatus != null) {
                eventPage = adminEventService.searchEventsByKeywordAndStatus(keyword.trim(), approvalStatus, page, size);
            } else if (keyword != null && !keyword.trim().isEmpty()) {
                eventPage = adminEventService.searchEventsByKeyword(keyword.trim(), page, size);
            } else if (approvalStatus != null) {
                eventPage = adminEventService.getEventsByStatus(approvalStatus, page, size);
            } else {
                eventPage = adminEventService.getAllEvents(page, size);
            }

            // THÊM: Lấy số lượng cho badges
            long totalEvents = adminEventService.getTotalEventsCount();
            long pendingEvents = adminEventService.getPendingCount();
            long approvedEvents = adminEventService.getApprovedCount();
            long rejectedEvents = adminEventService.getRejectedCount();

            model.addAttribute("eventList", eventPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", eventPage.getTotalPages());
            model.addAttribute("totalItems", eventPage.getTotalElements());
            model.addAttribute("hasNext", eventPage.hasNext());
            model.addAttribute("hasPrevious", eventPage.hasPrevious());
            model.addAttribute("selectedStatus", status);
            model.addAttribute("keyword", keyword);

            // THÊM: Số lượng cho badges
            model.addAttribute("totalEvents", totalEvents);
            model.addAttribute("pendingEvents", pendingEvents);
            model.addAttribute("approvedEvents", approvedEvents);
            model.addAttribute("rejectedEvents", rejectedEvents);

            return "admin/viewEvents";

        } catch (Exception e) {
            logger.error("Error loading events: ", e);
            model.addAttribute("error", "Error loading events: " + e.getMessage());

            // Safe defaults
            model.addAttribute("eventList", java.util.Collections.emptyList());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("totalItems", 0);
            model.addAttribute("hasNext", false);
            model.addAttribute("hasPrevious", false);
            model.addAttribute("selectedStatus", "");
            model.addAttribute("keyword", "");

            return "admin/viewEvents";
        }
    }

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
            } else {
                eventPage = adminEventService.getPendingEvents(page, size);
            }

            // THÊM: Lấy số lượng cho badges
            long totalEvents = adminEventService.getTotalEventsCount();
            long pendingEvents = adminEventService.getPendingCount();
            long approvedEvents = adminEventService.getApprovedCount();
            long rejectedEvents = adminEventService.getRejectedCount();

            model.addAttribute("eventList", eventPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", eventPage.getTotalPages());
            model.addAttribute("totalItems", eventPage.getTotalElements());
            model.addAttribute("hasNext", eventPage.hasNext());
            model.addAttribute("hasPrevious", eventPage.hasPrevious());
            model.addAttribute("keyword", keyword);

            // THÊM: Số lượng cho badges
            model.addAttribute("totalEvents", totalEvents);
            model.addAttribute("pendingEvents", pendingEvents);
            model.addAttribute("approvedEvents", approvedEvents);
            model.addAttribute("rejectedEvents", rejectedEvents);

            return "admin/pendingEvents";

        } catch (Exception e) {
            logger.error("Error loading pending events: ", e);
            model.addAttribute("error", "Error loading pending events: " + e.getMessage());
            return "admin/pendingEvents";
        }
    }


    @PostMapping("/ConfirmEvent/{id}")
    public String confirmEvent(@PathVariable("id") Integer eventId,
                               RedirectAttributes redirectAttributes) {
        try {
            adminEventService.confirmEvent(eventId);
            redirectAttributes.addFlashAttribute("success", "Event confirmed successfully");
        } catch (Exception e) {
            logger.error("Error confirming event: ", e);
            redirectAttributes.addFlashAttribute("error", "Error confirming event: " + e.getMessage());
        }
        return "redirect:/Admin/PendingEvents";
    }

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

    @PostMapping("/ChangeEventStatus/{id}")
    public String changeEventStatus(@PathVariable("id") Integer eventId,
                                    @RequestParam(value = "status", required = false) String status,
                                    RedirectAttributes redirectAttributes) {
        try {
            // Validate status parameter
            if (status == null || status.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Trạng thái không được để trống!");
                return "redirect:/Admin/PendingEvents";
            }

            Event.ApprovalStatus newStatus = Event.ApprovalStatus.valueOf(status.toUpperCase());

            if (newStatus == Event.ApprovalStatus.APPROVED) {
                adminEventService.confirmEvent(eventId);
            } else if (newStatus == Event.ApprovalStatus.REJECTED) {
                adminEventService.rejectEvent(eventId, "Status changed by admin");
            } else if (newStatus == Event.ApprovalStatus.PENDING) {
                // Set event back to pending status
                Event event = adminEventService.getEventById(eventId);
                if (event != null) {
                    event.setApprovalStatus(Event.ApprovalStatus.PENDING);
                    event.setApprovedAt(null);
                    event.setApprovedBy(null);
                    adminEventService.updateEvent(eventId, event);
                }
            }

            String message = String.format("Event status changed to %s successfully", newStatus);
            redirectAttributes.addFlashAttribute("success", message);

        } catch (Exception e) {
            logger.error("Error changing event status: ", e);
            redirectAttributes.addFlashAttribute("error", "Error changing event status: " + e.getMessage());
        }
        return "redirect:/Admin/Events";
    }

    @GetMapping("/Event/{id}")
    public String viewEventDetails(@PathVariable("id") Integer eventId, Model model) {
        try {
            Event event = adminEventService.getEventById(eventId);
            if (event == null) {
                return "redirect:/Admin/Events";
            }
            model.addAttribute("event", event);
            return "admin/viewEventDetails";
        } catch (Exception e) {
            logger.error("Error loading event details: ", e);
            return "redirect:/Admin/Events";
        }
    }
    @PostMapping("/ToggleApprovalStatus/{id}")
    public String toggleApprovalStatus(@PathVariable("id") Integer eventId,
                                       @RequestParam("newApprovalStatus") String newStatus,
                                       RedirectAttributes redirectAttributes) {
        try {
            Event.ApprovalStatus approvalStatus = Event.ApprovalStatus.valueOf(newStatus.toUpperCase());

            if (approvalStatus == Event.ApprovalStatus.APPROVED) {
                adminEventService.confirmEvent(eventId);
            } else if (approvalStatus == Event.ApprovalStatus.REJECTED) {
                adminEventService.rejectEvent(eventId, "Status changed by admin");
            } else if (approvalStatus == Event.ApprovalStatus.PENDING) {
                // Set event back to pending status
                Event event = adminEventService.getEventById(eventId);
                if (event != null) {
                    event.setApprovalStatus(Event.ApprovalStatus.PENDING);
                    event.setApprovedAt(null);
                    event.setApprovedBy(null);
                    adminEventService.updateEvent(eventId, event);
                }
            }

            String message = String.format("Event status changed to %s successfully", newStatus);
            redirectAttributes.addFlashAttribute("success", message);

        } catch (Exception e) {
            logger.error("Error toggling approval status: ", e);
            redirectAttributes.addFlashAttribute("error", "Error changing event status: " + e.getMessage());
        }
        return "redirect:/Admin/Events";
    }
}

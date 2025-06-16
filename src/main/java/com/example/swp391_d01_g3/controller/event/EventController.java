package com.example.swp391_d01_g3.controller.event;

import com.example.swp391_d01_g3.model.Event;
import com.example.swp391_d01_g3.model.EventForm;
import com.example.swp391_d01_g3.model.Student;
import com.example.swp391_d01_g3.service.event.IEventService;
import com.example.swp391_d01_g3.service.student.IStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/Events")
public class EventController {

    @Autowired
    private IEventService eventService;
    
    @Autowired
    private IStudentService studentService;

    /**
     * Hiển thị danh sách events với phân trang và tìm kiếm
     */
    @GetMapping
    public String eventsPage(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "6") int size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "status", required = false) String status,
            Model model) {
        
        try {
            // Tạo Pageable object với sắp xếp theo ngày tạo
            Pageable pageable = PageRequest.of(page, size, Sort.by("eventDate").ascending());
            
            // Lấy dữ liệu events
            Page<Event> eventsPage;
            
            if (search != null && !search.trim().isEmpty()) {
                // Tìm kiếm theo title hoặc description
                eventsPage = eventService.searchEvents(search.trim(), pageable);
                model.addAttribute("search", search);
            } else if (status != null && !status.trim().isEmpty()) {
                // Lọc theo status
                Event.ApprovalStatus approvalStatus = Event.ApprovalStatus.valueOf(status.toUpperCase());
                eventsPage = eventService.findByApprovalStatus(approvalStatus, pageable);
                model.addAttribute("selectedStatus", status);
            } else {
                // Chỉ hiển thị events đã được approve
                eventsPage = eventService.findByApprovalStatus(Event.ApprovalStatus.APPROVED, pageable);
            }
            
            // Lấy upcoming events cho sidebar
            List<Event> upcomingEvents = eventService.getUpcomingEvents(5);
            
            // Lấy event statistics cho categories
            long totalEvents = eventService.countApprovedEvents();
            long itEvents = eventService.countEventsByJobField("IT");
            long marketingEvents = eventService.countEventsByJobField("Marketing");
            long bankingEvents = eventService.countEventsByJobField("Banking");
            
            // Add data to model
            model.addAttribute("eventsPage", eventsPage);
            model.addAttribute("upcomingEvents", upcomingEvents);
            model.addAttribute("totalEvents", totalEvents);
            model.addAttribute("itEvents", itEvents);
            model.addAttribute("marketingEvents", marketingEvents);
            model.addAttribute("bankingEvents", bankingEvents);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", eventsPage.getTotalPages());
            
            return "events/eventsList";
            
        } catch (Exception e) {
            model.addAttribute("error", "Đã xảy ra lỗi khi tải danh sách sự kiện");
            return "events/eventsList";
        }
    }

    /**
     * Hiển thị chi tiết event
     */
    @GetMapping("/{eventId}")
    public String eventDetail(@PathVariable Integer eventId, Model model, Authentication authentication) {
        try {
            Event event = eventService.findById(eventId);
            if (event == null) {
                model.addAttribute("error", "Không tìm thấy sự kiện");
                return "error/404";
            }
            
            // Kiểm tra xem user đã đăng ký chưa
            boolean isRegistered = false;
            if (authentication != null && authentication.isAuthenticated()) {
                String email = authentication.getName();
                Student student = studentService.findByEmail(email);
                if (student != null) {
                    isRegistered = eventService.isStudentRegistered(eventId, student.getStudentId());
                }
            }
            
            // Lấy related events
            List<Event> relatedEvents = eventService.getRelatedEvents(eventId, 4);
            
            model.addAttribute("event", event);
            model.addAttribute("isRegistered", isRegistered);
            model.addAttribute("relatedEvents", relatedEvents);
            
            return "events/eventDetail";
            
        } catch (Exception e) {
            model.addAttribute("error", "Đã xảy ra lỗi khi tải chi tiết sự kiện");
            return "error/500";
        }
    }

    /**
     * Đăng ký tham gia event
     */
    @PostMapping("/{eventId}/register")
    @ResponseBody
    public String registerEvent(
            @PathVariable Integer eventId,
            @RequestParam(required = false) String notes,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return "error:Bạn cần đăng nhập để đăng ký sự kiện";
            }
            
            String email = authentication.getName();
            Student student = studentService.findByEmail(email);
            
            if (student == null) {
                return "error:Không tìm thấy thông tin sinh viên";
            }
            
            // Kiểm tra xem đã đăng ký chưa
            if (eventService.isStudentRegistered(eventId, student.getStudentId())) {
                return "error:Bạn đã đăng ký sự kiện này rồi";
            }
            
            // Kiểm tra event có thể đăng ký không
            Event event = eventService.findById(eventId);
            if (event == null) {
                return "error:Không tìm thấy sự kiện";
            }
            
            if (!event.canRegister()) {
                return "error:Sự kiện này không thể đăng ký";
            }
            
            // Đăng ký
            EventForm eventForm = new EventForm();
            eventForm.setEvent(event);
            eventForm.setStudent(student);
            eventForm.setNotes(notes);
            
            eventService.registerEvent(eventForm);
            
            return "success:Đăng ký sự kiện thành công!";
            
        } catch (Exception e) {
            return "error:Đã xảy ra lỗi khi đăng ký sự kiện";
        }
    }

    /**
     * Hủy đăng ký event
     */
    @PostMapping("/{eventId}/unregister")
    @ResponseBody
    public String unregisterEvent(@PathVariable Integer eventId, Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return "error:Bạn cần đăng nhập";
            }
            
            String email = authentication.getName();
            Student student = studentService.findByEmail(email);
            
            if (student == null) {
                return "error:Không tìm thấy thông tin sinh viên";
            }
            
            eventService.unregisterEvent(eventId, student.getStudentId());
            return "success:Hủy đăng ký thành công!";
            
        } catch (Exception e) {
            return "error:Đã xảy ra lỗi khi hủy đăng ký";
        }
    }
} 
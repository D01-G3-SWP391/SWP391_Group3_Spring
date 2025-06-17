package com.example.swp391_d01_g3.controller.event;

import com.example.swp391_d01_g3.dto.EventRegistrationDTO;
import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.Event;
import com.example.swp391_d01_g3.model.EventForm;
import com.example.swp391_d01_g3.model.Student;
import com.example.swp391_d01_g3.service.event.IEventService;
import com.example.swp391_d01_g3.service.security.IAccountService;
import com.example.swp391_d01_g3.service.student.IStudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private IAccountService accountService;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
            @Valid @RequestBody EventRegistrationDTO registrationDTO,
            Authentication authentication) {
        
        try {
            // Log request data
            System.out.println("Received registration request for event: " + eventId);
            System.out.println("Registration data: " + registrationDTO.toString());
            System.out.println("Authentication: " + (authentication != null ? authentication.getName() : "null"));
            
            if (authentication == null || !authentication.isAuthenticated()) {
                System.out.println("Authentication check failed");
                return "error:Bạn cần đăng nhập để đăng ký sự kiện";
            }
            
            // Kiểm tra event có thể đăng ký không
            Event event = eventService.findById(eventId);
            if (event == null) {
                System.out.println("Event not found: " + eventId);
                return "error:Không tìm thấy sự kiện";
            }
            
            System.out.println("Event found: " + event.getEventTitle());
            System.out.println("Event status: " + event.getEventStatus());
            System.out.println("Event approval status: " + event.getApprovalStatus());
            System.out.println("Current participants: " + event.getCurrentParticipants());
            System.out.println("Max participants: " + event.getMaxParticipants());
            
            if (!event.canRegister()) {
                System.out.println("Event cannot be registered");
                return "error:Sự kiện này không thể đăng ký";
            }

            // Kiểm tra email đã đăng ký chưa
            Student existingStudent = studentService.findByEmail(authentication.getName());
            System.out.println("Existing student check - Email: " + authentication.getName());
            System.out.println("Existing student found: " + (existingStudent != null));
            
            if (existingStudent != null) {
                if (eventService.isStudentRegistered(eventId, existingStudent.getStudentId())) {
                    System.out.println("Student already registered for this event");
                    return "error:Bạn đã đăng ký tham gia sự kiện này rồi";
                }
                
                // Cập nhật thông tin Student nếu cần
                boolean needUpdate = false;
                
                if (!existingStudent.getAccount().getFullName().equals(registrationDTO.getFullName())) {
                    existingStudent.getAccount().setFullName(registrationDTO.getFullName());
                    needUpdate = true;
                }
                
                if (!existingStudent.getAccount().getPhone().equals(registrationDTO.getPhone())) {
                    existingStudent.getAccount().setPhone(registrationDTO.getPhone());
                    needUpdate = true;
                }
                
                if (!existingStudent.getUniversity().equals(registrationDTO.getOrganization())) {
                    existingStudent.setUniversity(registrationDTO.getOrganization());
                    needUpdate = true;
                }
                
                if (needUpdate) {
                    accountService.save(existingStudent.getAccount());
                    studentService.save(existingStudent);
                    System.out.println("Updated student information");
                }
                
                // Đăng ký event
                EventForm eventForm = new EventForm();
                eventForm.setEvent(event);
                eventForm.setStudent(existingStudent);
                eventForm.setNotes(registrationDTO.getNotes());
                
                try {
                    eventService.registerEvent(eventForm);
                    System.out.println("Successfully registered event");
                    return "success:Đăng ký sự kiện thành công!";
                } catch (Exception e) {
                    System.err.println("Error registering event:");
                    e.printStackTrace();
                    return "error:Đã xảy ra lỗi khi đăng ký: " + e.getMessage();
                }
            } else {
                return "error:Không tìm thấy thông tin sinh viên của bạn trong hệ thống";
            }
            
        } catch (Exception e) {
            // Log error details
            System.err.println("Error processing registration:");
            e.printStackTrace();
            return "error:Đã xảy ra lỗi khi đăng ký sự kiện: " + e.getMessage();
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
package com.example.swp391_d01_g3.controller.event;

import com.example.swp391_d01_g3.dto.EventRegistrationDTO;
import com.example.swp391_d01_g3.model.Account;
import com.example.swp391_d01_g3.model.Event;
import com.example.swp391_d01_g3.model.EventForm;
import com.example.swp391_d01_g3.model.Student;
import com.example.swp391_d01_g3.service.event.IEventService;
import com.example.swp391_d01_g3.service.notification.INotificationService;
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

import java.security.Principal;
import java.time.LocalDateTime;
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
    private INotificationService notificationService;



    /**
     * Hiển thị danh sách events với phân trang và tìm kiếm
     */
    @GetMapping
    public String eventsPage(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "6") int size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "error", required = false) String error,
            Model model,
            Authentication authentication, Principal principal) {
        if (principal != null) {
            String email = principal.getName();
            model.addAttribute("userEmail", email);
            Account account = accountService.findByEmail(email);
            model.addAttribute("account", account);
        }
        
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
                // Chỉ hiển thị events đã được approve và active
                eventsPage = eventService.findActiveApprovedEvents(LocalDateTime.now(), pageable);
                
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
            
            // Thêm: truyền registeredEventIds và thông tin student nếu đã đăng nhập
            if (authentication != null && authentication.isAuthenticated()) {
                String email = authentication.getName();
                Student student = studentService.findByEmail(email);
                if (student != null) {
                    List<Integer> registeredEventIds = eventService.findRegisteredEventIdsByStudentId(student.getStudentId());
                    model.addAttribute("registeredEventIds", registeredEventIds);
                    
                    // Truyền thông tin student để điền sẵn form
                    model.addAttribute("currentStudent", student);
                }
            }
            
            // Xử lý error messages
            if (error != null) {
                switch (error) {
                    case "not-found":
                        model.addAttribute("errorMessage", "Không tìm thấy sự kiện bạn đang tìm kiếm.");
                        break;
                    case "server-error":
                        model.addAttribute("errorMessage", "Đã xảy ra lỗi khi tải chi tiết sự kiện. Vui lòng thử lại sau.");
                        break;
                    default:
                        model.addAttribute("errorMessage", "Đã xảy ra lỗi không xác định.");
                        break;
                }
            }
            
            return "events/eventPost";
            
        } catch (Exception e) {
            model.addAttribute("error", "Đã xảy ra lỗi khi tải danh sách sự kiện");
            return "events/eventPost";
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
                // Redirect về trang danh sách events với thông báo lỗi
                return "redirect:/Events?error=not-found";
            }
            
            // Kiểm tra xem user đã đăng ký chưa
            boolean isRegistered = false;
            if (authentication != null && authentication.isAuthenticated()) {
                String email = authentication.getName();
                Student student = studentService.findByEmail(email);
                if (student != null) {
                    isRegistered = eventService.isStudentRegistered(eventId, student.getStudentId());
                    // Truyền thông tin student để điền sẵn form
                    model.addAttribute("currentStudent", student);
                }
            }
            
            // Lấy related events
            List<Event> relatedEvents = eventService.getRelatedEvents(eventId, 4);
            
            model.addAttribute("event", event);
            model.addAttribute("isRegistered", isRegistered);
            model.addAttribute("relatedEvents", relatedEvents);
            
            return "events/eventDetail";
            
        } catch (Exception e) {
            e.printStackTrace(); // Log lỗi để debug
            // Redirect về trang danh sách events với thông báo lỗi
            return "redirect:/Events?error=server-error";
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
//            // Log request data
//            System.out.println("Received registration request for event: " + eventId);
//            System.out.println("Registration data: " + registrationDTO.toString());
//            System.out.println("Authentication: " + (authentication != null ? authentication.getName() : "null"));
            
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
            
//            System.out.println("Event found: " + event.getEventTitle());
//            System.out.println("Event status: " + event.getEventStatus());
//            System.out.println("Event approval status: " + event.getApprovalStatus());
//            System.out.println("Current participants: " + event.getCurrentParticipants());
//            System.out.println("Max participants: " + event.getMaxParticipants());
            
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
                
                // Kiểm tra và cập nhật fullName với null safety
                String currentFullName = existingStudent.getAccount().getFullName();
                if (!java.util.Objects.equals(currentFullName, registrationDTO.getFullName())) {
                    existingStudent.getAccount().setFullName(registrationDTO.getFullName());
                    needUpdate = true;
                }
                
                // Kiểm tra và cập nhật phone với null safety
                String currentPhone = existingStudent.getAccount().getPhone();
                if (!java.util.Objects.equals(currentPhone, registrationDTO.getPhone())) {
                    existingStudent.getAccount().setPhone(registrationDTO.getPhone());
                    needUpdate = true;
                }
                
                // Kiểm tra và cập nhật university với null safety
                String currentUniversity = existingStudent.getUniversity();
                if (!java.util.Objects.equals(currentUniversity, registrationDTO.getOrganization())) {
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
                    notificationService.createNotification(
                            existingStudent.getAccount(),
                            "Register Event Submitted",
                            "Your register for " + event.getEventTitle() + " has been submitted successfully.",
                            "JOB_APPLICATION",
                            event.getEventId().longValue()

                    );

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

} 
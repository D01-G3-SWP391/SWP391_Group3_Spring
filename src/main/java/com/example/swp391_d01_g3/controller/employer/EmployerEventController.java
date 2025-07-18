package com.example.swp391_d01_g3.controller.employer;


import com.example.swp391_d01_g3.model.Employer;
import com.example.swp391_d01_g3.model.Event;
import com.example.swp391_d01_g3.service.employer.IEmployerService;
import com.example.swp391_d01_g3.service.event.IEventService;
import com.example.swp391_d01_g3.service.security.IAccountService;
import com.example.swp391_d01_g3.dto.EventCreateDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/Employer/Events")
public class EmployerEventController {
    @Autowired
    private IEventService eventService;
    @Autowired
    private IEmployerService employerService;
    @Autowired
    private IAccountService accountService;

    // List events của employer
    @GetMapping("")
    public String listEvents(@RequestParam(value = "page", defaultValue = "0") int page,
                             @RequestParam(value = "size", defaultValue = "3") int size,
                             Principal principal,
                             Model model) {
        String email = principal.getName();
        Employer employer = employerService.findByEmail(email);
        // Thêm account cho navbar
        model.addAttribute("account", accountService.findByEmail(email));
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Event> eventsPage = eventService.findByEmployer(employer, pageable);
        
        // Calculate statistics from ALL events of this employer (not just current page)
        List<Event> allEmployerEvents = eventService.findAllEventsByEmployer(employer);
        
        long totalEvents = allEmployerEvents.size();
        long activeEvents = allEmployerEvents.stream()
                .filter(event -> event.getEventStatus() == Event.EventStatus.ACTIVE)
                .count();
        long pendingEvents = allEmployerEvents.stream()
                .filter(event -> event.getApprovalStatus() == Event.ApprovalStatus.PENDING)
                .count();
        
        model.addAttribute("eventsPage", eventsPage);
        model.addAttribute("totalEvents", totalEvents);
        model.addAttribute("activeEvents", activeEvents);
        model.addAttribute("pendingEvents", pendingEvents);
        
        return "employee/eventList";
    }

    // Show create event form
    @GetMapping("/Create")
    public String showCreateForm(Principal principal, Model model) {
        EventCreateDTO eventCreateDTO = new EventCreateDTO();
        if (principal != null) {
            String email = principal.getName();
            eventCreateDTO.setContactEmail(email);
            model.addAttribute("currentEmail", email);
            model.addAttribute("account", accountService.findByEmail(principal.getName()));
        }
        model.addAttribute("event", eventCreateDTO);
        return "employee/createEvent";
    }

    // Handle create event
    @PostMapping("/Create")
    public String createEvent(@ModelAttribute("event") @Valid EventCreateDTO eventDto,
                              BindingResult result,
                              Principal principal,
                              Model model,
                              RedirectAttributes ra) {
        
        // Custom validation: Hạn đăng ký phải nhỏ hơn ngày tổ chức
        if (eventDto.getRegistrationDeadline() != null && eventDto.getEventDate() != null) {
            if (eventDto.getRegistrationDeadline().isAfter(eventDto.getEventDate()) || 
                eventDto.getRegistrationDeadline().isEqual(eventDto.getEventDate())) {
                result.rejectValue("registrationDeadline", "error.registrationDeadline", 
                    "Hạn đăng ký phải nhỏ hơn ngày tổ chức sự kiện");
            }
        }
        
        if (result.hasErrors()) {
            // Thêm account cho navbar trong trường hợp lỗi
            model.addAttribute("account", accountService.findByEmail(principal.getName()));
            return "employee/createEvent";
        }
        String email = principal.getName();
        Employer employer = employerService.findByEmail(email);
        Event event = new Event();
        event.setEmployer(employer);
        event.setCreatedAt(LocalDateTime.now());
        event.setEventStatus(Event.EventStatus.ACTIVE);
        event.setApprovalStatus(Event.ApprovalStatus.PENDING);
        event.setCurrentParticipants(0);
        event.setEventTitle(eventDto.getEventTitle());
        event.setEventDescription(eventDto.getEventDescription());
        event.setEventDate(eventDto.getEventDate());
        event.setRegistrationDeadline(eventDto.getRegistrationDeadline());
        event.setEventLocation(eventDto.getEventLocation());
        event.setMaxParticipants(eventDto.getMaxParticipants());
        event.setContactEmail(eventDto.getContactEmail());
        eventService.save(event);
        ra.addFlashAttribute("successMsg", "Tạo sự kiện thành công!");
        return "redirect:/Employer/Events";
    }

    // Show edit event form
    @GetMapping("/Edit/{eventId}")
    public String showEditForm(@PathVariable Integer eventId, Principal principal, Model model, RedirectAttributes ra) {
        String email = principal.getName();
        Employer employer = employerService.findByEmail(email);
        Event event = eventService.findById(eventId);
        if (event == null || !event.getEmployer().getEmployerId().equals(employer.getEmployerId())) {
            ra.addFlashAttribute("errorMsg", "Không tìm thấy sự kiện hoặc bạn không có quyền chỉnh sửa!");
            return "redirect:/Employer/Events";
        }
        // Chuyển Event sang EventCreateDTO
        EventCreateDTO dto = new EventCreateDTO();
        dto.setEventTitle(event.getEventTitle());
        dto.setEventDescription(event.getEventDescription());
        dto.setEventDate(event.getEventDate());
        dto.setRegistrationDeadline(event.getRegistrationDeadline());
        dto.setEventLocation(event.getEventLocation());
        dto.setMaxParticipants(event.getMaxParticipants());
        dto.setContactEmail(event.getContactEmail());
        model.addAttribute("event", dto);
        model.addAttribute("eventId", eventId);
        // Thêm account cho navbar
        model.addAttribute("account", accountService.findByEmail(email));
        return "employee/editEvent";
    }

    // Handle update event
    @PostMapping("/Edit/{eventId}")
    public String updateEvent(@PathVariable Integer eventId,
                              @ModelAttribute("event") @Valid EventCreateDTO eventDto,
                              BindingResult result,
                              Principal principal,
                              Model model,
                              RedirectAttributes ra) {
        String email = principal.getName();
        Employer employer = employerService.findByEmail(email);
        Event event = eventService.findById(eventId);
        if (event == null || !event.getEmployer().getEmployerId().equals(employer.getEmployerId())) {
            ra.addFlashAttribute("errorMsg", "Không tìm thấy sự kiện hoặc bạn không có quyền chỉnh sửa!");
            return "redirect:/Employer/Events";
        }
        
        // Custom validation: Hạn đăng ký phải nhỏ hơn ngày tổ chức
        if (eventDto.getRegistrationDeadline() != null && eventDto.getEventDate() != null) {
            if (eventDto.getRegistrationDeadline().isAfter(eventDto.getEventDate()) || 
                eventDto.getRegistrationDeadline().isEqual(eventDto.getEventDate())) {
                result.rejectValue("registrationDeadline", "error.registrationDeadline", 
                    "Hạn đăng ký phải nhỏ hơn ngày tổ chức sự kiện");
            }
        }
        
        if (result.hasErrors()) {
            model.addAttribute("eventId", eventId);
            // Thêm account cho navbar trong trường hợp lỗi
            model.addAttribute("account", accountService.findByEmail(email));
            return "employee/editEvent";
        }
        // Cập nhật các trường cho phép
        event.setEventTitle(eventDto.getEventTitle());
        event.setEventDescription(eventDto.getEventDescription());
        event.setEventDate(eventDto.getEventDate());
        event.setEventLocation(eventDto.getEventLocation());
        event.setRegistrationDeadline(eventDto.getRegistrationDeadline());
        event.setMaxParticipants(eventDto.getMaxParticipants());
        event.setContactEmail(eventDto.getContactEmail());
        eventService.save(event);
        ra.addFlashAttribute("successMsg", "Cập nhật sự kiện thành công!");
        return "redirect:/Employer/Events";
    }

    // Cancel event
    @PostMapping("/Cancel/{eventId}")
    public String cancelEvent(@PathVariable Integer eventId, Principal principal, RedirectAttributes ra) {
        String email = principal.getName();
        Employer employer = employerService.findByEmail(email);
        Event event = eventService.findById(eventId);
        if (event == null || !event.getEmployer().getEmployerId().equals(employer.getEmployerId())) {
            ra.addFlashAttribute("errorMsg", "Không tìm thấy sự kiện hoặc bạn không có quyền hủy!");
            return "redirect:/Employer/Events";
        }
        event.setEventStatus(Event.EventStatus.CANCELLED);
        eventService.save(event);
        ra.addFlashAttribute("successMsg", "Đã hủy sự kiện thành công!");
        return "redirect:/Employer/Events";
    }

    // Hide event (soft delete)
    @PostMapping("/Hide/{eventId}")
    public String hideEvent(@PathVariable Integer eventId, Principal principal, RedirectAttributes ra) {
        String email = principal.getName();
        Employer employer = employerService.findByEmail(email);
        Event event = eventService.findById(eventId);
        
        // Kiểm tra event tồn tại và thuộc về employer
        if (event == null || !event.getEmployer().getEmployerId().equals(employer.getEmployerId())) {
            ra.addFlashAttribute("errorMsg", "Không tìm thấy sự kiện hoặc bạn không có quyền ẩn!");
            return "redirect:/Employer/Events";
        }
        
        // Đổi status thành INACTIVE
        event.setEventStatus(Event.EventStatus.INACTIVE);
        eventService.save(event);
        ra.addFlashAttribute("successMsg", "Đã ẩn sự kiện thành công!");
        
        return "redirect:/Employer/Events";
    }

    // Unhide event (show again)
    @PostMapping("/Unhide/{eventId}")
    public String unhideEvent(@PathVariable Integer eventId, Principal principal, RedirectAttributes ra) {
        String email = principal.getName();
        Employer employer = employerService.findByEmail(email);
        Event event = eventService.findById(eventId);
        
        // Kiểm tra event tồn tại và thuộc về employer
        if (event == null || !event.getEmployer().getEmployerId().equals(employer.getEmployerId())) {
            ra.addFlashAttribute("errorMsg", "Không tìm thấy sự kiện hoặc bạn không có quyền hiện!");
            return "redirect:/Employer/Events";
        }
        
        // Đổi status thành ACTIVE
        event.setEventStatus(Event.EventStatus.ACTIVE);
        eventService.save(event);
        ra.addFlashAttribute("successMsg", "Đã hiện sự kiện thành công!");
        
        return "redirect:/Employer/Events";
    }

    // View event details
    @GetMapping("/Details/{eventId}")
    public String viewEventDetails(@PathVariable Integer eventId, Principal principal, Model model, RedirectAttributes ra) {
        String email = principal.getName();
        Employer employer = employerService.findByEmail(email);
        Event event = eventService.findById(eventId);
        
        // Kiểm tra event tồn tại và thuộc về employer
        if (event == null || !event.getEmployer().getEmployerId().equals(employer.getEmployerId())) {
            ra.addFlashAttribute("errorMsg", "Không tìm thấy sự kiện hoặc bạn không có quyền xem!");
            return "redirect:/Employer/Events";
        }
        
        model.addAttribute("event", event);
        return "employee/eventDetails";
    }
} 
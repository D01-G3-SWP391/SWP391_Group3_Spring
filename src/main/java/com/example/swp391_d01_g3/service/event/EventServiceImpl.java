package com.example.swp391_d01_g3.service.event;

import com.example.swp391_d01_g3.model.Event;
import com.example.swp391_d01_g3.model.EventForm;
import com.example.swp391_d01_g3.repository.IEventFormRepository;
import com.example.swp391_d01_g3.repository.IEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EventServiceImpl implements IEventService {

    @Autowired
    private IEventRepository eventRepository;

    @Autowired
    private IEventFormRepository eventFormRepository;

    private static final Logger logger = LoggerFactory.getLogger(EventServiceImpl.class);

    @Override
    public Page<Event> findByApprovalStatus(Event.ApprovalStatus status, Pageable pageable) {
        return eventRepository.findByApprovalStatusOrderByEventDateAsc(status, pageable);
    }

    @Override
    public Page<Event> searchEvents(String keyword, Pageable pageable) {
        return eventRepository.findByApprovalStatusAndEventTitleContainingIgnoreCaseOrEventDescriptionContainingIgnoreCase(
                Event.ApprovalStatus.APPROVED, keyword, keyword, pageable);
    }

    // THÊM: Search events theo keyword và status
    @Override
    public Page<Event> searchEventsByKeywordAndStatus(String keyword, Event.ApprovalStatus status, Pageable pageable) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return eventRepository.findByApprovalStatusAndEventTitleContainingIgnoreCaseOrEventDescriptionContainingIgnoreCase(
                    status, keyword, keyword, pageable);
        } else {
            return eventRepository.findByApprovalStatusOrderByEventDateAsc(status, pageable);
        }
    }

    @Override
    public List<Event> getUpcomingEvents(int limit) {
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, limit, Sort.by("eventDate").ascending());
        return eventRepository.findByApprovalStatusAndEventDateAfterOrderByEventDateAsc(
                Event.ApprovalStatus.APPROVED, now, pageable).getContent();
    }

    @Override
    public Event findById(Integer eventId) {
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        return eventOpt.orElse(null);
    }

    @Override
    public List<Event> getRelatedEvents(Integer eventId, int limit) {
        Event currentEvent = findById(eventId);
        if (currentEvent == null) {
            return List.of();
        }

        Pageable pageable = PageRequest.of(0, limit);
        return eventRepository.findRelatedEvents(
                eventId,
                currentEvent.getEmployer().getEmployerId(),
                Event.ApprovalStatus.APPROVED,
                pageable);
    }

    @Override
    public boolean isStudentRegistered(Integer eventId, Integer studentId) {
        return eventFormRepository.existsByEventEventIdAndStudentStudentId(eventId, studentId);
    }

    @Override
    public void registerEvent(EventForm eventForm) {
        // Lưu đăng ký
        eventFormRepository.save(eventForm);

        // Tăng số lượng participants
        Event event = eventForm.getEvent();
        event.incrementParticipants();
        eventRepository.save(event);
    }

    @Override
    public void unregisterEvent(Integer eventId, Integer studentId) {
        Optional<EventForm> eventFormOpt = eventFormRepository.findByEventEventIdAndStudentStudentId(eventId, studentId);

        if (eventFormOpt.isPresent()) {
            EventForm eventForm = eventFormOpt.get();
            Event event = eventForm.getEvent();

            // Xóa đăng ký
            eventFormRepository.delete(eventForm);

            // Giảm số lượng participants
            event.decrementParticipants();
            eventRepository.save(event);
        }
    }

    @Override
    public long countApprovedEvents() {
        return eventRepository.countByApprovalStatus(Event.ApprovalStatus.APPROVED);
    }

    // THÊM: Count events theo status
    @Override
    public long countEventsByStatus(Event.ApprovalStatus status) {
        return eventRepository.countByApprovalStatus(status);
    }

    @Override
    public long countEventsByJobField(String jobFieldName) {
        return eventRepository.countEventsByJobField(jobFieldName, Event.ApprovalStatus.APPROVED);
    }

    @Override
    public Event save(Event event) {
        return eventRepository.save(event);
    }

    // SỬA: Delete method với cascade delete EventForm
    // SỬA: Delete method với cascade delete EventForm
    @Override
    public void delete(Integer eventId) {
        try {
            // Lấy số lượng registrations trước khi xóa
            long registrationCount = eventFormRepository.countByEventEventId(eventId);

            // XÓA TẤT CẢ EVENT_FORM TRƯỚC
            if (registrationCount > 0) {
                List<EventForm> eventForms = eventFormRepository.findByEventEventId(eventId);
                eventFormRepository.deleteAll(eventForms);
                logger.info("Deleted {} event registrations for event ID: {}", registrationCount, eventId);
            }

            // SAU ĐÓ XÓA EVENT
            eventRepository.deleteById(eventId);
            logger.info("Successfully deleted event with ID: {}", eventId);
        } catch (Exception e) {
            logger.error("Error deleting event with ID: {}", eventId, e);
            throw new RuntimeException("Error deleting event: " + e.getMessage());
        }
    }

    // THÊM: Method sử dụng @Modifying query (hiệu quả hơn)
    @Override
    public void deleteEventWithRegistrations(Integer eventId) {
        try {
            Event event = findById(eventId);
            if (event == null) {
                throw new RuntimeException("Event not found with ID: " + eventId);
            }

            // Đếm số registrations trước khi xóa
            long registrationCount = eventFormRepository.countByEventEventId(eventId);

            // Xóa tất cả registrations bằng @Modifying query
            if (registrationCount > 0) {
                eventFormRepository.deleteByEventEventId(eventId);
                logger.info("Deleted {} registrations for event: {}", registrationCount, event.getEventTitle());
            }

            // Xóa event
            eventRepository.deleteById(eventId);
            logger.info("Successfully deleted event: {} (ID: {}) with {} registrations",
                    event.getEventTitle(), eventId, registrationCount);
        } catch (Exception e) {
            logger.error("Error deleting event with registrations - ID: {}", eventId, e);
            throw new RuntimeException("Error deleting event: " + e.getMessage());
        }
    }


    @Override
    public Page<Event> findAll(Pageable pageable) {
        return eventRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    @Override
    public void approveEvent(Integer eventId, Integer approvedById) {
        Event event = findById(eventId);
        if (event != null) {
            event.setApprovalStatus(Event.ApprovalStatus.APPROVED);
            event.setApprovedAt(LocalDateTime.now());
            eventRepository.save(event);
        }
    }

    @Override
    public void rejectEvent(Integer eventId, Integer rejectedById) {
        Event event = findById(eventId);
        if (event != null) {
            event.setApprovalStatus(Event.ApprovalStatus.REJECTED);
            event.setApprovedAt(LocalDateTime.now());
            eventRepository.save(event);
        }
    }

    @Override
    public List<Event> findByEmployerId(Integer employerId) {
        return eventRepository.findByEmployer_EmployerIdOrderByEventDateDesc(employerId);
    }

    @Override
    public Page<Event> findByEmployerId(Integer employerId, Pageable pageable) {
        return eventRepository.findByEmployer_EmployerIdOrderByEventDateDesc(employerId, pageable);
    }
}

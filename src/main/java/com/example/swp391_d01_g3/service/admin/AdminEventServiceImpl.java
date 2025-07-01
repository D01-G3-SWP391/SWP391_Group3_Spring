package com.example.swp391_d01_g3.service.admin;

import com.example.swp391_d01_g3.model.Event;
import com.example.swp391_d01_g3.repository.IEventRepository;
import com.example.swp391_d01_g3.service.event.IEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AdminEventServiceImpl implements IAdminEventService {

    @Autowired
    private IEventService eventService;

    @Autowired
    private IEventRepository eventRepository;

    private static final Logger logger = LoggerFactory.getLogger(AdminEventServiceImpl.class);

    @Override
    public Page<Event> getPendingEvents(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return eventService.findByApprovalStatus(Event.ApprovalStatus.PENDING, pageable);
        } catch (Exception e) {
            logger.error("Error getting pending events: ", e);
            throw new RuntimeException("Error getting pending events: " + e.getMessage());
        }
    }

    @Override
    public Page<Event> searchPendingEvents(String keyword, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return eventRepository.searchEventsByKeywordAndStatus(keyword.trim(), Event.ApprovalStatus.PENDING, pageable);
        } catch (Exception e) {
            logger.error("Error searching pending events: ", e);
            throw new RuntimeException("Error searching pending events: " + e.getMessage());
        }
    }

    @Override
    public void confirmEvent(Integer eventId) {
        try {
            eventService.approveEvent(eventId, null);
            logger.info("Successfully confirmed event with ID: {}", eventId);
        } catch (Exception e) {
            logger.error("Error confirming event: ", e);
            throw new RuntimeException("Error confirming event: " + e.getMessage());
        }
    }

    @Override
    public void rejectEvent(Integer eventId, String reason) {
        try {
            eventService.rejectEvent(eventId, null);
            logger.info("Successfully rejected event with ID: {}, reason: {}", eventId, reason);
        } catch (Exception e) {
            logger.error("Error rejecting event: ", e);
            throw new RuntimeException("Error rejecting event: " + e.getMessage());
        }
    }

    @Override
    public Event getEventForConfirmation(Integer eventId) {
        return eventService.findById(eventId);
    }

    @Override
    public Page<Event> getAllEvents(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return eventRepository.findAllByOrderByCreatedAtDesc(pageable);
        } catch (Exception e) {
            logger.error("Error getting all events: ", e);
            throw new RuntimeException("Error getting all events: " + e.getMessage());
        }
    }

    @Override
    public Page<Event> searchAllEvents(String keyword, Event.ApprovalStatus status, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);

            if (keyword != null && !keyword.trim().isEmpty() && status != null) {
                return eventRepository.searchEventsByKeywordAndStatus(keyword.trim(), status, pageable);
            } else if (keyword != null && !keyword.trim().isEmpty()) {
                return eventRepository.searchEventsByKeyword(keyword.trim(), pageable);
            } else if (status != null) {
                return eventRepository.findByApprovalStatusOrderByCreatedAtDesc(status, pageable);
            } else {
                return eventRepository.findAllByOrderByCreatedAtDesc(pageable);
            }
        } catch (Exception e) {
            logger.error("Error searching events: ", e);
            throw new RuntimeException("Error searching events: " + e.getMessage());
        }
    }

    // THÊM: Search methods
    @Override
    public Page<Event> searchEventsByKeyword(String keyword, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return eventRepository.searchEventsByKeyword(keyword.trim(), pageable);
        } catch (Exception e) {
            logger.error("Error searching events by keyword: ", e);
            throw new RuntimeException("Error searching events by keyword: " + e.getMessage());
        }
    }

    @Override
    public Page<Event> searchEventsByKeywordAndStatus(String keyword, Event.ApprovalStatus status, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return eventRepository.searchEventsByKeywordAndStatus(keyword.trim(), status, pageable);
        } catch (Exception e) {
            logger.error("Error searching events by keyword and status: ", e);
            throw new RuntimeException("Error searching events by keyword and status: " + e.getMessage());
        }
    }

    @Override
    public Page<Event> getEventsByStatus(Event.ApprovalStatus status, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return eventRepository.findByApprovalStatusOrderByCreatedAtDesc(status, pageable);
        } catch (Exception e) {
            logger.error("Error getting events by status: ", e);
            throw new RuntimeException("Error getting events by status: " + e.getMessage());
        }
    }

    @Override
    public Event getEventById(Integer eventId) {
        return eventService.findById(eventId);
    }

    @Override
    public Event updateEvent(Integer eventId, Event eventDetails) {
        try {
            Event existingEvent = eventService.findById(eventId);
            if (existingEvent == null) {
                throw new RuntimeException("Event not found with ID: " + eventId);
            }

            existingEvent.setEventTitle(eventDetails.getEventTitle());
            existingEvent.setEventDescription(eventDetails.getEventDescription());
            existingEvent.setEventDate(eventDetails.getEventDate());
            existingEvent.setEventLocation(eventDetails.getEventLocation());
            existingEvent.setRegistrationDeadline(eventDetails.getRegistrationDeadline());
            existingEvent.setMaxParticipants(eventDetails.getMaxParticipants());
            existingEvent.setContactEmail(eventDetails.getContactEmail());

            Event updatedEvent = eventService.save(existingEvent);
            logger.info("Successfully updated event with ID: {}", eventId);
            return updatedEvent;
        } catch (Exception e) {
            logger.error("Error updating event: ", e);
            throw new RuntimeException("Error updating event: " + e.getMessage());
        }
    }

    @Override
    public void deleteEvent(Integer eventId) {
        try {
            Event event = eventService.findById(eventId);
            if (event == null) {
                throw new RuntimeException("Event not found with ID: " + eventId);
            }

            eventService.delete(eventId);
            logger.info("Successfully deleted event with ID: {}", eventId);
        } catch (Exception e) {
            logger.error("Error deleting event: ", e);
            throw new RuntimeException("Error deleting event: " + e.getMessage());
        }
    }

    @Override
    public boolean canDeleteEvent(Integer eventId) {
        try {
            Event event = eventService.findById(eventId);
            if (event == null) {
                return false;
            }

            LocalDateTime now = LocalDateTime.now();

            return event.getEventStatus() == Event.EventStatus.CANCELLED ||
                    event.getEventDate().isBefore(now.minusDays(7)) ||
                    event.getApprovalStatus() == Event.ApprovalStatus.REJECTED;
        } catch (Exception e) {
            logger.error("Error checking if event can be deleted: ", e);
            return false;
        }
    }

    // THÊM: Count methods cho filter badges
    @Override
    public long getTotalEventsCount() {
        try {
            return eventRepository.count();
        } catch (Exception e) {
            logger.error("Error counting total events: ", e);
            throw new RuntimeException("Error counting total events: " + e.getMessage());
        }
    }

    @Override
    public long getPendingCount() {
        try {
            return eventRepository.countByApprovalStatus(Event.ApprovalStatus.PENDING);
        } catch (Exception e) {
            logger.error("Error counting pending events: ", e);
            return 0;
        }
    }

    @Override
    public long getApprovedCount() {
        try {
            return eventRepository.countByApprovalStatus(Event.ApprovalStatus.APPROVED);
        } catch (Exception e) {
            logger.error("Error counting approved events: ", e);
            return 0;
        }
    }

    @Override
    public long getRejectedCount() {
        try {
            return eventRepository.countByApprovalStatus(Event.ApprovalStatus.REJECTED);
        } catch (Exception e) {
            logger.error("Error counting rejected events: ", e);
            return 0;
        }
    }
}

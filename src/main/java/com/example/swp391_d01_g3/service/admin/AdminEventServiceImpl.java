package com.example.swp391_d01_g3.service.admin;

import com.example.swp391_d01_g3.model.Event;
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
            // Sử dụng existing search method từ eventService
            return eventService.searchEvents(keyword, pageable);
        } catch (Exception e) {
            logger.error("Error searching pending events: ", e);
            throw new RuntimeException("Error searching pending events: " + e.getMessage());
        }
    }

    @Override
    public void confirmEvent(Integer eventId) {
        try {
            eventService.approveEvent(eventId, null); // Admin ID có thể null
            logger.info("Successfully confirmed event with ID: {}", eventId);
        } catch (Exception e) {
            logger.error("Error confirming event: ", e);
            throw new RuntimeException("Error confirming event: " + e.getMessage());
        }
    }

    @Override
    public void rejectEvent(Integer eventId, String reason) {
        try {
            eventService.rejectEvent(eventId, null); // Admin ID có thể null
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
            return eventService.findAll(pageable);
        } catch (Exception e) {
            logger.error("Error getting all events: ", e);
            throw new RuntimeException("Error getting all events: " + e.getMessage());
        }
    }

    @Override
    public Page<Event> searchAllEvents(String keyword, Event.ApprovalStatus status, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);

            if (keyword != null && !keyword.trim().isEmpty()) {
                return eventService.searchEvents(keyword, pageable);
            } else if (status != null) {
                return eventService.findByApprovalStatus(status, pageable);
            } else {
                return eventService.findAll(pageable);
            }
        } catch (Exception e) {
            logger.error("Error searching events: ", e);
            throw new RuntimeException("Error searching events: " + e.getMessage());
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

            // Update fields
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

            // Can delete if:
            // 1. Event is cancelled
            // 2. Event is past (more than 7 days ago)
            // 3. Event is rejected
            return event.getEventStatus() == Event.EventStatus.CANCELLED ||
                    event.getEventDate().isBefore(now.minusDays(7)) ||
                    event.getApprovalStatus() == Event.ApprovalStatus.REJECTED;
        } catch (Exception e) {
            logger.error("Error checking if event can be deleted: ", e);
            return false;
        }
    }

    @Override
    public long getPendingCount() {
        return eventService.countApprovedEvents(); // Adjust method name as needed
    }

    @Override
    public long getApprovedCount() {
        return eventService.countApprovedEvents();
    }

    @Override
    public long getRejectedCount() {
        return eventService.countApprovedEvents(); // Adjust method name as needed
    }
}

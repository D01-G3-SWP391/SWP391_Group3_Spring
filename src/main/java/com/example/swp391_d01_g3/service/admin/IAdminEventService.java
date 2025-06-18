package com.example.swp391_d01_g3.service.admin;

import com.example.swp391_d01_g3.model.Event;
import org.springframework.data.domain.Page;

public interface IAdminEventService {
    // Confirm Career Events
    Page<Event> getPendingEvents(int page, int size);
    Page<Event> searchPendingEvents(String keyword, int page, int size);
    void confirmEvent(Integer eventId);
    void rejectEvent(Integer eventId, String reason);
    Event getEventForConfirmation(Integer eventId);

    // View Career Event List
    Page<Event> getAllEvents(int page, int size);
    Page<Event> searchAllEvents(String keyword, Event.ApprovalStatus status, int page, int size);
    Event getEventById(Integer eventId);

    // Update Career Event
    Event updateEvent(Integer eventId, Event eventDetails);

    // Delete Career Event
    void deleteEvent(Integer eventId);
    boolean canDeleteEvent(Integer eventId);

    // Statistics
    long getPendingCount();
    long getApprovedCount();
    long getRejectedCount();
}

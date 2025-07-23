package com.example.swp391_d01_g3.dto;

public class EventStatsDTO {
    private String eventTitle;
    private Long participantCount;
    private String eventStatus;
    private String eventDate;

    public EventStatsDTO() {
    }

    public EventStatsDTO(String eventTitle, Long participantCount, String eventStatus, String eventDate) {
        this.eventTitle = eventTitle;
        this.participantCount = participantCount;
        this.eventStatus = eventStatus;
        this.eventDate = eventDate;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public Long getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(Long participantCount) {
        this.participantCount = participantCount;
    }

    public String getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(String eventStatus) {
        this.eventStatus = eventStatus;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }
} 
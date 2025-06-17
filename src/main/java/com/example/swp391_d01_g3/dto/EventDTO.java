package com.example.swp391_d01_g3.dto;

import com.example.swp391_d01_g3.model.Event;

public class EventDTO {
    private Event event;
    private boolean registered;

    public EventDTO(Event event, boolean registered) {
        this.event = event;
        this.registered = registered;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }
} 
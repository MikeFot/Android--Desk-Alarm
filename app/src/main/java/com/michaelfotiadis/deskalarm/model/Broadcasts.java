package com.michaelfotiadis.deskalarm.model;

public enum Broadcasts {
    STEP_SERVICE_STOPPED("Service Stopped"),
    ALARM_STOPPED("Alarm Stopped"),
    ALARM_TRIGGERED("Alarm Triggered"),
    IDLE_DETECTED("Idle Detected"),
    DATA_CHANGED("Data Changed"),
    CLOCK_MODE_CHANGED("Clock Normal");

    private final String text;

    Broadcasts(final String description) {
        text = description;
    }

    public String getString() {
        return text;
    }

}
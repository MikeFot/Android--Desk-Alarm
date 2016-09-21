package com.michaelfotiadis.deskalarm.model;

public enum Payloads {
    PAYLOAD_1("Payload_1"),
    ALARM_PAYLOAD("Payload_5"),
    CLOCK_MODE_PAYLOAD("Payload_6");

    private final String text;

    Payloads(final String description) {
        text = description;
    }

    public String getString() {
        return text;
    }
}
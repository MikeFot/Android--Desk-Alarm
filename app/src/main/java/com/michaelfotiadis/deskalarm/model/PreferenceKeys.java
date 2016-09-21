package com.michaelfotiadis.deskalarm.model;

public enum PreferenceKeys {
    KEY_1("Key_1_Time_Started");

    private String text;

    PreferenceKeys(final String description) {
        text = description;
    }

    public String getString() {
        return text;
    }
}
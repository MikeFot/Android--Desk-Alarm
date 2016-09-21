package com.michaelfotiadis.deskalarm.model;

public enum Results {
    RESULT_1("Result_1"),// unused
    RESULT_2("Result_2"),// unused
    RESULT_3("Result_3");// unused

    private final String text;

    Results(final String description) {
        text = description;
    }

    public String getString() {
        return text;
    }
}
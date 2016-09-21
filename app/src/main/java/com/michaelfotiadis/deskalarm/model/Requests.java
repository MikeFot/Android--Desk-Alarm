package com.michaelfotiadis.deskalarm.model;

public enum Requests {
    REQUEST_CODE_1(1);

    private final int code;

    Requests(final int number) {
        code = number;
    }

    public int getCode() {
        return code;
    }
}
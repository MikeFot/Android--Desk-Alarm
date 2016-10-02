package com.michaelfotiadis.deskalarm.views;


public interface Clock {
    void updateTime();

    void pauseClock();

    void setTime(final int hours, final int minutes, final int seconds);

    void startClock(final long startTime, final long minutesToAlarm);

    void stopClock();

    boolean isVisible();

    long getTimeRunning();

    void setMinutesToAlarm(final long minutesToAlarm);

    void setSystemTime();
}

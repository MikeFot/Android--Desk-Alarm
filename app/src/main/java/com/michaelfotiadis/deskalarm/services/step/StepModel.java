package com.michaelfotiadis.deskalarm.services.step;

/**
 *
 */

public class StepModel {

    private int mStepCount;
    private int mTimesIdle;
    private long mStepValue;
    private long mTimeOfLastStep;
    private long mTimeStepInterval;

    protected StepModel() {
        mStepCount = 0;
        mTimesIdle = 0;
        mStepValue = 0;
        mTimeOfLastStep = 0;
        mTimeStepInterval = Long.MAX_VALUE;
    }

    public int getStepCount() {
        return mStepCount;
    }

    public void setStepCount(final int stepCount) {
        this.mStepCount = stepCount;
    }

    public int getTimesIdle() {
        return mTimesIdle;
    }

    public void setTimesIdle(final int timesIdle) {
        this.mTimesIdle = timesIdle;
    }

    public long getStepValue() {
        return mStepValue;
    }

    public void setStepValue(final long stepValue) {
        this.mStepValue = stepValue;
    }

    public long getTimeOfLastStep() {
        return mTimeOfLastStep;
    }

    public void setTimeOfLastStep(final long timeOfLastStep) {
        this.mTimeOfLastStep = timeOfLastStep;
    }

    public long getTimeStepInterval() {
        return mTimeStepInterval;
    }

    public void setTimeStepInterval(final long timeStepInterval) {
        this.mTimeStepInterval = timeStepInterval;
    }

    protected void resetTimesIdle() {
        mTimesIdle = 0;
    }

    protected void incrementStepCount() {
        mStepCount++;
    }

    protected void incrementTimesIdle() {
        mTimesIdle++;
    }

    protected void resetStepCount() {
        mStepCount = 0;
    }

    protected void updateTimeStepInterval() {
        mTimeStepInterval = System.currentTimeMillis() - mTimeOfLastStep;
    }
}

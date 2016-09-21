package com.michaelfotiadis.deskalarm.containers;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DecimalFormat;
import java.util.Arrays;

public class ErgoClockInstance implements Parcelable {

    private float mHour;
    private float mMinutes;
    private float mSeconds;

    public ErgoClockInstance() {
        // Set all to 0 on initial creation
        this.reset();
    }

    /**
     * Sets the values of the Clock Instance's parameters
     *
     * @param hours   float
     * @param minutes float
     * @param seconds float
     */
    public void setTime(final float hours, final float minutes, final float seconds) {
        this.mHour = hours;
        this.mMinutes = minutes;
        this.mSeconds = seconds;
    }

    @Override
    public int describeContents() {
        // Auto generate Parcelable method
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel out, final int flags) {
        out.writeFloat(mHour);
        out.writeFloat(mMinutes);
        out.writeFloat(mSeconds);
    }

    public float getHour() {
        return mHour;
    }

    public void setHour(final float mHour) {
        this.mHour = mHour;
    }

    public float getMinutes() {
        return mMinutes;
    }

    public void setMinutes(final float mMinutes) {
        this.mMinutes = mMinutes;
    }

    public float getSeconds() {
        return mSeconds;
    }

    public void setSeconds(final float mSeconds) {
        this.mSeconds = mSeconds;
    }

    /**
     * Sets all values back to zero
     */
    public void reset() {
        this.mHour = 0;
        this.mMinutes = 0;
        this.mSeconds = 0;
    }

    /**
     * Converts Clock Instance to Digital Clock representation
     *
     * @return String representation
     */
    public String getString() {
        final StringBuilder timeBuilder = new StringBuilder();
        timeBuilder.append(floatToString(mHour, 2));
        timeBuilder.append(":");
        timeBuilder.append(floatToString(mMinutes, 2));
        timeBuilder.append(":");
        timeBuilder.append(floatToString(mSeconds, 2));
        return timeBuilder.toString();
    }

    /**
     * Converts a float to a String with the requested number of digits
     *
     * @param num    Float to be converted
     * @param digits Integer number of digits requested
     * @return String value of the float
     */
    private String floatToString(final float num, final int digits) {
        // create variable length array of zeros
        final char[] zeros = new char[digits];
        Arrays.fill(zeros, '0');
        // format number as String
        final DecimalFormat df = new DecimalFormat(String.valueOf(zeros));

        return df.format(num);
    }

}

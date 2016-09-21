package com.michaelfotiadis.deskalarm.utils;

import android.graphics.Color;

public final class ColorUtils {

    private ColorUtils() {
        // do not instantiate
    }

    public static int getLighterColor(final int color) {

        final float[] hsv = new float[3];

        Color.colorToHSV(color, hsv);
        hsv[2] = 1.5f * hsv[2];

        if (hsv[2] > 1) {
            hsv[2] = 1;
        }

        return Color.HSVToColor(hsv);
    }

    public static int getDarkerColor(final int color) {

        final float[] hsv = new float[3];

        Color.colorToHSV(color, hsv);
        hsv[2] = 0.5f * hsv[2];

        if (hsv[2] > 1) {
            hsv[2] = 1;
        }

        return Color.HSVToColor(hsv);
    }

    public static int getRightBitShiftedColor(final int color) {
        return color >> 16;
    }

}

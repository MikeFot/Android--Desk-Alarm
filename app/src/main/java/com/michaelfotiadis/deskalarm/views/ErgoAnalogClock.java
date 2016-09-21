package com.michaelfotiadis.deskalarm.views;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RemoteViews.RemoteView;

import com.michaelfotiadis.deskalarm.R;
import com.michaelfotiadis.deskalarm.common.base.core.PreferenceHandler;
import com.michaelfotiadis.deskalarm.containers.ErgoClockInstance;
import com.michaelfotiadis.deskalarm.utils.ColorUtils;
import com.michaelfotiadis.deskalarm.utils.PrimitiveConversions;
import com.michaelfotiadis.deskalarm.utils.log.AppLog;

import java.util.Calendar;

/**
 * This widget display an analogue clock with two hands for hours and
 * minutes.
 */
@RemoteView
public class ErgoAnalogClock extends View implements ErgoClockInterface {

    private final long HANDLER_UPDATE_INTERVAL = 1000;

    private final PreferenceHandler mPreferenceHandler;
    private final Handler mHandler = new Handler();
    // drawable fields
    private Drawable mHourHand;
    private Drawable mMinuteHand;
    private Drawable mSecondHand;
    private Drawable mDial;
    private Drawable mAlarmHandMinutes;
    private Drawable mAlarmHandHours;
    private int mDialWidth;
    private int mDialHeight;
    private boolean mChanged;
    // color fields
    private int mOverlayColor;
    private int mShiftedOverlayColor;
    private int mLighterOverlayColor;
    private long mTimeRunning;
    private final ErgoClockInstance mClockInstance = new ErgoClockInstance();
    private long mStartTime = 0;
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mHandler.postDelayed(this, HANDLER_UPDATE_INTERVAL);
            updateTime();
        }
    };
    private int mInterval;

    public ErgoAnalogClock(final Context context) {
        super(context);
        mPreferenceHandler = new PreferenceHandler(context);
    }

    public ErgoAnalogClock(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private ErgoAnalogClock(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        mPreferenceHandler = new PreferenceHandler(context);
        final Resources resources = context.getResources();
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AnalogClock, defStyle, 0);
        mDial = resources.getDrawable(R.drawable.clock_dial);
        mHourHand = resources.getDrawable(R.drawable.clock_hand_hour);
        mMinuteHand = resources.getDrawable(R.drawable.clock_hand_minute);
        mSecondHand = resources.getDrawable(R.drawable.clock_hand_second);

        // set the 2 alarm hands
        mAlarmHandHours = resources.getDrawable(R.drawable.clock_hand_alarm_hours);
        mAlarmHandMinutes = resources.getDrawable(R.drawable.clock_hand_alarm_minutes);

        mDialWidth = mDial.getIntrinsicWidth();
        mDialHeight = mDial.getIntrinsicHeight();
        typedArray.recycle();

        final String hexColour = mPreferenceHandler.getAppSharedPreferences().getString(
                getContext().getString(R.string.pref_font_color_key),
                getContext().getString(R.string.pref_font_color_default_value));
        mOverlayColor = Color.parseColor(hexColour);
        mShiftedOverlayColor = ColorUtils.getRightBitShiftedColor(mOverlayColor);
        mLighterOverlayColor = ColorUtils.getLighterColor(mOverlayColor);
    }

    /**
     * Method to process Handler ticks
     */
    @Override
    public void updateTime() {
        setTimeRunning((Calendar.getInstance().getTimeInMillis() -
                mStartTime) / 1000);

        if (mStartTime == 0) {
            mClockInstance.reset();
            invalidate();
            return;
        }

        // convert time into an array
        final float[] timeIntArray = PrimitiveConversions.getHourMinuteFloatTimeArrayFromSeconds(getTimeRunning());

        // set time for the clock instance
        mClockInstance.setTime(timeIntArray[0], timeIntArray[1], timeIntArray[2]);

        mChanged = true;
        invalidate();
    }

    @Override
    public void pauseClock() {
        mHandler.removeCallbacks(mRunnable);
    }

    @Override
    public void setTime(final int hours, final int minutes, final int seconds) {
        //		AppLog.d("Setting time to " + hours + " " + minutes + " " + seconds);
        mClockInstance.setSeconds(6.0f * seconds);
        mClockInstance.setMinutes(minutes + seconds / 60.0f);
        mClockInstance.setHour(hours + minutes / 60.0f);
        invalidate();
    }

    @Override
    public void startClock(final long startTime, final int minutesToAlarm) {

        mStartTime = startTime;
        mInterval = minutesToAlarm;
        mHandler.post(mRunnable);
    }

    @Override
    public void stopClock() {
        mClockInstance.reset();
        invalidate();
        mHandler.removeCallbacks(mRunnable);
    }

    @Override
    public boolean isVisible() {
        return this.getVisibility() == View.VISIBLE;
    }

    @Override
    public long getTimeRunning() {
        return mTimeRunning;
    }

    @Override
    public void setMinutesToAlarm(final int minutesToAlarm) {
        mInterval = minutesToAlarm;
    }

    @Override
    public void setSystemTime() {

    }

    private void setTimeRunning(final long timeRunning) {
        this.mTimeRunning = timeRunning;
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        final boolean changed = mChanged;
        if (changed) {
            mChanged = false;
        }

        //Here you can set the size of your clock
        final int availableWidth = getRight() - getLeft();
        final int availableHeight = getBottom() - getTop();
        //Actual size
        final int actualX = availableWidth / 2;
        final int actualY = availableHeight / 2;

        final Drawable dial = mDial;
        int width = dial.getIntrinsicWidth();
        int height = dial.getIntrinsicHeight();
        boolean scaled = false;

        if (availableWidth < width || availableHeight < height) {
            scaled = true;
            final float scale = Math.min((float) availableWidth / (float) width,
                    (float) availableHeight / (float) height);
            canvas.save();
            canvas.scale(scale, scale, actualX, actualY);
        }
        if (changed) {
            dial.setBounds(actualX - (width / 2), actualY - (height / 2), actualX + (width / 2), actualY + (height / 2));
        }
        /* uncomment this line if you want clock color */
        // dial.setColorFilter(mOverlayColor, android.graphics.PorterDuff.Mode.MULTIPLY);
        dial.draw(canvas);
        canvas.save();

        // draw the minutes hand
        canvas.save();
        canvas.rotate(mClockInstance.getMinutes() / 60.0f * 360.0f, actualX, actualY);
        width = mMinuteHand.getIntrinsicWidth();
        height = mMinuteHand.getIntrinsicHeight();
        mMinuteHand.setBounds(actualX - (width / 2), actualY - (height / 2), actualX + (width / 2), actualY + (height / 2));
        mMinuteHand.draw(canvas);
        canvas.restore();

        // draw the hours hand
        canvas.save();
        canvas.rotate(mClockInstance.getHour() / 12.0f * 360.0f, actualX, actualY);
        width = mHourHand.getIntrinsicWidth();
        height = mHourHand.getIntrinsicHeight();
        mHourHand.setBounds(actualX - (width / 2), actualY - (height / 2), actualX + (width / 2), actualY + (height / 2));
        mHourHand.draw(canvas);
        canvas.restore();

        // draw the hours alarm indication
        canvas.save();
        canvas.rotate((mInterval / 60) / 12.0f * 360.0f, actualX, actualY);
        width = mAlarmHandHours.getIntrinsicWidth();
        height = mAlarmHandHours.getIntrinsicHeight();
        mAlarmHandHours.setColorFilter(mLighterOverlayColor, android.graphics.PorterDuff.Mode.MULTIPLY);
        mAlarmHandHours.setAlpha(200);
        mAlarmHandHours.setBounds(actualX - (width / 2), actualY - (height / 2), actualX + (width / 2), actualY + (height / 2));
        mAlarmHandHours.draw(canvas);
        canvas.restore();

        // draw the minutes alarm indication
        canvas.save();
        canvas.rotate(mInterval / 60.0f * 360.0f, actualX, actualY);
        width = mAlarmHandMinutes.getIntrinsicWidth();
        height = mAlarmHandMinutes.getIntrinsicHeight();
        mAlarmHandMinutes.setColorFilter(mLighterOverlayColor, android.graphics.PorterDuff.Mode.MULTIPLY);
        mAlarmHandMinutes.setAlpha(200);
        mAlarmHandMinutes.setBounds(actualX - (width / 2), actualY - (height / 2), actualX + (width / 2), actualY + (height / 2));
        mAlarmHandMinutes.draw(canvas);
        canvas.restore();

        // draw seconds
        canvas.save();
        canvas.rotate(mClockInstance.getSeconds() * 6.0f, actualX, actualY);
        width = mSecondHand.getIntrinsicWidth();
        height = mSecondHand.getIntrinsicHeight();
        mSecondHand.setBounds(actualX - (width / 2), actualY - (height / 2), actualX + (width / 2), actualY + (height / 2));
        mSecondHand.setColorFilter(mShiftedOverlayColor, android.graphics.PorterDuff.Mode.MULTIPLY);
        mSecondHand.draw(canvas);
        canvas.restore();

        if (scaled) {
            canvas.restore();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        AppLog.d("Analogue Clock Attached to Window");
        mChanged = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopClock();
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {

        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        float hScale = 1.0f;
        float vScale = 1.0f;

        if (widthMode != MeasureSpec.UNSPECIFIED && widthSize < mDialWidth) {
            hScale = (float) widthSize / (float) mDialWidth;
        }

        if (heightMode != MeasureSpec.UNSPECIFIED && heightSize < mDialHeight) {
            vScale = (float) heightSize / (float) mDialHeight;
        }

        final float scale = Math.min(hScale, vScale);
        setMeasuredDimension(200, 200);
        setMeasuredDimension(resolveSize((int) (mDialWidth * scale), widthMeasureSpec),
                resolveSize((int) (mDialHeight * scale), heightMeasureSpec));
    }
}
package com.michaelfotiadis.deskalarm.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RemoteViews.RemoteView;

import com.michaelfotiadis.deskalarm.R;
import com.michaelfotiadis.deskalarm.containers.ClockModel;
import com.michaelfotiadis.deskalarm.ui.base.core.preference.PreferenceHandler;
import com.michaelfotiadis.deskalarm.ui.base.core.preference.PreferenceHandlerImpl;
import com.michaelfotiadis.deskalarm.utils.ColorUtils;
import com.michaelfotiadis.deskalarm.utils.PrimitiveConversions;
import com.michaelfotiadis.deskalarm.utils.log.AppLog;

import java.util.Calendar;

/**
 * This widget display an analogue clock with two hands for hours and
 * minutes.
 */
@RemoteView
public class FusionClock extends View implements Clock {

    private final PreferenceHandler mPreferenceHandler;
    private final Handler mHandler = new Handler();
    private final ClockModel mClockInstance = new ClockModel();
    private Drawable mHourHand;
    private Drawable mMinuteHand;
    private Drawable mSecondHand;
    private Drawable mDial;
    private Drawable mAlarmHandMinutes;
    private Drawable mAlarmHandHours;
    private int mDialWidth;
    private int mDialHeight;
    private boolean mChanged;
    private boolean isClockRunning;
    // overlay color for color filter - set from prefs
    private int mOverlayColor;
    private int mLighterOverlayColor;
    private int mShiftedOverlayColor;
    private long mInterval;
    private long mTimeRunning;
    private long mStartTime = 0;
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            final long HANDLER_UPDATE_INTERVAL = 10;
            mHandler.postDelayed(this, HANDLER_UPDATE_INTERVAL);

            updateTime();
        }
    };
    private float rotation = 0;

    public FusionClock(final Context context) {
        super(context);
        mPreferenceHandler = new PreferenceHandlerImpl(context);
    }

    public FusionClock(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private FusionClock(final Context context, final AttributeSet attrs,
                        final int defStyle) {
        super(context, attrs, defStyle);
        mPreferenceHandler = new PreferenceHandlerImpl(context);
        mDial = ContextCompat.getDrawable(context, R.drawable.clock_fusion_dial_r_three);
        mHourHand = ContextCompat.getDrawable(context, R.drawable.clock_fusion_hour_hand);
        mMinuteHand = ContextCompat.getDrawable(context, R.drawable.clock_fusion_hand_minutes);
        mSecondHand = ContextCompat.getDrawable(context, R.drawable.clock_fusion_second);
        mAlarmHandMinutes = ContextCompat.getDrawable(context, R.drawable.clock_fusion_minute_back);
        mAlarmHandHours = ContextCompat.getDrawable(context, R.drawable.clock_fusion_hour_back);

        mDialWidth = mDial.getIntrinsicWidth();
        mDialHeight = mDial.getIntrinsicHeight();

        final String hexColour = mPreferenceHandler.getString(PreferenceHandlerImpl.PreferenceKey.FONT_COLOR);
        mOverlayColor = Color.parseColor(hexColour);

        mLighterOverlayColor = ColorUtils.getLighterColor(mOverlayColor);
        mShiftedOverlayColor = ColorUtils.getRightBitShiftedColor(mOverlayColor);

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
        final float[] timeIntArray = PrimitiveConversions.getHourFloatTimeArrayFromSeconds(getTimeRunning());

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
        //		Logger.d(TAG, "Setting time to " + hours + " " + minutes + " " + seconds);
        mClockInstance.setSeconds(6.0f * seconds);
        mClockInstance.setMinutes(minutes + seconds / 60.0f);
        mClockInstance.setHour(hours + minutes / 60.0f);
        invalidate();
    }

    @Override
    public void startClock(final long startTime, final long minutesToAlarm) {
        mStartTime = startTime;
        mInterval = minutesToAlarm;
        isClockRunning = true;
        mHandler.post(mRunnable);
    }

    @Override
    public void stopClock() {
        isClockRunning = false;
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

    private void setTimeRunning(final long timeRunning) {
        this.mTimeRunning = timeRunning;
    }

    @Override
    public void setMinutesToAlarm(final long minutesToAlarm) {
        mInterval = minutesToAlarm;
    }

    @Override
    public void setSystemTime() {
        // TODO Auto-generated method stub

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
        canvas.save();

        // draw the hours alarm indication
        canvas.save();
        canvas.rotate((int) ((mInterval / 60) / 12.0f * 360.0f), actualX, actualY);
        width = mAlarmHandHours.getIntrinsicWidth();
        height = mAlarmHandHours.getIntrinsicHeight();
        mAlarmHandHours.setColorFilter(mOverlayColor, android.graphics.PorterDuff.Mode.MULTIPLY);
        mAlarmHandHours.setBounds(actualX - (width / 2), actualY - (height / 2), actualX + (width / 2), actualY + (height / 2));
        mAlarmHandHours.draw(canvas);
        canvas.restore();

        // draw the clock hour hand on top of the hours indication
        canvas.rotate(mClockInstance.getHour() / 12.0f * 360.0f, actualX, actualY);
        width = mHourHand.getIntrinsicWidth();
        height = mHourHand.getIntrinsicHeight();
        mHourHand.setColorFilter(mShiftedOverlayColor, android.graphics.PorterDuff.Mode.MULTIPLY);
        mHourHand.setBounds(actualX - (width / 2), actualY - (height / 2), actualX + (width / 2), actualY + (height / 2));
        mHourHand.draw(canvas);
        canvas.restore();

        // draw the minutes alarm indication which includes the backface of the minutes drawable
        canvas.save();
        // rotate effectively once
        canvas.rotate(mInterval / 60.0f * 360.0f, actualX, actualY);
        width = mAlarmHandMinutes.getIntrinsicWidth();
        height = mAlarmHandMinutes.getIntrinsicHeight();
        mAlarmHandMinutes.setColorFilter(mShiftedOverlayColor, android.graphics.PorterDuff.Mode.MULTIPLY);
        mAlarmHandMinutes.setBounds(actualX - (width / 2), actualY - (height / 2), actualX + (width / 2), actualY + (height / 2));
        mAlarmHandMinutes.draw(canvas);
        canvas.restore();

        // draw minutes
        canvas.save();
        canvas.rotate(mClockInstance.getMinutes() / 60.0f * 360.0f, actualX, actualY);
        width = mMinuteHand.getIntrinsicWidth();
        height = mMinuteHand.getIntrinsicHeight();
        mMinuteHand.setColorFilter(mLighterOverlayColor, android.graphics.PorterDuff.Mode.MULTIPLY);
        mMinuteHand.setBounds(actualX - (width / 2), actualY - (height / 2), actualX + (width / 2), actualY + (height / 2));
        mMinuteHand.draw(canvas);
        canvas.restore();

        // draw seconds
        canvas.save();
        canvas.rotate(mClockInstance.getSeconds() * 6.0f, actualX, actualY);
        width = mSecondHand.getIntrinsicWidth();
        height = mSecondHand.getIntrinsicHeight();
        mSecondHand.setBounds(actualX - (width / 2), actualY - (height / 2), actualX + (width / 2), actualY + (height / 2));
        // set a color filter
        mSecondHand.setColorFilter(mOverlayColor, android.graphics.PorterDuff.Mode.MULTIPLY);
        mSecondHand.draw(canvas);
        canvas.restore();

        // handle the rotating graphic - use a boolean to enable draw
        if (isClockRunning) {
            canvas.rotate(rotation, actualX, actualY);
            dial.setColorFilter(mLighterOverlayColor, android.graphics.PorterDuff.Mode.MULTIPLY);
//			dial.setAlpha(20);
            dial.draw(canvas);
        }

        if (scaled) {
            canvas.restore();
        }
        rotation = (rotation + 2f) % 360;
        this.postInvalidate();

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        AppLog.d("Fusion Clock Attached to Window");
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
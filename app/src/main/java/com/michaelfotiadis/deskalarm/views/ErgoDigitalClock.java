package com.michaelfotiadis.deskalarm.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.michaelfotiadis.deskalarm.R;
import com.michaelfotiadis.deskalarm.containers.ClockModelInstance;
import com.michaelfotiadis.deskalarm.ui.base.core.PreferenceHandler;
import com.michaelfotiadis.deskalarm.utils.PrimitiveConversions;
import com.michaelfotiadis.deskalarm.utils.log.AppLog;

import java.util.Calendar;

public class ErgoDigitalClock extends TextView implements ErgoClockInterface {

    private static final String TIME_ZERO_VALUE = "00:00:00";
    private static final long _updateInterval = 1000;
    private final Handler mHandler = new Handler();
    private final PreferenceHandler mPreferenceHandler;
    private long mStartTime = 0;
    private int mPrefFontSize = 12;
    private final ClockModelInstance mClockInstance;
    private long mTimeRunning;
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mHandler.postDelayed(this, _updateInterval);
            updateTime();
        }
    };

    public ErgoDigitalClock(final Context context) {
        super(context);
        mClockInstance = new ClockModelInstance();
        mPreferenceHandler = new PreferenceHandler(context);
    }

    public ErgoDigitalClock(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        mClockInstance = new ClockModelInstance();
        mPreferenceHandler = new PreferenceHandler(context);
        init(attrs);


    }

    public ErgoDigitalClock(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        mClockInstance = new ClockModelInstance();
        mPreferenceHandler = new PreferenceHandler(context);
        init(attrs);
    }

    @Override
    public void updateTime() {
        setTimeRunning((Calendar.getInstance().getTimeInMillis() -
                mStartTime) / 1000);

        if (mStartTime == 0) {
            mClockInstance.reset();
            this.setText(TIME_ZERO_VALUE);
            return;
        }

        final int[] timeIntArray = PrimitiveConversions.getIntTimeArrayFromSeconds(getTimeRunning());
        mClockInstance.setTime(timeIntArray[0], timeIntArray[1], timeIntArray[2]);

        this.setText(mClockInstance.getString());
        invalidate();
    }

    @Override
    public void pauseClock() {
        mHandler.removeCallbacks(mRunnable);
    }

    @Override
    public void setTime(final int hours, final int minutes, final int seconds) {
        mClockInstance.setSeconds(6.0f * seconds);
        mClockInstance.setMinutes(minutes + seconds / 60.0f);
        mClockInstance.setHour(hours + minutes / 60.0f);
        updateTime();
    }

    @Override
    public void startClock(final long startTime, final int minutesToAlarm) {
        mStartTime = startTime;
        mHandler.post(mRunnable);
        invalidate();
    }

    @Override
    public void stopClock() {
        mClockInstance.reset();
        this.setText(TIME_ZERO_VALUE);
        mHandler.removeCallbacks(mRunnable);
    }

    @Override
    public boolean isVisible() {
        if (this.getVisibility() == View.VISIBLE) {
            return true;
        }
        return false;
    }

    @Override
    public long getTimeRunning() {
        return mTimeRunning;
    }

    private void setTimeRunning(final long timeRunning) {
        this.mTimeRunning = timeRunning;
    }

    @Override
    public void setMinutesToAlarm(final int minutesToAlarm) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setSystemTime() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    private void init(final AttributeSet attrs) {
        mPrefFontSize = (int) getResources().getDimension(R.dimen.digital_clock_font_size);

        final String fontName = mPreferenceHandler.getAppSharedPreferences().getString(
                getContext().getString(R.string.pref_font_key),
                getContext().getString(R.string.pref_font_default_value));
        final Typeface typeface = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/" + fontName);
        try {
            setTypeface(typeface);
        } catch (final Exception e1) {
            AppLog.e("Error encountered while setting font Typeface: ", e1);
        }


        setTextSize(mPrefFontSize);

		/* This commented section contains code for handling Attributes */
        /*
        if (attrs!=null) {
			TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.DigitalClock);

			final int N = a.getIndexCount();
			for (int i = 0; i < N; ++i)
			{
				int attr = a.getIndex(i);
				switch (attr)
				{
				case R.styleable.DigitalClock_fontName:
					fontName = a.getString(attr);
					if (fontName != null) {
						Logger.i(TAG, "USING FONT " + fontName);
						typeface = Typeface.createFromAsset(getContext().getAssets(),
								"fonts/" + fontName);
						setTypeface(typeface);
						setTextSize(mPrefFontSize);
					}
					break;
				default:
					break;
				}
			}
			a.recycle();
		}
		*/

        final String fontColour = mPreferenceHandler.getAppSharedPreferences().getString(
                getContext().getString(R.string.pref_font_color_key),
                getContext().getString(R.string.pref_font_color_default_value));
        try {
            this.setTextColor(Color.parseColor(fontColour));
        } catch (final Exception e) {
            AppLog.e("Error encountered while settng font Color: ", e);
        }

        // set the time initially to 00:00:00
        this.setText(TIME_ZERO_VALUE);
    }
}

package com.michaelfotiadis.deskalarm.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.michaelfotiadis.deskalarm.R;
import com.michaelfotiadis.deskalarm.containers.ErgoClockInstance;
import com.michaelfotiadis.deskalarm.utils.AppUtils;
import com.michaelfotiadis.deskalarm.utils.Logger;
import com.michaelfotiadis.deskalarm.utils.PrimitiveConversions;

import java.util.Calendar;

public class ErgoDigitalClock extends TextView implements ErgoClockInterface {

    private final String TAG = "My Digital Clock";

    private final String TIME_ZERO_VALUE = "00:00:00";

    private long mStartTime = 0;

    private int prefFontSize = 12;

    private ErgoClockInstance mClockInstance;

    private final Handler mHandler = new Handler();

    private final long _updateInterval = 1000;

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mHandler.postDelayed(this, _updateInterval);
            updateTime();
        }
    };

    public ErgoDigitalClock(Context context) {
        super(context);
        mClockInstance = new ErgoClockInstance();
    }

    public ErgoDigitalClock(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
        mClockInstance = new ErgoClockInstance();

    }

    public ErgoDigitalClock(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
        mClockInstance = new ErgoClockInstance();
    }

    private void init(AttributeSet attrs) {
        prefFontSize = (int) getResources().getDimension(R.dimen.digital_clock_font_size);

        String fontName = new AppUtils().getAppSharedPreferences(getContext()).getString(
                getContext().getString(R.string.pref_font_key),
                getContext().getString(R.string.pref_font_default_value));
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/" + fontName);
        try {
            setTypeface(typeface);
        } catch (Exception e1) {
            Logger.e(TAG, "Error encountered while settng font Typeface: ", e1);
        }


        setTextSize(prefFontSize);

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
						setTextSize(prefFontSize);
					}
					break;
				default:
					break;
				}
			}
			a.recycle();
		}
		*/

        String fontColour = new AppUtils().getAppSharedPreferences(getContext()).getString(
                getContext().getString(R.string.pref_font_color_key),
                getContext().getString(R.string.pref_font_color_default_value));
        try {
            this.setTextColor(Color.parseColor(fontColour));
        } catch (Exception e) {
            Logger.e(TAG, "Error encountered while settng font Color: ", e);
        }

        // set the time initially to 00:00:00
        this.setText(TIME_ZERO_VALUE);
    }

    @Override
    public boolean isVisible() {
        if (this.getVisibility() == View.VISIBLE) {
            return true;
        }
        return false;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
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

    private long mTimeRunning;

    @Override
    public void updateTime() {
        setTimeRunning((Calendar.getInstance().getTimeInMillis() -
                mStartTime) / 1000);

        if (mStartTime == 0) {
            mClockInstance.reset();
            this.setText(TIME_ZERO_VALUE);
            return;
        }

        int[] timeIntArray = PrimitiveConversions.getIntTimeArrayFromSeconds(getTimeRunning());
        mClockInstance.setTime(timeIntArray[0], timeIntArray[1], timeIntArray[2]);

        this.setText(mClockInstance.getString());
        invalidate();
    }

    @Override
    public long getTimeRunning() {
        return mTimeRunning;
    }

    private void setTimeRunning(long timeRunning) {
        this.mTimeRunning = timeRunning;
    }

    @Override
    public void setMinutesToAlarm(int minutesToAlarm) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setSystemTime() {
        // TODO Auto-generated method stub

    }
}

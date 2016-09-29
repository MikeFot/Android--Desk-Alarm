package com.michaelfotiadis.deskalarm.ui.base.core;

import android.content.Context;
import android.content.Intent;

import com.michaelfotiadis.deskalarm.services.AudioService;
import com.michaelfotiadis.deskalarm.services.step.StepService;
import com.michaelfotiadis.deskalarm.utils.log.AppLog;

public class ErgoServiceManager {

    private final Context mContext;

    protected ErgoServiceManager(final Context context) {
        this.mContext = context;
    }

    /**
     * Starts the step service
     */
    public void startStepService() {
        if (!StepService.isServiceRunning()) {
            AppLog.d("Starting Service");
            final Intent intent = new Intent(mContext, StepService.class);
            mContext.startService(intent);
        } else {
            AppLog.d("Service Already Running");
        }
    }

    /**
     * Stops the step service
     */
    public void stopStepService() {
        AppLog.d("Stopping Service");
        final Intent intent = new Intent(mContext, StepService.class);
        mContext.stopService(intent);
    }

    /**
     * Starts the audio service
     */
    public void startAudioService() {
        final Intent intent = new Intent(mContext, AudioService.class);
        mContext.startService(intent);
    }

}

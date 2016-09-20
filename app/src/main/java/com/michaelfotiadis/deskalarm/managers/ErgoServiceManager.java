package com.michaelfotiadis.deskalarm.managers;

import android.content.Context;
import android.content.Intent;

import com.michaelfotiadis.deskalarm.services.ErgoAudioService;
import com.michaelfotiadis.deskalarm.services.ErgoStepService;
import com.michaelfotiadis.deskalarm.utils.Logger;

public class ErgoServiceManager {

    private String TAG = "Service Manager";

    /**
     * Starts the step service
     */
    public void startStepService(final Context context) {
        if (!ErgoStepService.isServiceRunning()) {
            Logger.d(TAG, "Starting Service");
            Intent intent = new Intent(context, ErgoStepService.class);
            context.startService(intent);
        } else {
            Logger.d(TAG, "Service Already Running");
        }
    }

    ;

    /**
     * Stops the step service
     */
    public void stopStepService(final Context context) {
        Logger.d(TAG, "Stopping Service");
        Intent intent = new Intent(context, ErgoStepService.class);
        context.stopService(intent);
    }

    /**
     * Starts the audio service
     */
    public void startAudioService(final Context context) {
        Intent intent = new Intent(context, ErgoAudioService.class);
        context.startService(intent);
    }

    ;

}

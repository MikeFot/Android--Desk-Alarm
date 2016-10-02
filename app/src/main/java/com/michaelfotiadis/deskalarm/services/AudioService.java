package com.michaelfotiadis.deskalarm.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.michaelfotiadis.deskalarm.ui.base.core.Core;
import com.michaelfotiadis.deskalarm.ui.base.core.CoreProvider;
import com.michaelfotiadis.deskalarm.ui.base.core.preference.PreferenceHandler;
import com.michaelfotiadis.deskalarm.utils.log.AppLog;

public class AudioService extends IntentService {

    private final Core mCore;

    public AudioService() {
        super("AudioService");
        mCore = new CoreProvider(this);
    }

    @Override
    protected void onHandleIntent(final Intent intent) {

        final String preference = mCore.getPreferenceHandler().getStringPreference(PreferenceHandler.PreferenceKey.RINGTONE);

        if (preference.length() < 1) {
            this.stopSelf();
        }
        final int resID = getApplicationContext().getResources().getIdentifier(
                preference, "raw", getApplicationContext().getPackageName());
        if (resID == 0) {
            this.stopSelf();
        }

        try {
            final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

            if (audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) != 0) {
                final MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), resID);
                mediaPlayer.start();
            }
        } catch (final Exception e) {
            AppLog.d(String.format("Exception while playing Audio:%s", e));
        }
    }


}

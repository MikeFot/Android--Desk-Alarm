package com.michaelfotiadis.deskalarm.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.michaelfotiadis.deskalarm.R;
import com.michaelfotiadis.deskalarm.common.base.core.Core;
import com.michaelfotiadis.deskalarm.common.base.core.CoreProvider;
import com.michaelfotiadis.deskalarm.utils.log.AppLog;

public class ErgoAudioService extends IntentService {

    private final Core mCore;

    public ErgoAudioService() {
        super("ErgoAudioService");
        mCore = new CoreProvider(this);
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        final String preference = mCore.getPreferenceHandler().getAppSharedPreferences().getString(
                getString(R.string.pref_ringtones_key),
                getString(R.string.pref_ringtones_default));
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

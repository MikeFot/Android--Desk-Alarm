package com.michaelfotiadis.deskalarm.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.michaelfotiadis.deskalarm.R;
import com.michaelfotiadis.deskalarm.utils.AppUtils;
import com.michaelfotiadis.deskalarm.utils.Logger;

public class ErgoAudioService extends IntentService {

    private final String TAG = "ErgoAudioService";

    public ErgoAudioService() {
        super("ErgoAudioService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String preference = new AppUtils().getAppSharedPreferences(getApplicationContext()).getString(
                getString(R.string.pref_ringtones_key),
                getString(R.string.pref_ringtones_default));
        if (preference.length() < 1) {
            this.stopSelf();
        }
        int resID = getApplicationContext().getResources().getIdentifier(
                preference, "raw", getApplicationContext().getPackageName());
        if (resID == 0) {
            this.stopSelf();
        }

        try {
            final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

            if (audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) != 0) {
                MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), resID);
                // TODO getting an orange message!!!
                mediaPlayer.start();
            }
        } catch (Exception e) {
            Logger.e(TAG, "Exception while playing Audio:", e);
        }
    }


}

package com.michaelfotiadis.deskalarm.common.base.core;

import android.content.Context;

import com.michaelfotiadis.deskalarm.utils.FileHelper;

/**
 *
 */
public class CoreProvider implements Core {

    private final Context mContext;
    private PreferenceHandler mPreferenceHandler;
    private ErgoServiceManager mServiceManager;
    private ErgoNotificationManager mNotificationManager;
    private ErgoAlarmManager mAlarmManager;
    private ErgoDataManager mDataManager;
    private FileHelper mFileHelper;

    public CoreProvider(final Context context) {
        this.mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public PreferenceHandler getPreferenceHandler() {
        if (mPreferenceHandler == null) {
            mPreferenceHandler = new PreferenceHandler(mContext);
        }
        return mPreferenceHandler;
    }

    @Override
    public ErgoServiceManager getServiceManager() {
        if (mServiceManager == null) {
            mServiceManager = new ErgoServiceManager(mContext);
        }
        return mServiceManager;
    }

    @Override
    public ErgoNotificationManager getNotificationManager() {
        if (mNotificationManager == null) {
            mNotificationManager = new ErgoNotificationManager(mContext);
        }
        return mNotificationManager;
    }

    @Override
    public ErgoAlarmManager getAlarmManager() {
        if (mAlarmManager == null) {
            mAlarmManager = new ErgoAlarmManager(mContext);
        }
        return mAlarmManager;
    }

    @Override
    public FileHelper getFileHelper() {
        if (mFileHelper == null) {
            mFileHelper = new FileHelper(mContext);
        }
        return mFileHelper;
    }

    @Override
    public ErgoDataManager getDataManager() {
        if (mDataManager == null) {
            mDataManager = new ErgoDataManager(mContext, getPreferenceHandler(), getFileHelper());
        }
        return mDataManager;
    }

}

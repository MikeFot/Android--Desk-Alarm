package com.michaelfotiadis.deskalarm.ui.base.core;

import android.content.Context;

import com.michaelfotiadis.deskalarm.ui.base.core.preference.PreferenceHandler;
import com.michaelfotiadis.deskalarm.ui.base.core.preference.PreferenceHandlerImpl;
import com.michaelfotiadis.deskalarm.utils.FileHelper;

/**
 *
 */
public class CoreProvider implements Core {

    private final Context mContext;
    private PreferenceHandler mPreferenceHandler;
    private ServiceManager mServiceManager;
    private NotificationManager mNotificationManager;
    private AlarmManager mAlarmManager;
    private DataManager mDataManager;
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
            mPreferenceHandler = new PreferenceHandlerImpl(mContext);
        }
        return mPreferenceHandler;
    }

    @Override
    public ServiceManager getServiceManager() {
        if (mServiceManager == null) {
            mServiceManager = new ServiceManagerImpl(mContext);
        }
        return mServiceManager;
    }

    @Override
    public NotificationManager getNotificationManager() {
        if (mNotificationManager == null) {
            mNotificationManager = new NotificationManagerImpl(mContext);
        }
        return mNotificationManager;
    }

    @Override
    public AlarmManager getAlarmManager() {
        if (mAlarmManager == null) {
            mAlarmManager = new AlarmManagerImpl(mContext);
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
    public DataManager getDataManager() {
        if (mDataManager == null) {
            mDataManager = new DataManagerImpl(mContext, getPreferenceHandler(), getFileHelper());
        }
        return mDataManager;
    }

}

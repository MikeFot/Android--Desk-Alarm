package com.michaelfotiadis.deskalarm.ui.base.core;

import com.michaelfotiadis.deskalarm.ui.base.core.preference.PreferenceHandler;
import com.michaelfotiadis.deskalarm.utils.FileHelper;

/**
 *
 */
public interface Core {

    PreferenceHandler getPreferenceHandler();

    ServiceManager getServiceManager();

    NotificationManager getNotificationManager();

    AlarmManager getAlarmManager();

    FileHelper getFileHelper();

    DataManager getDataManager();
}

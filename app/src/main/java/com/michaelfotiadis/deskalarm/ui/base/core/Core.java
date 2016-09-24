package com.michaelfotiadis.deskalarm.ui.base.core;

import com.michaelfotiadis.deskalarm.utils.FileHelper;

/**
 *
 */
public interface Core {

    PreferenceHandler getPreferenceHandler();

    ErgoServiceManager getServiceManager();

    ErgoNotificationManager getNotificationManager();

    ErgoAlarmManager getAlarmManager();

    FileHelper getFileHelper();

    ErgoDataManager getDataManager();
}

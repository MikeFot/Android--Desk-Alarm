package com.michaelfotiadis.deskalarm.common.base.core;

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

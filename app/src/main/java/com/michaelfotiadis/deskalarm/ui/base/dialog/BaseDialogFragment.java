package com.michaelfotiadis.deskalarm.ui.base.dialog;

import android.content.Context;
import android.support.v4.app.DialogFragment;

import com.michaelfotiadis.deskalarm.ui.base.activity.BaseActivity;
import com.michaelfotiadis.deskalarm.ui.base.core.AlarmManager;
import com.michaelfotiadis.deskalarm.ui.base.core.Core;
import com.michaelfotiadis.deskalarm.ui.base.core.CoreProvider;
import com.michaelfotiadis.deskalarm.ui.base.core.DataManager;
import com.michaelfotiadis.deskalarm.ui.base.core.NotificationManager;
import com.michaelfotiadis.deskalarm.ui.base.core.ServiceManager;
import com.michaelfotiadis.deskalarm.ui.base.core.preference.PreferenceHandler;
import com.michaelfotiadis.deskalarm.utils.FileHelper;

/**
 *
 */
public abstract class BaseDialogFragment extends DialogFragment implements Core {

    Core mCore;

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            mCore = ((BaseActivity) context).getCore();
        } else {
            mCore = new CoreProvider(context);
        }
    }

    @Override
    public PreferenceHandler getPreferenceHandler() {
        return mCore.getPreferenceHandler();
    }

    @Override
    public AlarmManager getAlarmManager() {
        return mCore.getAlarmManager();
    }

    @Override
    public NotificationManager getNotificationManager() {
        return mCore.getNotificationManager();
    }


    @Override
    public ServiceManager getServiceManager() {
        return mCore.getServiceManager();
    }

    @Override
    public DataManager getDataManager() {
        return mCore.getDataManager();
    }

    @Override
    public FileHelper getFileHelper() {
        return mCore.getFileHelper();
    }

}



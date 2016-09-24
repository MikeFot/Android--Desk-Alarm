package com.michaelfotiadis.deskalarm.ui.base.dialog;

import android.content.Context;
import android.support.v4.app.DialogFragment;

import com.michaelfotiadis.deskalarm.ui.base.activity.BaseActivity;
import com.michaelfotiadis.deskalarm.ui.base.core.Core;
import com.michaelfotiadis.deskalarm.ui.base.core.CoreProvider;
import com.michaelfotiadis.deskalarm.ui.base.core.ErgoAlarmManager;
import com.michaelfotiadis.deskalarm.ui.base.core.ErgoDataManager;
import com.michaelfotiadis.deskalarm.ui.base.core.ErgoNotificationManager;
import com.michaelfotiadis.deskalarm.ui.base.core.ErgoServiceManager;
import com.michaelfotiadis.deskalarm.ui.base.core.PreferenceHandler;
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
    public ErgoAlarmManager getAlarmManager() {
        return mCore.getAlarmManager();
    }

    @Override
    public ErgoNotificationManager getNotificationManager() {
        return mCore.getNotificationManager();
    }


    @Override
    public ErgoServiceManager getServiceManager() {
        return mCore.getServiceManager();
    }

    @Override
    public ErgoDataManager getDataManager() {
        return mCore.getDataManager();
    }

    @Override
    public FileHelper getFileHelper() {
        return mCore.getFileHelper();
    }

}



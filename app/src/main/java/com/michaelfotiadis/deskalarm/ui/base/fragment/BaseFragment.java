package com.michaelfotiadis.deskalarm.ui.base.fragment;

import android.support.v4.app.Fragment;

import com.michaelfotiadis.deskalarm.ui.base.activity.BaseActivity;
import com.michaelfotiadis.deskalarm.ui.base.core.AlarmManager;
import com.michaelfotiadis.deskalarm.ui.base.core.Core;
import com.michaelfotiadis.deskalarm.ui.base.core.CoreProvider;
import com.michaelfotiadis.deskalarm.ui.base.core.DataManager;
import com.michaelfotiadis.deskalarm.ui.base.core.NotificationManager;
import com.michaelfotiadis.deskalarm.ui.base.core.ServiceManager;
import com.michaelfotiadis.deskalarm.ui.base.core.preference.PreferenceHandler;
import com.michaelfotiadis.deskalarm.ui.base.dialog.AlertDialogFactory;
import com.michaelfotiadis.deskalarm.utils.FileHelper;

/**
 *
 */

public abstract class BaseFragment extends Fragment implements Core {

    private AlertDialogFactory mAlertDialogFactory;
    private Core mCore;

    @Override
    public PreferenceHandler getPreferenceHandler() {
        return getCore().getPreferenceHandler();
    }

    @Override
    public ServiceManager getServiceManager() {
        return getCore().getServiceManager();
    }

    @Override
    public NotificationManager getNotificationManager() {
        return getCore().getNotificationManager();
    }

    @Override
    public AlarmManager getAlarmManager() {
        return getCore().getAlarmManager();
    }

    @Override
    public FileHelper getFileHelper() {
        return getCore().getFileHelper();
    }

    @Override
    public DataManager getDataManager() {
        return getCore().getDataManager();
    }

    protected Core getCore() {
        if (mCore == null) {
            if (getActivity() != null && getActivity() instanceof BaseActivity) {
                mCore = ((BaseActivity) getActivity()).getCore();
            } else {
                mCore = new CoreProvider(getActivity());
            }
        }
        return mCore;
    }

    protected AlertDialogFactory getAlertDialogFactory() {
        if (mAlertDialogFactory == null) {
            mAlertDialogFactory = new AlertDialogFactory(getActivity());
        }
        return mAlertDialogFactory;
    }
}

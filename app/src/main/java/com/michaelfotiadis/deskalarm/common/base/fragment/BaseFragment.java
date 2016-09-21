package com.michaelfotiadis.deskalarm.common.base.fragment;

import android.support.v4.app.Fragment;

import com.michaelfotiadis.deskalarm.common.base.activity.BaseActivity;
import com.michaelfotiadis.deskalarm.common.base.core.Core;
import com.michaelfotiadis.deskalarm.common.base.core.CoreProvider;
import com.michaelfotiadis.deskalarm.common.base.core.ErgoAlarmManager;
import com.michaelfotiadis.deskalarm.common.base.core.ErgoDataManager;
import com.michaelfotiadis.deskalarm.common.base.core.ErgoNotificationManager;
import com.michaelfotiadis.deskalarm.common.base.core.ErgoServiceManager;
import com.michaelfotiadis.deskalarm.common.base.core.PreferenceHandler;
import com.michaelfotiadis.deskalarm.common.base.dialog.AlertDialogFactory;
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
    public ErgoServiceManager getServiceManager() {
        return getCore().getServiceManager();
    }

    @Override
    public ErgoNotificationManager getNotificationManager() {
        return getCore().getNotificationManager();
    }

    @Override
    public ErgoAlarmManager getAlarmManager() {
        return getCore().getAlarmManager();
    }

    @Override
    public FileHelper getFileHelper() {
        return getCore().getFileHelper();
    }

    @Override
    public ErgoDataManager getDataManager() {
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

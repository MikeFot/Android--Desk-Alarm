package com.michaelfotiadis.deskalarm.common.base.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.michaelfotiadis.deskalarm.common.base.core.Core;
import com.michaelfotiadis.deskalarm.common.base.core.CoreProvider;
import com.michaelfotiadis.deskalarm.common.base.core.ErgoAlarmManager;
import com.michaelfotiadis.deskalarm.common.base.core.ErgoDataManager;
import com.michaelfotiadis.deskalarm.common.base.core.ErgoNotificationManager;
import com.michaelfotiadis.deskalarm.common.base.core.ErgoServiceManager;
import com.michaelfotiadis.deskalarm.common.base.core.PreferenceHandler;
import com.michaelfotiadis.deskalarm.common.base.dialog.AlertDialogFactory;
import com.michaelfotiadis.deskalarm.utils.FileHelper;

public class BaseActivity extends AppCompatActivity implements Core {

    private AlertDialogFactory mAlertDialogFactory;
    private Core mCore;

    protected void addContentFragmentIfMissing(final Fragment fragment, final int id, final String fragmentTag) {
        if (getSupportFragmentManager().findFragmentByTag(fragmentTag) == null) {
            final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(id, fragment, fragmentTag);
            fragmentTransaction.commit();
        }
    }

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
        return mCore.getDataManager();
    }

    public Core getCore() {
        if (mCore == null) {
            mCore = new CoreProvider(this);
        }
        return mCore;
    }

    public AlertDialogFactory getAlertDialogFactory() {
        if (mAlertDialogFactory == null) {
            mAlertDialogFactory = new AlertDialogFactory(this);
        }
        return mAlertDialogFactory;
    }
}

package com.michaelfotiadis.deskalarm.ui.base.activity;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.michaelfotiadis.deskalarm.R;
import com.michaelfotiadis.deskalarm.notification.ActivityNotificationController;
import com.michaelfotiadis.deskalarm.notification.SnackbarNotificationController;
import com.michaelfotiadis.deskalarm.ui.base.core.AlarmManager;
import com.michaelfotiadis.deskalarm.ui.base.core.Core;
import com.michaelfotiadis.deskalarm.ui.base.core.CoreProvider;
import com.michaelfotiadis.deskalarm.ui.base.core.DataManager;
import com.michaelfotiadis.deskalarm.ui.base.core.NotificationManager;
import com.michaelfotiadis.deskalarm.ui.base.core.ServiceManager;
import com.michaelfotiadis.deskalarm.ui.base.core.preference.PreferenceHandler;
import com.michaelfotiadis.deskalarm.ui.base.dialog.AlertDialogFactory;
import com.michaelfotiadis.deskalarm.utils.FileHelper;
import com.michaelfotiadis.deskalarm.utils.log.AppLog;

public abstract class BaseActivity extends AppCompatActivity implements Core {

    protected static final int NO_LAYOUT = Integer.MIN_VALUE;

    private AlertDialogFactory mAlertDialogFactory;
    private Core mCore;
    private ActivityNotificationController mNotificationController;
    private Toolbar mToolbar;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppLog.d("OnCreate");
        if (getLayoutResource() != NO_LAYOUT) {
            AppLog.d("On Create with layout resource " + getLayoutResource());
            setContentView(getLayoutResource());
            setupActionBar();

            final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
            if (coordinatorLayout != null) {
                mNotificationController = new SnackbarNotificationController(this);
            }

        }
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    protected void setupActionBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            setTitle("");
        } else {
            AppLog.w(this.getClass().getName() + ": Null toolbar");
        }
    }

    public void setDisplayHomeAsUpEnabled(final boolean isEnabled) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(isEnabled);
        }
    }

    protected abstract int getLayoutResource();

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

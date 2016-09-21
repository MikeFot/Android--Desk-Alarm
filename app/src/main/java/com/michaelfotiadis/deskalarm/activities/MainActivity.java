package com.michaelfotiadis.deskalarm.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

import com.michaelfotiadis.deskalarm.R;
import com.michaelfotiadis.deskalarm.adapters.CustomFragmentAdapter;
import com.michaelfotiadis.deskalarm.adapters.CustomViewPager;
import com.michaelfotiadis.deskalarm.common.base.activity.BaseActivity;
import com.michaelfotiadis.deskalarm.common.base.core.ErgoAlarmManager.ALARM_MODE;
import com.michaelfotiadis.deskalarm.fragments.ErgoClockFragment;
import com.michaelfotiadis.deskalarm.fragments.ErgoGraphFragment;
import com.michaelfotiadis.deskalarm.model.Broadcasts;
import com.michaelfotiadis.deskalarm.model.Payloads;
import com.michaelfotiadis.deskalarm.services.ErgoStepService;
import com.michaelfotiadis.deskalarm.utils.ToastHelper;
import com.michaelfotiadis.deskalarm.utils.log.AppLog;

import java.lang.reflect.Method;

public class MainActivity extends BaseActivity implements
        OnCheckedChangeListener, TabListener, OnPageChangeListener,
        OnClickListener, OnSharedPreferenceChangeListener {

    private Dialog mDialog;
    private boolean showDialog = false;
    private boolean isActivityShown = false;
    private ToastHelper mToastHelper;

    private CustomViewPager mViewPager;
    private Switch mSwitchButton;
    // instance of broadcast receiver
    private ResponseReceiver mResponseReceiver;


    @Override
    public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
        AppLog.d("Switch Toggled");
        if (isChecked) {
            getServiceManager().startStepService();
        } else {
            getServiceManager().stopStepService();
        }
    }

    @Override
    public void onClick(final DialogInterface dialog, final int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                dismissAllProcessesAndHideDialog();
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                // cancel the notification, just in case
                getNotificationManager().cancelAlarmNotification();

                // reschedule alarm
                getAlarmManager().setAlarm(ALARM_MODE.SNOOZE);
                showDialog = false;
                mDialog.dismiss();
                mDialog = null;
                break;
            case DialogInterface.BUTTON_NEUTRAL:
                // cancel the notification, just in case
                getNotificationManager().cancelAlarmNotification();
                // TODO there is something wrong here
                getAlarmManager().setAlarm(ALARM_MODE.REPEAT);
                showDialog = false;
                mDialog.dismiss();
                mDialog = null;
                break;
            default:
                dismissAllProcessesAndHideDialog();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        // Set up the Switch Button
        final MenuItem switchMenuItem = menu.getItem(0);

        mSwitchButton = (Switch) switchMenuItem.getActionView().findViewById(
                R.id.switchForActionBar);

        mSwitchButton.setChecked(ErgoStepService.isServiceRunning());
        mSwitchButton.setOnCheckedChangeListener(this);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final int id = item.getItemId();
        if (id == R.id.menu_settings) {
            // start the shared preferences activity
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else {
            AppLog.e("We should not be here!");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
        // Do nothing
    }

    @Override
    public void onPageSelected(final int position) {
        // Change the selected page
        getSupportActionBar().setSelectedNavigationItem(position);
    }

    @Override
    public void onPageScrollStateChanged(final int state) {
        // Do nothing

    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
        if (key.equals(getString(R.string.pref_theme_key))) {
            // Use this to recreate the main activity with the new theme
            AppLog.d("Theme Changed. Recreating Main Activity");
            recreate();
        } else if (key.equals(getString(R.string.pref_alarm_interval_key))) {
            // Cancel both the service and the alarm if user changes the settings
            getServiceManager().stopStepService();
            getAlarmManager().cancelAlarm();
        }
    }

    @Override
    public void onTabSelected(final Tab tab, final FragmentTransaction fragmentTransaction) {
        // set viewpager position
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(final Tab tab, final FragmentTransaction fragmentTransaction) {
        // do nothing
    }

    @Override
    public void onTabReselected(final Tab tab, final FragmentTransaction fragmentTransaction) {
        // do nothing
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        // switch activity theme according to settings
        getPreferenceHandler().changeAppTheme();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mToastHelper = new ToastHelper(this);


        // look out for Theme changes from SharedPreferences
        getPreferenceHandler().getAppSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        // Set up the action bar
        AppLog.d("Creating Action Bar");
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set ViewPager
        AppLog.d("Setting ViewPager");
        final CustomFragmentAdapter customAdapter = new CustomFragmentAdapter(this);
        mViewPager = (CustomViewPager) findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(customAdapter);
        mViewPager.setOnPageChangeListener(this);

        // Initialise the adapter
        AppLog.d("Setting Adapter");

        Tab viewpagerTab; // parameter for each tab
        Fragment genfragment; // parameter for each generated fragment

        int tabPosition = 0; // count for fragment position

        for (final String tabTitle : getResources().getStringArray(R.array.array_viewpager_titles)) {
            viewpagerTab = getSupportActionBar().newTab();
            switch (tabPosition) {
                case 0:
                    genfragment = ErgoClockFragment.newInstance(tabPosition);
                    break;
                case 1:
                    genfragment = ErgoGraphFragment.newInstance(tabPosition);
                    break;
                default:
                    genfragment = ErgoClockFragment.newInstance(tabPosition);
                    break;
            }
            tabPosition++; // increment the position

            // Finalise the tab
            viewpagerTab.setText(tabTitle);
            viewpagerTab.setTabListener(this);

            // Add the tab to the Action Bar
            getSupportActionBar().addTab(viewpagerTab);

            // Add the fragment to the Adapter
            customAdapter.add(genfragment, tabTitle);
        }

        // parse user data
        if (getDataManager().retrieveDailyData() == null || getDataManager().retrieveDailyData().size() == 0) {
            AppLog.d("Retrieving User Data");
            getFileHelper().parseFromFileByLine();
        }

        // Set the adapter to the ViewPager
        mViewPager.getAdapter().notifyDataSetChanged();

        if (savedInstanceState != null) {
            showDialog = savedInstanceState.getBoolean(Payloads.PAYLOAD_1.getString());
        } else {
            final Bundle intentBundle = getIntent().getExtras();
            if (intentBundle != null) {
                showDialog = intentBundle.getBoolean(Payloads.PAYLOAD_1.getString());
            }
        }
        AppLog.d("onCreate finished");
    }

    @Override
    protected void onDestroy() {
        getNotificationManager().cancelAlarmNotification();
        unregisterResponseReceiver();
        super.onDestroy();
    }

    @Override
    public boolean onMenuOpened(final int featureId, final Menu menu) {
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    final Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (final NoSuchMethodException e) {
                    AppLog.e("onMenuOpened " + e);
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        // store the state of the alarm dialog
        outState.putBoolean(Payloads.PAYLOAD_1.getString(), showDialog);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        mToastHelper.dismissActiveToasts();
        isActivityShown = false;
        super.onPause();
    }

    ;

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        // Read the extras from a new intent
        AppLog.d("Received onNewIntent");
        showDialog = getIntent().getBooleanExtra(Payloads.PAYLOAD_1.getString(), false);
        if (showDialog) {
            enableAlarmDialog();
        }
    }

    @Override
    protected void onResume() {
        registerResponseReceiver();

        if (showDialog) {
            enableAlarmDialog();
        }

        isActivityShown = true;
        super.onResume();
    }

    private Dialog createDialog() {
        /** Turn Screen On and Unlock the keypad when this alert dialog is displayed */
        this.getWindow().addFlags(LayoutParams.FLAG_TURN_SCREEN_ON | LayoutParams.FLAG_DISMISS_KEYGUARD);

        /** Creating a alert dialog builder */
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Disable dismiss
        builder.setCancelable(false);

        /** Setting title for the alert dialog */
        builder.setTitle(getString(R.string.dialog_alarm_title));

        /** Setting the content for the alert dialog */
        builder.setMessage(getString(R.string.dialog_alarm_body));

        /** Defining an OK button event listener */
        builder.setPositiveButton("Dismiss", this);
        builder.setNegativeButton("Snooze", this);
        builder.setNeutralButton("Repeat", this);

        /** Creating the alert dialog window */
        return builder.create();
    }

    /**
     * Dismisses the Alarm, stops the StepService and dismisses the dialog
     */
    private void dismissAllProcessesAndHideDialog() {

        // Stop the scanning service
        getServiceManager().stopStepService();
        // cancel the notification, just in case
        getNotificationManager().cancelAlarmNotification();

        showDialog = false;

        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
        mSwitchButton.setChecked(false);
    }

    /**
     * Creates and shows the alarm dialog
     */
    private void enableAlarmDialog() {
        if (mDialog == null) {
            mDialog = createDialog();
        }
        mDialog.show();
        showDialog = true;
    }

    /**
     *
     */
    private void registerResponseReceiver() {
        unregisterResponseReceiver();

        AppLog.d("Registering Response Receiver");
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Broadcasts.ALARM_TRIGGERED.getString());
        intentFilter.addAction(Broadcasts.CLOCK_MODE_CHANGED.getString());
        intentFilter.addAction(Broadcasts.ALARM_STOPPED.getString());
        mResponseReceiver = new ResponseReceiver();
        registerReceiver(mResponseReceiver, intentFilter);
    }

    /**
     *
     */
    private void unregisterResponseReceiver() {
        if (mResponseReceiver == null) {
            return;
        }
        try {
            unregisterReceiver(mResponseReceiver);
            AppLog.d("Receiver Unregistered Successfully");
        } catch (final Exception e) {
            AppLog.e(String.format("Response Receiver Not Registered or Already Unregistered. Exception : %s", e.getLocalizedMessage()));
        }
    }

    /**
     * Custom broadcast receiver for handling broadcasts in the activity
     *
     * @author Michael Fotiadis
     */
    private class ResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            if (action.equalsIgnoreCase(Broadcasts.ALARM_TRIGGERED.getString())) {
                AppLog.d("Alarm Triggered");
                if (isActivityShown) {
                    enableAlarmDialog();
                } else {
                    showDialog = true;
                }
            } else if (action.equalsIgnoreCase(Broadcasts.CLOCK_MODE_CHANGED.getString())) {
                // display a toast depending on the extra payload
                final ALARM_MODE mode = (ALARM_MODE) intent.getSerializableExtra(Payloads.CLOCK_MODE_PAYLOAD.getString());
                if (mode != null && isActivityShown) {
                    // call the utils to show toast
                    mToastHelper.makeToast(mode);
                }
            } else if (action.equalsIgnoreCase(Broadcasts.ALARM_STOPPED.getString())) {
                AppLog.d("Received Alarm Stopped in Main");
                dismissAllProcessesAndHideDialog();
            }
        }
    }
}

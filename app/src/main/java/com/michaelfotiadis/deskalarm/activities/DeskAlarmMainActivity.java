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
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.michaelfotiadis.deskalarm.R;
import com.michaelfotiadis.deskalarm.adapters.CustomFragmentAdapter;
import com.michaelfotiadis.deskalarm.adapters.CustomViewPager;
import com.michaelfotiadis.deskalarm.constants.AppConstants;
import com.michaelfotiadis.deskalarm.fragments.ErgoClockFragment;
import com.michaelfotiadis.deskalarm.fragments.ErgoGraphFragment;
import com.michaelfotiadis.deskalarm.managers.ErgoAlarmManager;
import com.michaelfotiadis.deskalarm.managers.ErgoAlarmManager.ALARM_MODE;
import com.michaelfotiadis.deskalarm.managers.ErgoDataManager;
import com.michaelfotiadis.deskalarm.managers.ErgoNotificationManager;
import com.michaelfotiadis.deskalarm.managers.ErgoServiceManager;
import com.michaelfotiadis.deskalarm.services.ErgoStepService;
import com.michaelfotiadis.deskalarm.utils.AppUtils;
import com.michaelfotiadis.deskalarm.utils.FileUtils;
import com.michaelfotiadis.deskalarm.utils.Logger;
import com.michaelfotiadis.deskalarm.utils.ToastUtils;

import java.lang.reflect.Method;

public class DeskAlarmMainActivity extends ActionBarActivity implements
        OnCheckedChangeListener, TabListener, OnPageChangeListener,
        OnClickListener, OnSharedPreferenceChangeListener {

    /**
     * Custom broadcast receiver for handling broadcasts in the activity
     *
     * @author Michael Fotiadis
     */
    private class ResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            if (action.equalsIgnoreCase(
                    AppConstants.Broadcasts.ALARM_TRIGGERED.getString())) {
                Logger.d(TAG, "Alarm Triggered");
                if (isActivityShown) {
                    enableAlarmDialog();
                } else {
                    showDialog = true;
                }
            } else if (action.equalsIgnoreCase(AppConstants.Broadcasts.CLOCK_MODE_CHANGED.getString())) {
                // display a toast depending on the extra payload
                ALARM_MODE mode = (ALARM_MODE) intent.getSerializableExtra(
                        AppConstants.Payloads.CLOCK_MODE_PAYLOAD.getString());
                if (mode != null && isActivityShown) {
                    // call the utils to show toast
                    mSuperActivityToast = new ToastUtils().makeToast(DeskAlarmMainActivity.this, mode);
                }
            } else if (action.equalsIgnoreCase(AppConstants.Broadcasts.ALARM_STOPPED.getString())) {
                Logger.d(TAG, "Received Alarm Stopped in Main");
                dismissAllProcessesAndHideDialog();
            }
        }
    }

    private Dialog mDialog;
    private boolean showDialog = false;
    private boolean isActivityShown = false;

    private SuperActivityToast mSuperActivityToast;

    private final String TAG = "Main Activity";

    private CustomViewPager mViewPager;
    private Switch mSwitchButton;

    // instance of broadcast receiver
    private ResponseReceiver mResponseReceiver;

    private Dialog createDialog() {
        /** Turn Screen On and Unlock the keypad when this alert dialog is displayed */
        this.getWindow().addFlags(LayoutParams.FLAG_TURN_SCREEN_ON | LayoutParams.FLAG_DISMISS_KEYGUARD);

        /** Creating a alert dialog builder */
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

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
        new ErgoServiceManager().stopStepService(this);
        // cancel the notification, just in case
        new ErgoNotificationManager().cancelAlarmNotification(this);

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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Logger.d(TAG, "Switch Toggled");
        if (isChecked) {
            new ErgoServiceManager().startStepService(getApplicationContext());
        } else {
            new ErgoServiceManager().stopStepService(getApplicationContext());
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
                new ErgoNotificationManager().cancelAlarmNotification(this);

                // reschedule alarm
                new ErgoAlarmManager().setAlarm(getApplicationContext(), ALARM_MODE.SNOOZE);
                showDialog = false;
                mDialog.dismiss();
                mDialog = null;
                break;
            case DialogInterface.BUTTON_NEUTRAL:
                // cancel the notification, just in case
                new ErgoNotificationManager().cancelAlarmNotification(this);
                // TODO there is something wrong here
                new ErgoAlarmManager().setAlarm(getApplicationContext(), ALARM_MODE.REPEAT);
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
    protected void onCreate(Bundle savedInstanceState) {
        // switch activity theme according to settings
        new AppUtils().changeAppTheme(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // look out for Theme changes from SharedPreferences
        new AppUtils().getAppSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

        // Set up the action bar
        Logger.d(TAG, "Creating Action Bar");
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set ViewPager
        Logger.d(TAG, "Setting ViewPager");
        final CustomFragmentAdapter customAdapter = new CustomFragmentAdapter(this);
        mViewPager = (CustomViewPager) findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(customAdapter);
        mViewPager.setOnPageChangeListener(this);

        // Initialise the adapter
        Logger.d(TAG, "Setting Adapter");

        Tab viewpagerTab; // parameter for each tab
        Fragment genfragment; // parameter for each generated fragment

        int tabPosition = 0; // count for fragment position

        for (String tabTitle : getResources().getStringArray(R.array.array_viewpager_titles)) {
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
        ErgoDataManager dataManager = new ErgoDataManager(getApplicationContext());
        if (dataManager.retrieveDailyData() == null || dataManager.retrieveDailyData().size() == 0) {
            Logger.d(TAG, "Retrieving User Data");
            new FileUtils().parseFromFileByLine(getApplicationContext());
        }

        // Set the adapter to the ViewPager
        mViewPager.getAdapter().notifyDataSetChanged();

        if (savedInstanceState != null) {
            showDialog = savedInstanceState.getBoolean(AppConstants.Payloads.PAYLOAD_1.getString());
        } else {
            Bundle intentBundle = getIntent().getExtras();
            if (intentBundle != null) {
                showDialog = intentBundle.getBoolean(AppConstants.Payloads.PAYLOAD_1.getString());
            }
        }
        Logger.d(TAG, "onCreate finished");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        new ErgoNotificationManager().cancelAlarmNotification(this);
        unregisterResponseReceiver();
        super.onDestroy();
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (NoSuchMethodException e) {
                    Logger.e(TAG, "onMenuOpened", e);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // Read the extras from a new intent
        Logger.d(TAG, "Received onNewIntent");
        showDialog = getIntent().getBooleanExtra(AppConstants.Payloads.PAYLOAD_1.getString(), false);
        if (showDialog) {
            enableAlarmDialog();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_settings) {
            // start the shared preferences activity
            startActivity(new Intent(this, ErgoPreferencesActivity.class));
            return true;
        } else {
            Logger.e(TAG, "We should not be here!");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // Do nothing
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // Do nothing

    }

    @Override
    public void onPageSelected(int position) {
        // Change the selected page
        getSupportActionBar().setSelectedNavigationItem(position);
    }

    @Override
    protected void onPause() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        if (mSuperActivityToast != null && mSuperActivityToast.isShowing()) {
            mSuperActivityToast.dismiss();
        }

        isActivityShown = false;
        super.onPause();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Set up the Switch Button
        MenuItem switchMenuItem = menu.getItem(0);

        mSwitchButton = (Switch) switchMenuItem.getActionView().findViewById(
                R.id.switchForActionBar);

        mSwitchButton.setChecked(ErgoStepService.isServiceRunning());
        mSwitchButton.setOnCheckedChangeListener(this);
        return super.onPrepareOptionsMenu(menu);
    }

    ;

    @Override
    protected void onResume() {
        registerResponseReceiver();

        if (showDialog) {
            enableAlarmDialog();
        }

        isActivityShown = true;
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // store the state of the alarm dialog
        outState.putBoolean(AppConstants.Payloads.PAYLOAD_1.getString(), showDialog);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_theme_key))) {
            // Use this to recreate the main activity with the new theme
            Logger.d(TAG, "Theme Changed. Recreating Main Activity");
            recreate();
        } else if (key.equals(getString(R.string.pref_alarm_interval_key))) {
            // Cancel both the service and the alarm if user changes the settings
            new ErgoServiceManager().stopStepService(this);
            new ErgoAlarmManager().cancelAlarm(this);
        }
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction fragmentTransaction) {
        // do nothing
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction fragmentTransaction) {
        // set viewpager position
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction fragmentTransaction) {
        // do nothing
    }


    /**
     *
     */
    private void registerResponseReceiver() {
        unregisterResponseReceiver();

        Logger.d(TAG, "Registering Response Receiver");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppConstants.Broadcasts.ALARM_TRIGGERED.getString());
        intentFilter.addAction(AppConstants.Broadcasts.CLOCK_MODE_CHANGED.getString());
        intentFilter.addAction(AppConstants.Broadcasts.ALARM_STOPPED.getString());
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
            Logger.d(TAG, "Receiver Unregistered Successfully");
        } catch (Exception e) {
            Logger.e(TAG,
                    "Response Receiver Not Registered or Already Unregistered. Exception : "
                            + e.getLocalizedMessage());
        }
    }
}

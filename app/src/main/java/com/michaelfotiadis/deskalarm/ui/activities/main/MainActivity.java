package com.michaelfotiadis.deskalarm.ui.activities.main;

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
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.michaelfotiadis.deskalarm.R;
import com.michaelfotiadis.deskalarm.model.Broadcasts;
import com.michaelfotiadis.deskalarm.model.Payloads;
import com.michaelfotiadis.deskalarm.services.step.StepService;
import com.michaelfotiadis.deskalarm.ui.activities.settings.SettingsActivity;
import com.michaelfotiadis.deskalarm.ui.base.activity.BaseActivity;
import com.michaelfotiadis.deskalarm.ui.base.core.AlarmManager.ALARM_MODE;
import com.michaelfotiadis.deskalarm.ui.base.viewpager.SmartFragmentPagerAdapter;
import com.michaelfotiadis.deskalarm.ui.base.viewpager.SmartFragmentPagerBinder;
import com.michaelfotiadis.deskalarm.ui.base.viewpager.SmartFragmentPagerPages;
import com.michaelfotiadis.deskalarm.utils.ToastHelper;
import com.michaelfotiadis.deskalarm.utils.log.AppLog;

import java.lang.reflect.Method;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements OnSharedPreferenceChangeListener {

    private static final int OFF_PAGE_LIMIT = 1;

    @BindView(R.id.view_pager)
    protected ViewPager mPager;
    @BindView(R.id.tabs)
    protected TabLayout mTabLayout;

    private Switch mSwitchButton;

    private SmartFragmentPagerAdapter mPagerAdapter;

    private Dialog mDialog;
    private boolean mIsShowingDialog = false;
    private boolean mIsActivityShown = false;
    private ToastHelper mToastHelper;

    // instance of broadcast receiver
    private ResponseReceiver mResponseReceiver;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        // switch activity theme according to settings
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        mToastHelper = new ToastHelper(this);

        // look out for Theme changes from SharedPreferences
        getPreferenceHandler().registerOnSharedPreferenceChangeListener(this);

        setUpViewPager();

        if (savedInstanceState != null) {
            mIsShowingDialog = savedInstanceState.getBoolean(Payloads.PAYLOAD_1.getString());
        } else {
            final Bundle intentBundle = getIntent().getExtras();
            if (intentBundle != null) {
                mIsShowingDialog = intentBundle.getBoolean(Payloads.PAYLOAD_1.getString());
            }
        }
        AppLog.d("onCreate finished");
    }

    private void setUpViewPager() {
        final HomeTabsFactory tabsFactory = new HomeTabsFactory(this);
        final SmartFragmentPagerPages pages = tabsFactory.getPages();
        mPagerAdapter = new SmartFragmentPagerAdapter(getSupportFragmentManager());
        mPagerAdapter.setFragments(pages);
        mPager.setAdapter(mPagerAdapter);
        mPager.setOffscreenPageLimit(OFF_PAGE_LIMIT);

        SmartFragmentPagerBinder binder = new SmartFragmentPagerBinder(mPager, pages, mTabLayout,
                new SmartFragmentPagerBinder.NavBarTitleNeedsChangingListener() {
                    @Override
                    public void onNavBarTitleNeedsChanging(final CharSequence newTitle) {
                        setTitle(newTitle);
                    }
                });

        binder.bind();
        mPager.setCurrentItem(0);
        // The onPageSelectedEvent of OnPageChangeListener is not called for the first page
        binder.onPageSelected(0);

    }


    private Fragment getCurrentFragment() {
        return mPagerAdapter.getItem(mPager.getCurrentItem());
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
        mSwitchButton = (Switch) switchMenuItem.getActionView().findViewById(R.id.switchForActionBar);
        mSwitchButton.setChecked(StepService.isServiceRunning());
        mSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppLog.d("Switch Toggled");
                if (isChecked) {
                    getServiceManager().startStepService();
                } else {
                    getServiceManager().stopStepService();
                }
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final int id = item.getItemId();
        if (id == R.id.menu_settings) {
            // start the shared prefs activity
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else {
            AppLog.e("We should not be here!");
        }
        return super.onOptionsItemSelected(item);
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
        } else {
            recreate();
        }
    }


    @Override
    protected void onDestroy() {
        getPreferenceHandler().unregisterOnSharedPreferenceChangeListener(this);
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
        outState.putBoolean(Payloads.PAYLOAD_1.getString(), mIsShowingDialog);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        mToastHelper.dismissActiveToasts();
        mIsActivityShown = false;
        super.onPause();
    }

    ;

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        // Read the extras from a new intent
        AppLog.d("Received onNewIntent");
        mIsShowingDialog = getIntent().getBooleanExtra(Payloads.PAYLOAD_1.getString(), false);
        if (mIsShowingDialog) {
            enableAlarmDialog();
        }
    }

    @Override
    protected void onResume() {
        registerResponseReceiver();

        if (mIsShowingDialog) {
            enableAlarmDialog();
        }

        mIsActivityShown = true;
        super.onResume();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_default_view_pager;
    }

    private Dialog createDialog() {
        // TODO alert dialog factory
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
        builder.setPositiveButton("Dismiss", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismissAllProcessesAndHideDialog();
            }
        });
        builder.setNegativeButton("Snooze", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // cancel the notification, just in case
                getNotificationManager().cancelAlarmNotification();

                // reschedule alarm
                getAlarmManager().setAlarm(ALARM_MODE.SNOOZE);
                mIsShowingDialog = false;
                mDialog.dismiss();
                mDialog = null;
            }
        });
        builder.setNeutralButton("Repeat", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // cancel the notification, just in case
                getNotificationManager().cancelAlarmNotification();
                // TODO there is something wrong here
                getAlarmManager().setAlarm(ALARM_MODE.REPEAT);
                mIsShowingDialog = false;
                mDialog.dismiss();
                mDialog = null;
            }
        });

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

        mIsShowingDialog = false;

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
        mIsShowingDialog = true;
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
                if (mIsActivityShown) {
                    enableAlarmDialog();
                } else {
                    mIsShowingDialog = true;
                }
            } else if (action.equalsIgnoreCase(Broadcasts.CLOCK_MODE_CHANGED.getString())) {
                // display a toast depending on the extra payload
                final ALARM_MODE mode = (ALARM_MODE) intent.getSerializableExtra(Payloads.CLOCK_MODE_PAYLOAD.getString());
                if (mode != null && mIsActivityShown) {
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

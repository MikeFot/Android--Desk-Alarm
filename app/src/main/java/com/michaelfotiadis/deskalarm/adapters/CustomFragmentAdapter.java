package com.michaelfotiadis.deskalarm.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.michaelfotiadis.deskalarm.containers.ErgoFragmentInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment Adapter for handling Instantiated Fragments instead of Classes
 *
 * @author Michael Fotiadis
 */
public class CustomFragmentAdapter extends FragmentStatePagerAdapter {

    private final List<String> mTitleList; // array of titles
    private final List<ErgoFragmentInfo> mTabs; // array of custom FragmentInfo objects

    private final Context mContext;

    /**
     * Constructor for the custom fragment adapter
     *
     * @param context Context
     */
    public CustomFragmentAdapter(final FragmentActivity context) {
        super(context.getSupportFragmentManager());
        mTabs = new ArrayList<ErgoFragmentInfo>();
        mTitleList = new ArrayList<String>();

        mContext = context;
    }

    /**
     * Adds a fragment to the tabs with the given title
     *
     * @param fragment Fragment
     * @param title    String
     */
    public void add(final Fragment fragment, final String title) {
        if (fragment == null) {
            return;
        }

        mTabs.add(new ErgoFragmentInfo(fragment));
        mTitleList.add(title);
        notifyDataSetChanged();
    }

    /**
     * Returns the size of the Tabs
     */
    @Override
    public int getCount() {
        return mTabs.size();
    }

    /**
     * Returns Fragment at specified position
     */
    @Override
    public Fragment getItem(final int position) {
        final ErgoFragmentInfo info = mTabs.get(position);

        if (info.getFrag() == null) {
            return Fragment.instantiate(mContext, info.getClss().getName(),
                    info.getArgs());
        } else {
            return info.getFrag();
        }
    }
}
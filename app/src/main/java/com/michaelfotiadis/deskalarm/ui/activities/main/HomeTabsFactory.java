package com.michaelfotiadis.deskalarm.ui.activities.main;

import com.michaelfotiadis.deskalarm.R;
import com.michaelfotiadis.deskalarm.ui.base.activity.BaseActivity;
import com.michaelfotiadis.deskalarm.ui.base.viewpager.SmartFragmentPagerPage;
import com.michaelfotiadis.deskalarm.ui.base.viewpager.SmartFragmentPagerPages;
import com.michaelfotiadis.deskalarm.ui.fragments.ClockFragment;
import com.michaelfotiadis.deskalarm.ui.fragments.GraphFragment;

/**
 *
 */
/*package*/ class HomeTabsFactory {

    private final BaseActivity mActivity;

    HomeTabsFactory(final BaseActivity activity) {
        this.mActivity = activity;
    }

    SmartFragmentPagerPages getPages() {
        final SmartFragmentPagerPages pages = new SmartFragmentPagerPages();

        final String[] titles = mActivity.getResources().getStringArray(R.array.array_viewpager_titles);

        pages.add(getClockPage(titles[0]));

        pages.add(getGraphPage(titles[1]));
        return pages;
    }

    private static SmartFragmentPagerPage getClockPage(final String title) {

        return new SmartFragmentPagerPage.Builder()
                .withNavBarTitle(title)
                .withTabIcon(R.drawable.ic_access_time_black)
                .withFragment(ClockFragment.newInstance())
                .withTabTitle(title).build();
    }

    private static SmartFragmentPagerPage getGraphPage(final String title) {

        return new SmartFragmentPagerPage.Builder()
                .withNavBarTitle(title)
                .withTabIcon(R.drawable.ic_timeline_black_24dp)
                .withFragment(GraphFragment.newInstance())
                .withTabTitle(title).build();
    }

}
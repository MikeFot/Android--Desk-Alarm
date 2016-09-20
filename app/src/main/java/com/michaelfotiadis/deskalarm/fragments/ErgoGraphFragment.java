package com.michaelfotiadis.deskalarm.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.michaelfotiadis.deskalarm.R;
import com.michaelfotiadis.deskalarm.constants.AppConstants;
import com.michaelfotiadis.deskalarm.containers.ErgoTimeDataInstance;
import com.michaelfotiadis.deskalarm.managers.ErgoDataManager;
import com.michaelfotiadis.deskalarm.utils.AppUtils;
import com.michaelfotiadis.deskalarm.utils.Logger;
import com.michaelfotiadis.deskalarm.views.ErgoGraphicalViewBuilder;

import org.achartengine.GraphicalView;
import org.achartengine.model.SeriesSelection;

import java.util.SortedMap;

public class ErgoGraphFragment extends Fragment implements OnClickListener {

    private class ResponseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Logger.d(TAG, "On Receiver Result");
            if (intent.getAction().equalsIgnoreCase(
                    AppConstants.Broadcasts.DATA_CHANGED.getString())) {
                Logger.d(TAG, "Got Data Changed to Graph Fragment");
                generateChart();
            }
        }
    }

    /**
     * Fragment constructor
     *
     * @param position integer position of the fragment on the ViewPager
     * @return
     */
    public static ErgoGraphFragment newInstance(final int position) {
        ErgoGraphFragment eFragment = new ErgoGraphFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        eFragment.setArguments(b);

        return eFragment;
    }

    private static final String ARG_POSITION = "position";
    private final String TAG = "Fragment Graph";

    private ResponseReceiver mResponseReceiver;

    // TextView to display in case graph data is null
    private TextView mInformationTextView;

    private GraphicalView mChartView;

    // Button Fields
    private ImageButton mZoomExtentsButton;
    private ImageButton mZoomInButton;
    private ImageButton mZoomOutButton;
    private LinearLayout mZoomLayout;

    // linear layout containing a GraphicalView
    private LinearLayout mChartContainerLayout;

    private int mDefaultTextViewColor;

    /**
     * Method which generates the Chart View
     */
    private void generateChart() {
        mChartContainerLayout.removeAllViews();

        mChartView = new ErgoGraphicalViewBuilder().generateChart(getActivity());

        // Adding click event to the Line Chart.
        mChartView.setOnClickListener(this);

        mChartView.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mInformationTextView.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        // Add the graphical view mChart object into the Linear layout
        mChartContainerLayout.addView(mInformationTextView);
        mChartContainerLayout.addView(mChartView);

        // hide the graph if there is no data
        final SortedMap<String, ErgoTimeDataInstance> data = new ErgoDataManager(getActivity()).retrieveDailyData();
        if (data.size() == 0) {
            mInformationTextView.setText(R.string.label_no_data_yet);
            mChartView.setVisibility(View.GONE);
            mZoomLayout.setVisibility(View.GONE);
        } else {
            mInformationTextView.setText(R.string.no_selection);
            mChartView.setVisibility(View.VISIBLE);
            mZoomLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Allows retrieval of the constructor arguments
     *
     * @return
     */
    public int getConstructorArguments() {
        return getArguments().getInt(ARG_POSITION);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(final View view) {
        if (view.hashCode() == mChartView.hashCode()) {
            doSeriesSelection();
        } else if (view.hashCode() == mZoomExtentsButton.hashCode()) {
            mChartView.zoomReset();
        } else if (view.hashCode() == mZoomInButton.hashCode()) {
            mChartView.zoomIn();
        } else if (view.hashCode() == mZoomOutButton.hashCode()) {
            mChartView.zoomOut();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.d(TAG, "CREATED FRAGMENT WITH " + getConstructorArguments());

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_graph, container, false);

        mChartContainerLayout = (LinearLayout) view.findViewById(R.id.chart_layout);

        mInformationTextView = (TextView) view.findViewById(R.id.text_view_no_data);
        mDefaultTextViewColor = mInformationTextView.getTextColors().getDefaultColor();

        mZoomLayout = (LinearLayout) view.findViewById(R.id.zoom_layout);

        mZoomExtentsButton = (ImageButton) view.findViewById(R.id.custom_zoom_reset);
        mZoomExtentsButton.setOnClickListener(this);
        mZoomInButton = (ImageButton) view.findViewById(R.id.custom_zoom_in);
        mZoomInButton.setOnClickListener(this);
        mZoomOutButton = (ImageButton) view.findViewById(R.id.custom_zoom_out);
        mZoomOutButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterResponseReceiver();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerResponseReceiver();

        generateChart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void doSeriesSelection() {
        SeriesSelection series_selection = mChartView
                .getCurrentSeriesAndPoint();

        if (series_selection != null) {

            // get the key for the selected index
            String key = new ErgoDataManager(getActivity())
                    .retrieveDailyData()
                    .keySet()
                    .toArray(
                            new String[new ErgoDataManager(getActivity())
                                    .retrieveDailyData().size()])
                    [(int) series_selection.getXValue()];

            StringBuilder sb = new StringBuilder();
            sb.append("Selected : ");
            sb.append(key);
            sb.append(" - ");
            sb.append((int) series_selection.getValue());

            if (series_selection.getValue() <= 1) {
                sb.append(" minute");
            } else {
                sb.append(" minutes");
            }
            int textColour;

            final int interval = new AppUtils().getAppSharedPreferences(getActivity())
                    .getInt(getActivity().getString(
                            R.string.pref_alarm_interval_key), 1);

            // calculate a threshold for the graph gradient limit
            int threshold = (int) (interval * 0.1);
            if (threshold < 1) {
                threshold = 1;
            }

            if (series_selection.getValue() <= threshold) {
                textColour = getResources().getColor(R.color.holo_green_dark);
            } else {
                textColour = getResources().getColor(R.color.holo_orange_dark);
            }
            mInformationTextView.setTextColor(textColour);
            mInformationTextView.setText(sb.toString());
        } else {
            mInformationTextView.setTextColor(mDefaultTextViewColor);
            mInformationTextView.setText(R.string.no_selection);
        }
    }

    private void registerResponseReceiver() {
        unregisterResponseReceiver();

        Logger.d(TAG, "Registering Response Receiver");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppConstants.Broadcasts.DATA_CHANGED
                .getString());
        mResponseReceiver = new ResponseReceiver();
        getActivity().registerReceiver(mResponseReceiver, intentFilter);
    }

    private void unregisterResponseReceiver() {
        if (mResponseReceiver == null) {
            return;
        }

        try {
            getActivity().unregisterReceiver(mResponseReceiver);
            Logger.d(TAG, "Receiver Unregistered Successfully");
        } catch (Exception e) {
            Logger.d(TAG,
                    "Response Receiver Not Registered or Already Unregistered. Exception : "
                            + e.getLocalizedMessage());
        }
    }

}

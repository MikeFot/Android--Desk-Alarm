package com.michaelfotiadis.deskalarm.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.michaelfotiadis.deskalarm.R;
import com.michaelfotiadis.deskalarm.containers.TimeModel;
import com.michaelfotiadis.deskalarm.model.Broadcasts;
import com.michaelfotiadis.deskalarm.ui.base.core.preference.PreferenceHandler;
import com.michaelfotiadis.deskalarm.ui.base.core.preference.PreferenceHandlerImpl;
import com.michaelfotiadis.deskalarm.ui.base.fragment.BaseFragment;
import com.michaelfotiadis.deskalarm.utils.log.AppLog;
import com.michaelfotiadis.deskalarm.views.GraphFactory;

import org.achartengine.GraphicalView;
import org.achartengine.model.SeriesSelection;

import java.util.SortedMap;

public class GraphFragment extends BaseFragment implements OnClickListener {

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

    private PreferenceHandler mPreferenceHandler;

    /**
     * Fragment constructor
     *
     * @return {@link BaseFragment}
     */
    public static BaseFragment newInstance() {
        return new GraphFragment();
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
    public void onAttach(final Context context) {
        super.onAttach(context);
        mPreferenceHandler = new PreferenceHandlerImpl(context);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

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
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        registerResponseReceiver();

        generateChart();
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterResponseReceiver();
    }

    /**
     * Method which generates the Chart View
     */
    private void generateChart() {
        mChartContainerLayout.removeAllViews();

        mChartView = new GraphFactory().generateChart(getActivity(), getPreferenceHandler(), getDataManager());

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
        final SortedMap<String, TimeModel> data = getDataManager().retrieveDailyData();
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

    private void doSeriesSelection() {
        final SeriesSelection series_selection = mChartView
                .getCurrentSeriesAndPoint();

        if (series_selection != null) {

            // get the key for the selected index
            final String key = getDataManager()
                    .retrieveDailyData()
                    .keySet()
                    .toArray(
                            new String[getDataManager()
                                    .retrieveDailyData().size()])
                    [(int) series_selection.getXValue()];

            final StringBuilder sb = new StringBuilder();
            sb.append("Selected : ");
            sb.append(key);
            sb.append(" - ");
            sb.append((int) series_selection.getValue());

            if (series_selection.getValue() <= 1) {
                sb.append(" minute");
            } else {
                sb.append(" minutes");
            }
            final int textColour;

            final int interval = mPreferenceHandler.getInt(PreferenceHandlerImpl.PreferenceKey.ALARM_INTERVAL);
            // calculate a threshold for the graph gradient limit
            int threshold = (int) (interval * 0.1);
            if (threshold < 1) {
                threshold = 1;
            }

            if (series_selection.getValue() <= threshold) {
                textColour = getResources().getColor(R.color.graph_green);
            } else {
                textColour = getResources().getColor(R.color.graph_orange);
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

        AppLog.d("Registering Response Receiver");
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Broadcasts.DATA_CHANGED
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
            AppLog.d("Receiver Unregistered Successfully");
        } catch (final Exception e) {
            AppLog.d(
                    "Response Receiver Not Registered or Already Unregistered. Exception : "
                            + e.getLocalizedMessage());
        }
    }

    private class ResponseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            AppLog.d("On Receiver Result");
            if (intent.getAction().equalsIgnoreCase(Broadcasts.DATA_CHANGED.getString())) {
                AppLog.d("Got Data Changed to Graph Fragment");
                generateChart();
            }
        }
    }

}

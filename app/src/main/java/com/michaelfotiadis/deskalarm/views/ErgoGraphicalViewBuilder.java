package com.michaelfotiadis.deskalarm.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.michaelfotiadis.deskalarm.R;
import com.michaelfotiadis.deskalarm.containers.ErgoTimeDataInstance;
import com.michaelfotiadis.deskalarm.ui.base.core.ErgoDataManager;
import com.michaelfotiadis.deskalarm.ui.base.core.PreferenceHandler;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.Map.Entry;
import java.util.SortedMap;

public class ErgoGraphicalViewBuilder {

    // graph fields
    private GraphicalView mChartView;
    private XYSeries mSeries;
    private XYMultipleSeriesRenderer mRenderer;
    private XYMultipleSeriesDataset mDataset;

    // factor for calculating axes-label margins
    private final int mMarginFactor = 2;

    // threshold used for calculating graph gradient and toast colour
    private int mWakeThreshold;

    // gradient fields
    private int mColorGradientStop;
    private int mColorGradientStart;
    private int mGradientLimit;

    public GraphicalView generateChart(final Context context,
                                       final PreferenceHandler preferenceHandler,
                                       final ErgoDataManager dataManager) {

        final int interval = preferenceHandler.getAppSharedPreferences()
                .getInt(context.getString(
                        R.string.pref_alarm_interval_key), 1);

        // calculate a threshold for the graph gradient limit
        mWakeThreshold = (int) (interval * 0.1);
        if (mWakeThreshold < 1) {
            mWakeThreshold = 1;
        }

        // gradient limit dependent on interval value plus wake threshold
        mGradientLimit = interval + mWakeThreshold * 2;

        // get font metrics
        final DisplayMetrics displayMetrics = context.getResources()
                .getDisplayMetrics();
        // TODO: "12" should be replaced with values.xml value
        final float fontSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 12, displayMetrics);

        // Create XYSeriesRenderer to customise XSeries
        final XYSeriesRenderer xyRenderer = new XYSeriesRenderer();

        // chart values dependent on fontSize
        xyRenderer.setDisplayChartValues(true);
        xyRenderer.setChartValuesTextSize(fontSize);
        xyRenderer.setChartValuesSpacing(fontSize / 2);
        xyRenderer.setChartValuesTextAlign(Align.CENTER);

        // stylistic stuff
        xyRenderer.setDisplayBoundingPoints(true);
        xyRenderer.setHighlighted(true);
        xyRenderer.setShowLegendItem(true);
        xyRenderer.setLineWidth(fontSize / 4);
        xyRenderer.setGradientEnabled(true);

        // Create XYMultipleSeriesRenderer to customise the whole chart
        mRenderer = new XYMultipleSeriesRenderer();

        // titles from resources
        mRenderer.setChartTitle(context.getString(R.string.graph_chart_title));
        mRenderer.setXTitle(context.getString(R.string.graph_x));
        mRenderer.setYTitle(context.getString(R.string.graph_y));

        // stylistic stuff
        mRenderer.setXRoundedLabels(true);
        mRenderer.setXLabels(0);
        mRenderer.setPanEnabled(true);
        mRenderer.setBarSpacing(fontSize / 3);
        mRenderer.setShowGrid(true);
        mRenderer.setClickEnabled(true);
        mRenderer.setShowGridX(true);
        mRenderer.setZoomRate(fontSize / 20);
        mRenderer.setZoomButtonsVisible(false);
        mRenderer.setExternalZoomEnabled(true);
        mRenderer.setZoomEnabled(true, false);

        // margins between axes and labels are set depending on fontSize settings
        mRenderer.setMargins(new int[]{(int) (fontSize * mMarginFactor),
                (int) (fontSize * mMarginFactor), (int) (fontSize * mMarginFactor),
                (int) (fontSize * mMarginFactor)});
        // set all fontSize related values
        mRenderer.setLabelsTextSize(fontSize);
        mRenderer.setAxisTitleTextSize(fontSize);
        mRenderer.setChartTitleTextSize(0);
        mRenderer.setLegendTextSize(fontSize);
        mRenderer.setFitLegend(true);
        mRenderer.setBarWidth(fontSize * 2);

        // Switch margins colour according to application theme
        final String defaultThemeValue = context.getString(R.string.pref_theme_default);
        final String themeKey = new PreferenceHandler(context).getAppSharedPreferences().
                getString(context.getString(R.string.pref_theme_key), defaultThemeValue);

        // default theme value should be "Dark", so use colours that contrast nicely
        if (themeKey.equals(defaultThemeValue)) {
            mRenderer.setMarginsColor(context.getResources().getColor(R.color.md_grey_600));
            mRenderer.setBackgroundColor(Color.GRAY);
            mRenderer.setAxesColor(Color.LTGRAY);
            mRenderer.setGridColor(Color.GRAY);
            mRenderer.setXLabelsColor(Color.WHITE);
            mRenderer.setYLabelsColor(0, Color.WHITE);
            mRenderer.setLabelsColor(Color.WHITE);

            // get colours from resources
            mColorGradientStart = context.getResources().getColor(R.color.md_green_400);
            mColorGradientStop = context.getResources().getColor(R.color.md_orange_300);

            // set renderer colours
            xyRenderer.setColor(mColorGradientStart);
            xyRenderer.setGradientStart(0, mColorGradientStart);
            xyRenderer.setGradientStop(mGradientLimit, mColorGradientStop);
        } else {
            // handle "Light" theme
            mRenderer.setMarginsColor(context.getResources().getColor(R.color.md_grey_200));
            mRenderer.setBackgroundColor(Color.DKGRAY);
            mRenderer.setAxesColor(Color.DKGRAY);
            mRenderer.setGridColor(Color.DKGRAY);
            mRenderer.setXLabelsColor(Color.DKGRAY);
            mRenderer.setYLabelsColor(0, Color.DKGRAY);
            mRenderer.setLabelsColor(Color.BLACK);

            // get colours from resources
            mColorGradientStart = context.getResources().getColor(R.color.md_green_400);
            mColorGradientStop = context.getResources().getColor(R.color.md_orange_300);

            // set renderer colours
            xyRenderer.setColor(mColorGradientStart);
            xyRenderer.setGradientStart(0, mColorGradientStart);
            xyRenderer.setGradientStop(mGradientLimit, mColorGradientStop);
        }

        // Get graph extents
        long maxY = Long.MIN_VALUE;
        mSeries = new XYSeries(context.getString(R.string.graph_title));
        // get the data from the data manager
        final SortedMap<String, ErgoTimeDataInstance> data = dataManager.retrieveDailyData();
        int i = 0;
        if (data != null) {
            // iterate through the entry set
            for (final Entry<String, ErgoTimeDataInstance> entry : data.entrySet()) {
                final String x = entry.getKey();
                final double y = entry.getValue().getTimeLogged();

                // find the maximum Y value
                if (maxY < y) {
                    maxY = (long) y;
                }
                // add labels
                mRenderer.addXTextLabel(i, String.valueOf(x));
                mSeries.add(i, y);
                mRenderer.addXTextLabel(i, x);
                i++;
            }
        }

        // set min-max values for proper graph display
        mRenderer.setXAxisMin(-1);
        mRenderer.setXAxisMax(i + 1);
        mRenderer.setYAxisMax(maxY + 0.3 * maxY);
        mRenderer.setYAxisMin(0);

        // set pan - zoom limits
        final double[] panLimits = new double[]{-3, i + 3, 0, maxY + 0.5 * maxY};
        mRenderer.setPanLimits(panLimits);
        mRenderer.setPanEnabled(true, false);
        mRenderer.setZoomLimits(panLimits);

        // add data to graph
        mDataset = new XYMultipleSeriesDataset();
        mDataset.addSeries(mSeries);

        // Adding the XSeriesRenderer to the MultipleRenderer.
        mRenderer.addSeriesRenderer(xyRenderer);

        // Creating an intent to plot line chart using dataset and multipleRenderer
        mChartView = ChartFactory.getBarChartView(context, mDataset, mRenderer, Type.STACKED);

        return mChartView;
    }

}

package com.michaelfotiadis.deskalarm.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.michaelfotiadis.deskalarm.R;
import com.michaelfotiadis.deskalarm.containers.TimeModelInstance;
import com.michaelfotiadis.deskalarm.ui.base.core.ErgoDataManager;
import com.michaelfotiadis.deskalarm.ui.base.core.preference.PreferenceHandler;
import com.michaelfotiadis.deskalarm.ui.base.core.preference.PreferenceHandlerImpl;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.Map.Entry;
import java.util.SortedMap;

public class GraphFactory {

    @SuppressWarnings("MethodMayBeStatic")
    public GraphicalView generateChart(final Context context,
                                       final PreferenceHandler preferenceHandler,
                                       final ErgoDataManager dataManager) {

        final int interval = preferenceHandler.getInt(PreferenceHandlerImpl.PreferenceKey.ALARM_INTERVAL);

        // calculate a threshold for the graph gradient limit
        int wakeThreshold = (int) (interval * 0.1);
        if (wakeThreshold < 1) {
            wakeThreshold = 1;
        }

        // gradient limit dependent on interval value plus wake threshold
        final long gradientLimit = interval + wakeThreshold * 2;

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
        final XYMultipleSeriesRenderer seriesRenderer = new XYMultipleSeriesRenderer();

        // titles from resources
        seriesRenderer.setChartTitle(context.getString(R.string.graph_chart_title));
        seriesRenderer.setXTitle(context.getString(R.string.graph_x));
        seriesRenderer.setYTitle(context.getString(R.string.graph_y));

        // stylistic stuff
        seriesRenderer.setXRoundedLabels(true);
        seriesRenderer.setXLabels(0);
        seriesRenderer.setPanEnabled(true);
        seriesRenderer.setBarSpacing(fontSize / 3);
        seriesRenderer.setShowGrid(true);
        seriesRenderer.setClickEnabled(true);
        seriesRenderer.setShowGridX(true);
        seriesRenderer.setZoomRate(fontSize / 20);
        seriesRenderer.setZoomButtonsVisible(false);
        seriesRenderer.setExternalZoomEnabled(true);
        seriesRenderer.setZoomEnabled(true, false);

        // margins between axes and labels are set depending on fontSize settings
        final int marginFactor = 2;
        seriesRenderer.setMargins(new int[]{(int) (fontSize * marginFactor),
                (int) (fontSize * marginFactor), (int) (fontSize * marginFactor),
                (int) (fontSize * marginFactor)});
        // set all fontSize related values
        seriesRenderer.setLabelsTextSize(fontSize);
        seriesRenderer.setAxisTitleTextSize(fontSize);
        seriesRenderer.setChartTitleTextSize(0);
        seriesRenderer.setLegendTextSize(fontSize);
        seriesRenderer.setFitLegend(true);
        seriesRenderer.setBarWidth(fontSize * 2);

        // Switch margins colour according to application theme
        /*final String defaultThemeValue = context.getString(R.string.pref_theme_default);*/
        /*final String themeKey = new PreferenceHandler(context).getAppSharedPreferences().
                getString(context.getString(R.string.pref_theme_key), defaultThemeValue);*/

        // default theme value should be "Dark", so use colours that contrast nicely
        final int colorGradientStart;
        final int colorGradientEnd;
        /*if (themeKey.equals(defaultThemeValue)) {
            mRenderer.setMarginsColor(ContextCompat.getColor(context, R.color.md_grey_600));
            mRenderer.setBackgroundColor(Color.GRAY);
            mRenderer.setAxesColor(Color.LTGRAY);
            mRenderer.setGridColor(Color.GRAY);
            mRenderer.setXLabelsColor(Color.WHITE);
            mRenderer.setYLabelsColor(0, Color.WHITE);
            mRenderer.setLabelsColor(Color.WHITE);

            // get colours from resources
            colorGradientStart = ContextCompat.getColor(context, R.color.md_green_400);
            colorGradientEnd = ContextCompat.getColor(context, R.color.md_orange_300);

            // set renderer colours
            xyRenderer.setColor(colorGradientStart);
            xyRenderer.setGradientStart(0, colorGradientStart);
            xyRenderer.setGradientStop(mGradientLimit, colorGradientEnd);
        } else {*/
        // handle "Light" theme
        seriesRenderer.setMarginsColor(ContextCompat.getColor(context, R.color.md_grey_200));
        seriesRenderer.setBackgroundColor(Color.DKGRAY);
        seriesRenderer.setAxesColor(Color.DKGRAY);
        seriesRenderer.setGridColor(Color.DKGRAY);
        seriesRenderer.setXLabelsColor(Color.DKGRAY);
        seriesRenderer.setYLabelsColor(0, Color.DKGRAY);
        seriesRenderer.setLabelsColor(Color.BLACK);

        // get colours from resources
        colorGradientStart = ContextCompat.getColor(context, R.color.md_green_400);
        colorGradientEnd = ContextCompat.getColor(context, R.color.md_orange_300);

        // set renderer colours
        xyRenderer.setColor(colorGradientStart);
        xyRenderer.setGradientStart(0, colorGradientStart);
        xyRenderer.setGradientStop(gradientLimit, colorGradientEnd);
        /*}*/

        // Get graph extents
        long maxY = Long.MIN_VALUE;
        final XYSeries series = new XYSeries(context.getString(R.string.graph_title));
        // get the data from the data manager
        final SortedMap<String, TimeModelInstance> data = dataManager.retrieveDailyData();
        int i = 0;
        if (data != null) {
            // iterate through the entry set
            for (final Entry<String, TimeModelInstance> entry : data.entrySet()) {
                final String x = entry.getKey();
                final double y = entry.getValue().getTimeLogged();

                // find the maximum Y value
                if (maxY < y) {
                    maxY = (long) y;
                }
                // add labels
                seriesRenderer.addXTextLabel(i, String.valueOf(x));
                series.add(i, y);
                seriesRenderer.addXTextLabel(i, x);
                i++;
            }
        }

        // set min-max values for proper graph display
        seriesRenderer.setXAxisMin(-1);
        seriesRenderer.setXAxisMax(i + 1);
        seriesRenderer.setYAxisMax(maxY + 0.3 * maxY);
        seriesRenderer.setYAxisMin(0);

        // set pan - zoom limits
        final double[] panLimits = new double[]{-3, i + 3, 0, maxY + 0.5 * maxY};
        seriesRenderer.setPanLimits(panLimits);
        seriesRenderer.setPanEnabled(true, false);
        seriesRenderer.setZoomLimits(panLimits);

        // add data to graph
        final XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(series);

        // Adding the XSeriesRenderer to the MultipleRenderer.
        seriesRenderer.addSeriesRenderer(xyRenderer);

        // Creating an intent to plot line chart using dataset and multipleRenderer
        return ChartFactory.getBarChartView(context, dataset, seriesRenderer, Type.STACKED);
    }

}

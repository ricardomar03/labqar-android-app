package com.ua.ricardomartins.qualar.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.filter.Approximator;
import com.github.mikephil.charting.data.filter.Approximator.ApproximatorType;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.ua.ricardomartins.qualar.ApiManager;
import com.ua.ricardomartins.qualar.R;
import com.ua.ricardomartins.qualar.models.AirIndex;
import com.ua.ricardomartins.qualar.models.ChartData;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Graphic extends AppCompatActivity {
    private WebView mWebView;
    public static final String ASSET_PATH = "file:///android_asset/";

    private BarChart mChart;
    private TextView mTitle;

    private ProgressBar mProgressBar;
    private RelativeLayout mContentLayout;
    private String mPollutantName;
    private View.OnClickListener snackbarClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            updateView();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphic);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        /*mWebView = (WebView) findViewById(R.id.webView1);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);*/

        Intent intent = getIntent();
        mPollutantName = intent.getStringExtra("POLLUTANT_NAME");

        mTitle = (TextView) findViewById(R.id.chart_textView);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mContentLayout = (RelativeLayout) findViewById(R.id.main_content);

        updateView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateView();
    }

    private void updateView() {
        mProgressBar.setVisibility(View.VISIBLE);
        mContentLayout.setVisibility(View.GONE);

        int loggedUserId = ApiManager.getLoggedUser().getID();
        String loggedUserToken = ApiManager.getLoggedUser().getToken();

        Log.d("TEST3", Integer.toString(loggedUserId));
        Log.d("TEST3", loggedUserToken);
        Log.d("TEST3", mPollutantName);

        Call<ChartData> call = ApiManager.getService().getChart("Token " + loggedUserToken, loggedUserId, mPollutantName);

        call.enqueue(new Callback<ChartData>() {
            @Override
            public void onResponse(Call<ChartData> call, Response<ChartData> response) {
                Log.d("TEST3", response.message());
                if (response.code() == 200) {
                    mProgressBar.setVisibility(View.GONE);
                    mContentLayout.setVisibility(View.VISIBLE);
                    //loadChart(response.body().getValues());
                    mTitle.setText(getResources().getString(R.string.chart_title) + "\n" + mPollutantName );
                    drawChart(response.body().getValues(), mPollutantName);
                } else {
                    Log.d("TEST3", Integer.toString(response.code()));
                }
            }

            @Override
            public void onFailure(Call<ChartData> call, Throwable t) {
                Snackbar.make(findViewById(R.id.graphic_layout), "Algo correu mal. Verifique a sua ligação e tente novamente", Snackbar.LENGTH_INDEFINITE)
                        .setAction(getString(R.string.dialog_retry), snackbarClickListener)
                        .show();
            }
        });
    }

    private void drawChart(ArrayList values, String pollutantName) {
        mChart = (BarChart) findViewById(R.id.chart1);
        mChart.setDescription("");

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        mChart.setDrawBarShadow(false);
        mChart.setDrawGridBackground(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setSpaceBetweenLabels(0);
        xAxis.setDrawGridLines(false);

        mChart.getAxisLeft().setDrawGridLines(false);

        // add a nice and smooth animation
        mChart.animateY(2500);

        mChart.getLegend().setEnabled(false);

        ArrayList<BarEntry> yVals = new ArrayList<BarEntry>();
        ArrayList<String> xVals = new ArrayList<>();


        int count = 0;
        for (Object value : values) {
            ArrayList<String> value_array = (ArrayList<String>) value;
            yVals.add(new BarEntry(Float.parseFloat(value_array.get(1)), count));
            xVals.add(value_array.get(0));
            count++;
        }

        BarDataSet set1;

        set1 = new BarDataSet(yVals, pollutantName);
        set1.setColors(new int[]{Color.rgb(83, 109, 254), Color.rgb(76, 175, 80)});
        set1.setDrawValues(false);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);

        TooltipView mv = new TooltipView(this, R.layout.tooltip_view);

        // set the marker to the chart
        mChart.setMarkerView(mv);

        mChart.setData(data);
        mChart.invalidate();

        // Legend l = mChart.getLegend();
        // l.setPosition(LegendPosition.BELOW_CHART_CENTER);
        // l.setFormSize(8f);
        // l.setFormToTextSpace(4f);
        // l.setXEntrySpace(6f);

        // mChart.setDrawLegend(false);
    }

    private class TooltipView extends MarkerView {

        private TextView tvContent;

        public TooltipView (Context context, int layoutResource) {
            super(context, layoutResource);
            // this markerview only displays a textview
            tvContent = (TextView) findViewById(R.id.tooltip_textView);
        }

        // callbacks everytime the MarkerView is redrawn, can be used to update the
        // content (user-interface)
        @Override
        public void refreshContent(Entry e, Highlight highlight) {
            tvContent.setText("" + e.getVal()); // set the entry-value as the display text
        }

        @Override
        public int getXOffset(float xpos) {
            // this will center the marker-view horizontally
            return -(getWidth() / 2);
        }

        @Override
        public int getYOffset(float ypos) {
            // this will cause the marker-view to be above the selected value
            return -getHeight();
        }
    }

    /*private void drawChart(ArrayList values) {
        mLabels = new String[5];
        mValues = new float[5];

        int count = 0;
        for (Object value : values) {
            ArrayList<String> value_array = (ArrayList<String>) value;
            mLabels[count] = value_array.get(0).split("\\s")[0];
            mValues[count] = Float.parseFloat(value_array.get(1));
            count++;
            if(count == 5)
                break;
            Log.d("TEST", value_array.get(0) + " - " + value_array.get(1));
        }

        BarSet barSet = new BarSet(mLabels, mValues);
        barSet.setColor(Color.parseColor("#fc2a53"));
        mChart.addData(barSet);

        mChart.setBarSpacing(Tools.fromDpToPx(40));
        mChart.setRoundCorners(Tools.fromDpToPx(2));
        mChart.setBarBackgroundColor(Color.parseColor("#592932"));

        // Chart
        mChart.setXAxis(false)
                .setYAxis(false)
                .setXLabels(XController.LabelPosition.OUTSIDE)
                .setYLabels(YController.LabelPosition.NONE)
                .setLabelsColor(Color.parseColor("#86705c"))
                .setAxisColor(Color.parseColor("#86705c"));

        int[] order = {1, 0, 2, 3};
        mChart.show();
    }*/

    private void loadChart(String values) {
        String content = null;
        try {
            AssetManager assetManager = getAssets();
            InputStream in = assetManager.open("chart.html");
            byte[] bytes = readFully(in);
            content = new String(bytes, "UTF-8");
        } catch (IOException e) {
            Log.e("TEST3", "An error occurred.", e);
        }

        String formattedContent = String.format(content, values);
        mWebView.loadDataWithBaseURL(ASSET_PATH, formattedContent, "text/html", "utf-8", null);
        mWebView.requestFocusFromTouch();
    }

    private static byte[] readFully(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        for (int count; (count = in.read(buffer)) != -1; ) {
            out.write(buffer, 0, count);
        }
        return out.toByteArray();
    }

}

package com.ua.ricardomartins.qualar.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.ua.ricardomartins.qualar.ApiManager;
import com.ua.ricardomartins.qualar.R;
import com.ua.ricardomartins.qualar.models.ChartData;
import com.ua.ricardomartins.qualar.models.IndexChartData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Graphic_Index extends AppCompatActivity {
    private BarChart mChart;
    private String[] mTooltips;
    private ProgressBar mProgressBar;
    private RelativeLayout mContentLayout;
    private View.OnClickListener snackbarClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            updateView();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphic__index);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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

        Call<IndexChartData> call = ApiManager.getService().getIndexChart("Token " + loggedUserToken, loggedUserId);

        call.enqueue(new Callback<IndexChartData>() {
            @Override
            public void onResponse(Call<IndexChartData> call, Response<IndexChartData> response) {
                if (response.code() == 200) {
                    mProgressBar.setVisibility(View.GONE);
                    mContentLayout.setVisibility(View.VISIBLE);
                    drawChart(response.body().getValues());
                } else {
                    Log.d("TEST3", Integer.toString(response.code()));
                }
            }

            @Override
            public void onFailure(Call<IndexChartData> call, Throwable t) {
                Snackbar.make(findViewById(R.id.graphic_index_layout), "Algo correu mal. Verifique a sua ligação e tente novamente", Snackbar.LENGTH_INDEFINITE)
                        .setAction(getString(R.string.dialog_retry), snackbarClickListener)
                        .show();
            }
        });
    }

    private void drawChart(ArrayList values) {
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

        YAxis yAxis = mChart.getAxisLeft();
        yAxis.setEnabled(false);
        yAxis.setDrawGridLines(false);
        yAxis.setAxisMaxValue((float) 1.2);
        yAxis.setAxisMinValue((float) 0);
        mChart.getAxisRight().setEnabled(false);

        LimitLine ll = new LimitLine(1, "Muito Mau");
        ll.setLineColor(Color.parseColor("#FF0000"));
        ll.setLineWidth(2f);
        ll.setTextColor(Color.BLACK);
        ll.setTextSize(12f);

        LimitLine l2 = new LimitLine((float) 0.8, "Fraco");
        l2.setLineColor(Color.parseColor("#ffa500"));
        l2.setLineWidth(2f);
        l2.setTextColor(Color.BLACK);
        l2.setTextSize(12f);

        LimitLine l3 = new LimitLine((float) 0.6, "Médio");
        l3.setLineColor(Color.parseColor("#FFFF00"));
        l3.setLineWidth(2f);
        l3.setTextColor(Color.BLACK);
        l3.setTextSize(12f);

        LimitLine l4 = new LimitLine((float) 0.4, "Bom");
        l4.setLineColor(Color.parseColor("#008000"));
        l4.setLineWidth(2f);
        l4.setTextColor(Color.BLACK);
        l4.setTextSize(12f);

        LimitLine l5 = new LimitLine((float) 0.2, "Muito Bom");
        l5.setLineColor(Color.parseColor("#02dd02"));
        l5.setLineWidth(2f);
        l5.setTextColor(Color.BLACK);
        l5.setTextSize(12f);

        LimitLine l6 = new LimitLine((float) 0, "Indisponivel");
        l6.setLineColor(Color.parseColor("#808080"));
        l6.setLineWidth(2f);
        l6.setTextColor(Color.BLACK);
        l6.setTextSize(12f);
// .. and more styling options

        yAxis.addLimitLine(ll);
        yAxis.addLimitLine(l2);
        yAxis.addLimitLine(l3);
        yAxis.addLimitLine(l4);
        yAxis.addLimitLine(l5);
        yAxis.addLimitLine(l6);

        // add a nice and smooth animation
        mChart.animateY(2500);

        mChart.getLegend().setEnabled(false);

        ArrayList<BarEntry> yVals = new ArrayList<BarEntry>();
        ArrayList<String> xVals = new ArrayList<>();
        int [] colors = new int[values.size()];
        mTooltips = new String[values.size()];


        int count = 0;
        for (Object value : values) {
            ArrayList<String> value_array = (ArrayList<String>) value;
            yVals.add(new BarEntry(Float.parseFloat(value_array.get(1)), count));
            colors[count] = Color.parseColor(value_array.get(2).split(":")[1]);
            mTooltips[count] = value_array.get(3);
            xVals.add(value_array.get(0));
            count++;
        }

        BarDataSet set1;

        set1 = new BarDataSet(yVals, "Index");
        set1.setColors(colors);
        set1.setDrawValues(false);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);

        TooltipView mv = new TooltipView(this, R.layout.tooltip_view2);

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
            tvContent = (TextView) findViewById(R.id.tooltip_textView2);
        }

        // callbacks everytime the MarkerView is redrawn, can be used to update the
        // content (user-interface)
        @Override
        public void refreshContent(Entry e, Highlight highlight) {
            tvContent.setText(mTooltips[e.getXIndex()]); // set the entry-value as the display text
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
}

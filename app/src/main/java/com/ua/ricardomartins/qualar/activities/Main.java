package com.ua.ricardomartins.qualar.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.ua.ricardomartins.qualar.ApiManager;
import com.ua.ricardomartins.qualar.Common;
import com.ua.ricardomartins.qualar.R;
import com.ua.ricardomartins.qualar.RegistrationIntentService;
import com.ua.ricardomartins.qualar.UnregisterIntentService;
import com.ua.ricardomartins.qualar.models.AirIndex;
import com.ua.ricardomartins.qualar.models.Alert;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Main extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView mTextView;
    private TextView mUsernameTextView;
    private PieChart mPieChart;
    private BarChart mBarChart;
    private Button mButton;
    private ProgressBar mProgressBar;
    private RelativeLayout mContentLayout;
    private TextView mUnavailableTextView;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private View.OnClickListener snackbarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            updateView();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        mUsernameTextView = (TextView) header.findViewById(R.id.user_name_textView);
        mButton = (Button) findViewById(R.id.index_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main.this, Graphic_Index.class);
                startActivity(intent);
            }
        });
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mContentLayout = (RelativeLayout) findViewById(R.id.main_content);
        mUnavailableTextView = (TextView) findViewById(R.id.unavailable_textView);

        if (!ApiManager.getLoggedUser().getName().equals(""))
            mUsernameTextView.setText(ApiManager.getLoggedUser().getName());
        else
            mUsernameTextView.setText(ApiManager.getLoggedUser().getUsername());

        mTextView = (TextView) findViewById(R.id.date_time_index_textView);

        updateView();

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Log.d("SENDT", "SEND 1");
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        final SwipeRefreshLayout swipeView = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeView.setColorSchemeColors(Color.BLUE);
        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeView.setRefreshing(true);
                updateView();
                swipeView.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateView();
    }

    private void updateView() {
        mProgressBar.setVisibility(View.VISIBLE);
        mContentLayout.setVisibility(View.GONE);
        mUnavailableTextView.setVisibility(View.GONE);

        int loggedUserId = ApiManager.getLoggedUser().getID();
        String loggedUserToken = ApiManager.getLoggedUser().getToken();

        Call<AirIndex> call = ApiManager.getService().getAirIndex("Token " + loggedUserToken, loggedUserId);

        call.enqueue(new Callback<AirIndex>() {
            @Override
            public void onResponse(Call<AirIndex> call, Response<AirIndex> response) {
                mProgressBar.setVisibility(View.GONE);
                mContentLayout.setVisibility(View.VISIBLE);
                if (response.code() == 200) {
                    String datetime = response.body().getDateTime();
                    if (!response.body().getCampaign().equals("current")) {
                        mTextView.setText(response.body().getCampaign() + ", dados recolhidos em: " + datetime.split("\\s")[0] + " às " + datetime.split("\\s")[1]);
                    } else {
                        mTextView.setText("Dados recolhidos em: " + datetime.split("\\s")[0] + " às " + datetime.split("\\s")[1]);
                    }
                    loadPieChart(response.body().getIndex());
                    loadBarChart(response.body().getIndex());
                } else if (response.code() == 204) {
                    mTextView.setText("De momento não existem dados para apresentar!");
                    RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.pieChart_layout);
                    relativeLayout.setVisibility(View.GONE);
                    TextView qualityTextView = (TextView) findViewById(R.id.quality_textView);
                    qualityTextView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<AirIndex> call, Throwable t) {
                Snackbar.make(findViewById(R.id.main_layout), "Algo correu mal. Verifique a sua ligação e tente novamente", Snackbar.LENGTH_INDEFINITE)
                        .setAction(getString(R.string.dialog_retry), snackbarClickListener)
                        .show();
            }
        });
    }

    private void loadBarChart(String[] index) {
        mBarChart = (BarChart) findViewById(R.id.barChart);
        mBarChart.setDescription("");

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mBarChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        mBarChart.setPinchZoom(false);

        mBarChart.setDrawBarShadow(false);
        mBarChart.setDrawGridBackground(false);

        XAxis xAxis = mBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setSpaceBetweenLabels(0);
        xAxis.setDrawGridLines(false);

        YAxis yAxis = mBarChart.getAxisLeft();
        yAxis.setEnabled(false);
        yAxis.setDrawGridLines(false);
        yAxis.setAxisMaxValue((float) 1.2);
        yAxis.setAxisMinValue((float) 0);
        mBarChart.getAxisRight().setEnabled(false);

        LimitLine ll = new LimitLine(1, "Mau");
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

        mBarChart.setTouchEnabled(false);

        // add a nice and smooth animation
        mBarChart.animateY(2500);

        mBarChart.getLegend().setEnabled(false);

        ArrayList<BarEntry> yVals = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();

        /*
        int count = 0;
        for (Object value : values) {
            ArrayList<String> value_array = (ArrayList<String>) value;
            yVals.add(new BarEntry(Float.parseFloat(value_array.get(1)), count));
            xVals.add(value_array.get(0));
            count++;
        }*/
        int[] colors = new int[5];

        for (int i = 3; i < index.length; i++) {
            if (Integer.parseInt(index[i]) == 0 || Integer.parseInt(index[i]) == 6) {
                colors[i - 3] = Color.parseColor("#808080");
                yVals.add(new BarEntry((float) 0, i - 3));
            } else if (Integer.parseInt(index[i]) == 1) {
                colors[i - 3] = Color.parseColor("#FF0000");
                yVals.add(new BarEntry((float) 1, i - 3));
            } else if (Integer.parseInt(index[i]) == 2) {
                colors[i - 3] = Color.parseColor("#ffa500");
                yVals.add(new BarEntry((float) 0.8, i - 3));
            } else if (Integer.parseInt(index[i]) == 3) {
                colors[i - 3] = Color.parseColor("#FFFF00");
                yVals.add(new BarEntry((float) 0.6, i - 3));
            } else if (Integer.parseInt(index[i]) == 4) {
                Log.d("TEST", "4");
                colors[i - 3] = Color.parseColor("#008000");
                yVals.add(new BarEntry((float) 0.4, i - 3));
            } else if (Integer.parseInt(index[i]) == 5) {
                colors[i - 3] = Color.parseColor("#02dd02");
                yVals.add(new BarEntry((float) 0.2, i - 3));
            }
        }
        xVals.add("CO");
        xVals.add("SO2");
        xVals.add("NO2");
        xVals.add("O3");
        xVals.add("PM10");
        xVals.add((""));
        xVals.add((""));

        BarDataSet set1;

        set1 = new BarDataSet(yVals, "");
        set1.setColors(colors);
        set1.setDrawValues(false);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        BarData data = new BarData(xVals, dataSets);

        mBarChart.setData(data);
        mBarChart.invalidate();
    }

    private void loadPieChart(String[] index) {
        mPieChart = (PieChart) findViewById(R.id.pieChart);
        mPieChart.setDescription("");

        mPieChart.setDragDecelerationFrictionCoef(0.95f);

        mPieChart.setCenterText(generateCenterSpannableText(index[2]));

        mPieChart.setDrawHoleEnabled(true);
        mPieChart.setHoleColor(Color.WHITE);

        mPieChart.setTransparentCircleColor(Color.WHITE);
        mPieChart.setTransparentCircleAlpha(110);

        mPieChart.setHoleRadius(75f);
        mPieChart.setTransparentCircleRadius(85f);

        mPieChart.setDrawCenterText(true);

        mPieChart.setTouchEnabled(false);

        mPieChart.setRotationAngle(180);
        // enable rotation of the chart by touch
        mPieChart.setRotationEnabled(false);

        mPieChart.setMaxAngle(180f);

        Legend legend = mPieChart.getLegend();
        legend.setEnabled(false);

        // mChart.setUnit(" €");
        // mChart.setDrawUnitsInChart(true);

        // add a selection listener
        //mChart.setOnChartValueSelectedListener(this);

        setData(Double.parseDouble(index[0]));

        mPieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        // mChart.spin(2000, 0, 360);

    }

    private void setData(double index) {

        int value = 0;
        String color = "#808080";
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        // IMPORTANT: In a PieChart, no values (Entry) should have the same
        // xIndex (even if from different DataSets), since no values can be
        // drawn above each other.

        if (index == 0) {
            color = "#808080";
            value = 0;
        } else if (index == 1.0) {
            color = "#FF0000";
            value = 100;
        } else if (index == 0.8) {
            color = "#ffa500";
            value = 80;
        } else if (index == 0.6) {
            color = "#FFFF00";
            value = 60;
        } else if (index == 0.4) {
            color = "#008000";
            value = 40;
        } else if (index == 0.2) {
            color = "#02dd02";
            value = 20;
        }

        yVals1.add(new Entry((value), 0));
        yVals1.add(new Entry((100 - value), 1));
        ArrayList<String> xVals = new ArrayList<>();
        xVals.add("");
        xVals.add("");

        PieDataSet dataSet = new PieDataSet(yVals1, "Classificação");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        dataSet.setColors(new int[]{ Color.parseColor(color), Color.parseColor("#808080")});
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(xVals, dataSet);
        //data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(0f);
        data.setValueTextColor(Color.parseColor(color));
        mPieChart.setData(data);

        // undo all highlights
        mPieChart.highlightValues(null);

        mPieChart.invalidate();
    }

    private SpannableString generateCenterSpannableText(String text) {

        SpannableString s = new SpannableString(text + "\n\n");
        s.setSpan(new RelativeSizeSpan(2.5f), 0, s.length(), 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), 0, s.length(), 0);
        return s;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_overview) {
            Intent intent = new Intent(Main.this,Main.class);
            startActivity(intent);
        } else if (id == R.id.nav_measurements) {
            Intent intent = new Intent(Main.this, Measurements.class);
            startActivity(intent);
        } else if (id == R.id.nav_alerts) {
            Intent intent = new Intent(Main.this, Alerts.class);
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(Main.this, About.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(Main.this, Settings.class);
            startActivity(intent);
        } else if (id == R.id.nav_tutorial) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            sharedPreferences.edit().putBoolean(Common.USER_SAW_TUTORIAL, false).apply();
            Intent intent = new Intent(Main.this, Tutorial.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.logout) {
            logOutConfirmation();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void markUserAsLoggedOut() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putBoolean(Common.USER_LOGGED_IN, false).apply();
        sharedPreferences.edit().putInt(Common.USER_ID, -1).apply();
        sharedPreferences.edit().putString(Common.USER_USERNAME, "").apply();
        sharedPreferences.edit().putString(Common.USER_NAME, "").apply();
        sharedPreferences.edit().putBoolean(Common.USER_SUPERUSER, false).apply();
        sharedPreferences.edit().putString(Common.USER_TOKEN, "").apply();
        Intent intent = new Intent(this, UnregisterIntentService.class);
        startService(intent);
    }

    public void logOutConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
        builder.setMessage(getString(R.string.dialog_message));

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        markUserAsLoggedOut();
                        Intent intent = new Intent(Main.this, Login.class);
                        startActivity(intent);
                        finish();
                    }
                });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i("TEST", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}

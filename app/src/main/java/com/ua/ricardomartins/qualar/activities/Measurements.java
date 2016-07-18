package com.ua.ricardomartins.qualar.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ua.ricardomartins.qualar.ApiManager;
import com.ua.ricardomartins.qualar.Common;
import com.ua.ricardomartins.qualar.models.Measurement;
import com.ua.ricardomartins.qualar.PollutantAdapter;
import com.ua.ricardomartins.qualar.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Measurements extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, PollutantAdapter.MyListItemClickListener{
    private RecyclerView mRecyclerView;
    private TextView mTextView;
    private TextView mUsernameTextView;
    private View.OnClickListener snackbarClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            updateView();
        }
    };
    private ProgressBar mProgressBar;
    private RelativeLayout mContentLayout;
    private TextView mUnavailableTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurements);
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
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mContentLayout = (RelativeLayout) findViewById(R.id.main_content);
        mUnavailableTextView = (TextView) findViewById(R.id.unavailable_textView);

        if(!ApiManager.getLoggedUser().getName().equals(""))
            mUsernameTextView.setText(ApiManager.getLoggedUser().getName());
        else
            mUsernameTextView.setText(ApiManager.getLoggedUser().getUsername());

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_pollutants);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mTextView = (TextView) findViewById(R.id.date_time_textView);

        updateView();

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

        Call<Measurement> call = ApiManager.getService().getLatestUpdate("Token " + loggedUserToken, loggedUserId);
        call.enqueue(new Callback<Measurement>() {
            @Override
            public void onResponse(Call<Measurement> call, Response<Measurement> response) {
                mProgressBar.setVisibility(View.GONE);
                mContentLayout.setVisibility(View.VISIBLE);
                if(response.code() == 200){
                    PollutantAdapter adapter = new PollutantAdapter(response.body().getValues(), Measurements.this, Measurements.this);
                    mRecyclerView.setAdapter(adapter);
                    String datetime = response.body().getDateTime();
                    if(!response.body().getCampaign().equals("current")){
                        mTextView.setText(response.body().getCampaign() + ", dados recolhidos em: " + datetime.split("\\s")[0] + " às " + datetime.split("\\s")[1]);
                    }
                    else{
                        mTextView.setText("Dados recolhidos em: " + datetime.split("\\s")[0] + " às " + datetime.split("\\s")[1]);
                    }
                }
                else if(response.code() == 204){
                    mTextView.setText("De momento não existem dados para apresentar!");
                    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView_pollutants);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<Measurement> call, Throwable t) {
                Snackbar.make(findViewById(R.id.measurements_layout), "Algo correu mal. Verifique a sua ligação e tente novamente", Snackbar.LENGTH_INDEFINITE)
                        .setAction(getString(R.string.dialog_retry), snackbarClickListener)
                        .show();
            }
        });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_overview) {
            Intent intent = new Intent(Measurements.this,Main.class);
            startActivity(intent);
        } else if (id == R.id.nav_measurements) {
            Intent intent = new Intent(Measurements.this, Measurements.class);
            startActivity(intent);
        } else if (id == R.id.nav_alerts) {
            Intent intent = new Intent(Measurements.this, Alerts.class);
            startActivity(intent);
        }else if (id == R.id.nav_about) {
            Intent intent = new Intent(Measurements.this, About.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(Measurements.this, Settings.class);
            startActivity(intent);
        } else if (id == R.id.nav_tutorial) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            sharedPreferences.edit().putBoolean(Common.USER_SAW_TUTORIAL, false).apply();
            Intent intent = new Intent(Measurements.this, Tutorial.class);
            startActivity(intent);
        } else if (id == R.id.logout) {
            logOutConfirmation();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void markUserAsLoggedOut(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putBoolean(Common.USER_LOGGED_IN, false).apply();
        sharedPreferences.edit().putInt(Common.USER_ID, -1).apply();
        sharedPreferences.edit().putString(Common.USER_USERNAME, "").apply();
        sharedPreferences.edit().putString(Common.USER_NAME, "").apply();
        sharedPreferences.edit().putBoolean(Common.USER_SUPERUSER, false).apply();
        sharedPreferences.edit().putString(Common.USER_TOKEN, "").apply();
    }

    public void logOutConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Measurements.this);
        builder.setMessage(getString(R.string.dialog_message));

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        markUserAsLoggedOut();
                        Intent intent = new Intent(Measurements.this, Login.class);
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

    @Override
    public void onItemClick(String pollutantName) {
        Intent intent = new Intent(Measurements.this, Graphic.class);
        intent.putExtra("POLLUTANT_NAME", pollutantName);
        startActivity(intent);
    }

}

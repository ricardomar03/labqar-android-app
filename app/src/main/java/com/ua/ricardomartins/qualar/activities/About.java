package com.ua.ricardomartins.qualar.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ua.ricardomartins.qualar.Common;
import com.ua.ricardomartins.qualar.R;

public class About extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_overview) {
            Intent intent = new Intent(About.this,Main.class);
            startActivity(intent);
        } else if (id == R.id.nav_measurements) {
            Intent intent = new Intent(About.this, Measurements.class);
            startActivity(intent);
        } else if (id == R.id.nav_alerts) {
            Intent intent = new Intent(About.this, Alerts.class);
            startActivity(intent);
        }else if (id == R.id.nav_about) {
            Intent intent = new Intent(About.this, About.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(About.this, Settings.class);
            startActivity(intent);
        } else if (id == R.id.nav_tutorial) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            sharedPreferences.edit().putBoolean(Common.USER_SAW_TUTORIAL, false).apply();
            Intent intent = new Intent(About.this, Tutorial.class);
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
        sharedPreferences.edit().putInt(Common.USER_ID, -1).apply();
        sharedPreferences.edit().putBoolean(Common.USER_LOGGED_IN, false).apply();
        sharedPreferences.edit().putString(Common.USER_USERNAME, "").apply();
        sharedPreferences.edit().putString(Common.USER_NAME, "").apply();
        sharedPreferences.edit().putBoolean(Common.USER_SUPERUSER, false).apply();
        sharedPreferences.edit().putString(Common.USER_TOKEN, "").apply();
    }

    public void logOutConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(About.this);
        builder.setMessage(getString(R.string.dialog_message));

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        markUserAsLoggedOut();
                        Intent intent = new Intent(About.this, Login.class);
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

}

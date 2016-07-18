package com.ua.ricardomartins.qualar.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ua.ricardomartins.qualar.ApiManager;
import com.ua.ricardomartins.qualar.Common;
import com.ua.ricardomartins.qualar.R;
import com.ua.ricardomartins.qualar.models.NewSettings;
import com.ua.ricardomartins.qualar.models.User;

import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Settings extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private EditText mNewUsername;
    private EditText mNewName;
    private EditText mNewPassword;
    private TextView mUsernameTextView;
    private Button mButton;
    private View.OnClickListener snackbarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
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

        mNewName = (EditText) findViewById(R.id.input_name);
        mNewUsername = (EditText) findViewById(R.id.input_username);
        mNewPassword = (EditText) findViewById(R.id.input_password);
        mButton = (Button) findViewById(R.id.save_changes_button);

        mUsernameTextView = (TextView) header.findViewById(R.id.user_name_textView);
        if (!ApiManager.getLoggedUser().getName().equals(""))
            mUsernameTextView.setText(ApiManager.getLoggedUser().getName());
        else
            mUsernameTextView.setText(ApiManager.getLoggedUser().getUsername());

        mButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (submit()) {
                    String name = mNewName.getText().toString();
                    String username = mNewUsername.getText().toString();
                    String password = mNewPassword.getText().toString();

                    int loggedUserId = ApiManager.getLoggedUser().getID();
                    String loggedUserToken = ApiManager.getLoggedUser().getToken();

                    Call<NewSettings> call = ApiManager.getService().editSettings("Token " + loggedUserToken, loggedUserId, name, username, password);
                    call.enqueue(new Callback<NewSettings>() {
                        @Override
                        public void onResponse(Call<NewSettings> call, Response<NewSettings> response) {
                            if (response.code() == 200) {
                                Boolean somethingChanged = false;
                                Boolean errorOccurred = false;

                                if (response.body().didNameChanged()) {
                                    ApiManager.getLoggedUser().setName(response.body().getName());
                                    somethingChanged = true;
                                } else if (!response.body().didNameChanged() && !mNewName.getText().toString().equals("")) {
                                    errorOccurred = true;
                                }
                                if (response.body().didUsernameChanged()) {
                                    ApiManager.getLoggedUser().setUsername(response.body().getUsername());
                                    somethingChanged = true;
                                } else if (!response.body().didUsernameChanged() && !mNewUsername.getText().toString().equals("")) {
                                    errorOccurred = true;
                                }
                                if (response.body().didPasswordChanged()) {
                                    somethingChanged = true;
                                } else if (!response.body().didPasswordChanged() && !mNewPassword.getText().toString().equals("")) {
                                    errorOccurred = true;
                                }

                                if (somethingChanged) {
                                    int id = ApiManager.getLoggedUser().getID();
                                    String username = ApiManager.getLoggedUser().getUsername();
                                    String name = ApiManager.getLoggedUser().getName();
                                    Boolean superuser = ApiManager.getLoggedUser().isSuperuser();
                                    String token = ApiManager.getLoggedUser().getToken();
                                    markUserAsLoggedIn(id, username, name, superuser, token);

                                    if (!ApiManager.getLoggedUser().getName().equals(""))
                                        mUsernameTextView.setText(ApiManager.getLoggedUser().getName());
                                    else
                                        mUsernameTextView.setText(ApiManager.getLoggedUser().getUsername());

                                    mNewUsername.setText("");
                                    mNewName.setText("");
                                    mNewPassword.setText("");


                                    if (!errorOccurred) {
                                        Snackbar.make(findViewById(R.id.settings_layout), "Os seus dados foram atualizados com sucesso!", Snackbar.LENGTH_INDEFINITE)
                                                .setAction(getString(R.string.dialog_confirm), snackbarClickListener)
                                                .show();
                                    } else {
                                        Snackbar.make(findViewById(R.id.settings_layout), "Alguns dados não puderem ser atualizados com sucesso, por favor tente novamente", Snackbar.LENGTH_INDEFINITE)
                                                .setAction(getString(R.string.dialog_confirm), snackbarClickListener)
                                                .show();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<NewSettings> call, Throwable t) {
                            Snackbar.make(findViewById(R.id.settings_layout), "Não foi possível efetuar o seu pedido, por favor tente novamente", Snackbar.LENGTH_INDEFINITE)
                                    .setAction(getString(R.string.dialog_confirm), snackbarClickListener)
                                    .show();
                        }
                    });
                }

            }
        });
    }

    public boolean submit() {
        if (isPasswordEmpty() && isUsernameEmpty() && isNameEmpty()) {
            Snackbar.make(findViewById(R.id.settings_layout), "Os campos não podem ser todos vazios", Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.dialog_confirm), snackbarClickListener)
                    .show();
            return false;
        }

        return true;
    }

    private boolean isPasswordEmpty() {
        return mNewPassword == null || mNewPassword.getText().toString() == null || mNewPassword.getText().toString().isEmpty();
    }

    private boolean isUsernameEmpty() {
        return mNewUsername == null || mNewUsername.getText().toString() == null || mNewUsername.getText().toString().isEmpty();
    }

    private boolean isNameEmpty() {
        return mNewName == null || mNewName.getText().toString() == null || mNewName.getText().toString().isEmpty();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_overview) {
            Intent intent = new Intent(Settings.this, Main.class);
            startActivity(intent);
        } else if (id == R.id.nav_measurements) {
            Intent intent = new Intent(Settings.this, Measurements.class);
            startActivity(intent);
        } else if (id == R.id.nav_alerts) {
            Intent intent = new Intent(Settings.this, Alerts.class);
            startActivity(intent);
        }else if (id == R.id.nav_about) {
            Intent intent = new Intent(Settings.this, About.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(Settings.this, Settings.class);
            startActivity(intent);
        } else if (id == R.id.nav_tutorial) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            sharedPreferences.edit().putBoolean(Common.USER_SAW_TUTORIAL, false).apply();
            Intent intent = new Intent(Settings.this, Tutorial.class);
            startActivity(intent);
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
    }

    public void markUserAsLoggedIn(int id, String username, String name, Boolean superuser, String token) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putInt(Common.USER_ID, id).apply();
        sharedPreferences.edit().putBoolean(Common.USER_LOGGED_IN, true).apply();
        sharedPreferences.edit().putString(Common.USER_USERNAME, username).apply();
        sharedPreferences.edit().putString(Common.USER_NAME, name).apply();
        sharedPreferences.edit().putBoolean(Common.USER_SUPERUSER, superuser).apply();
        sharedPreferences.edit().putString(Common.USER_TOKEN, token).apply();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    public void logOutConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
        builder.setMessage(getString(R.string.dialog_message));

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        markUserAsLoggedOut();
                        Intent intent = new Intent(Settings.this, Login.class);
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

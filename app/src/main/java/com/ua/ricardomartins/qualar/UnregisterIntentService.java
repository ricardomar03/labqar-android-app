package com.ua.ricardomartins.qualar;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ricardo on 19/05/16.
 */
public class UnregisterIntentService extends IntentService {
    private static final String TAG = "UnregIntentService";

    public UnregisterIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("LOGOUT", "Entrou");

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            disableInServer(token);

        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        //Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
        //LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    public void disableInServer(String token) {
        try {
            int loggedUserId = ApiManager.getLoggedUser().getID();
            String loggedUserToken = ApiManager.getLoggedUser().getToken();

            Call<ResponseBody> call = ApiManager.getService().logout("Token " + loggedUserToken, loggedUserId, token);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code() == 200) {
                        Log.d("LOGOUT", "DIsabled");
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d("LOGOUT", "Failed");
                }
            });
        } catch (
                Exception e
                )
        {
            Log.d(TAG, "Failed to complete token refresh", e);
        }
    }
}

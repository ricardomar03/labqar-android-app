package com.ua.ricardomartins.qualar.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.crashlytics.android.Crashlytics;
import com.ua.ricardomartins.qualar.ApiManager;
import com.ua.ricardomartins.qualar.Common;
import com.ua.ricardomartins.qualar.R;
import com.ua.ricardomartins.qualar.models.User;

import io.fabric.sdk.android.Fabric;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {
    public static final byte[] KEY = { 0, 4, 2, 5, 4, 4, 6, 7, 6, 9, 5, 1, 1, 1, 6, 1 };

    private SharedPreferences mSharedPreferences;
    private EditText mUsername;
    private EditText mPassword;
    private Button mButton;
    private View.OnClickListener snackbarClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mButton = (Button) findViewById(R.id.login_button);
        mUsername = (EditText) findViewById(R.id.input_username);
        mPassword = (EditText) findViewById(R.id.input_password);
        mButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(submit()){
                    String username = mUsername.getText().toString();
                    String password = mPassword.getText().toString();
                    Call<User> call = ApiManager.getService().login(username, password);
                    call.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            if (response.code() == 200) {
                                int id = response.body().getID();
                                String username = response.body().getUsername();
                                String name = response.body().getName();
                                Boolean superuser = response.body().isSuperuser();
                                String token = response.body().getToken();
                                markUserAsLoggedIn(id, username, name, superuser, token);
                                setLoggedUser();
                            }
                            else {
                                Snackbar.make(findViewById(R.id.login_layout), "Os seus dados estão incorretos, por favor tente novamente", Snackbar.LENGTH_INDEFINITE)
                                        .setAction(getString(R.string.dialog_confirm), snackbarClickListener)
                                        .show();
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Snackbar.make(findViewById(R.id.login_layout), "Não foi possível efetuar o seu pedido, por favor tente novamente", Snackbar.LENGTH_INDEFINITE)
                                    .setAction(getString(R.string.dialog_confirm), snackbarClickListener)
                                    .show();
                        }
                    });
                }
            }
        });
        if(alreadyLoggedIn()){
            setLoggedUser();
        }
    }

    public static String encryptPassword(String password)
    {
        try
        {
            // encryption
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecureRandom r = new SecureRandom();
            byte[] ivBytes = new byte[16];
            r.nextBytes(ivBytes);
            SecretKeySpec keySpec = new SecretKeySpec(KEY, "AES");
            c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(ivBytes));
            String s = "textpassword";
            byte[] data = s.getBytes("ISO-8859-1");

            Log.d("TEST", "PW_Original " + s);
            byte[] encrypted = c.doFinal(data);
            Log.d("TEST", "CT" + Arrays.toString(encrypted));
            byte[] cipherBytes = new byte[encrypted.length + ivBytes.length];

            Log.d("TEST", "IV " + Arrays.toString(ivBytes));

            System.arraycopy(encrypted, 0, cipherBytes, 0, encrypted.length);
            System.arraycopy(ivBytes, 0, cipherBytes, encrypted.length, ivBytes.length);


            Log.d("TEST", "CB " + Arrays.toString(cipherBytes));

            String cipherText = bytesToHex(cipherBytes);

            Log.d("TEST", "CTS " + cipherText);

            Log.d("TEST", Arrays.toString(hexToBytes(cipherText)));


            //decryoption
            byte[] cipherBytes2 = hexToBytes(cipherText);
            Log.d("TEST", "CB2" + Arrays.toString(cipherBytes2));
            byte[] ivBytes2 =  new byte[16];
            System.arraycopy(cipherBytes2, cipherBytes2.length - 16, ivBytes2, 0, 16);
            Log.d("TEST", "IV2" + Arrays.toString(ivBytes2));
            byte[] cipherText2 = new byte[cipherBytes2.length - 16];
            System.arraycopy(cipherBytes2, 0, cipherText2, 0, cipherBytes2.length - 16);
            Log.d("TEST", "CT2" + Arrays.toString(cipherText2));
            Cipher d_c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec d_keySpec = new SecretKeySpec(KEY, "AES");

            d_c.init(Cipher.DECRYPT_MODE, d_keySpec, new IvParameterSpec(ivBytes2));
            byte[] decrypted = d_c.doFinal(cipherText2);

            Log.d("TEST", Arrays.toString(decrypted));

            String clearText = new String(decrypted);
            Log.d("TEST", "PW_Decifrada" + clearText);

        }
        catch (Exception ex)
        {
            Log.d("", ex.getMessage());
        }
        return "";
    }

    public boolean submit(){
        if(isUsernameEmpty() && isPasswordEmpty()){
            mUsername.setError("O nome de utilizador não pode ser vazio");
            mPassword.setError("A password não pode ser vazia");
            return false;
        }
        else if(isUsernameEmpty() && !isPasswordEmpty()){
            mUsername.setError("O nome de utilizador não pode ser vazio");
            mPassword.setError(null);
            return false;
        }
        else if(!isUsernameEmpty() && isPasswordEmpty()){
            mUsername.setError(null);
            mPassword.setError("A password não pode ser vazia");
            return false;
        }

        return true;
    }

    private boolean isPasswordEmpty() {
        return mPassword == null || mPassword.getText().toString() == null || mPassword.getText().toString().isEmpty();
    }

    private boolean isUsernameEmpty() {
        return mUsername == null || mUsername.getText().toString() == null || mUsername.getText().toString().isEmpty();
    }

    private boolean alreadyLoggedIn() {
        Boolean alreadyLoggedIn = mSharedPreferences.getBoolean(Common.USER_LOGGED_IN, false);
        return alreadyLoggedIn;
    }

    public void setLoggedUser() {
        int id = mSharedPreferences.getInt(Common.USER_ID, -1);
        String username = mSharedPreferences.getString(Common.USER_USERNAME, "");
        String name = mSharedPreferences.getString(Common.USER_NAME, "");
        Boolean superuser = mSharedPreferences.getBoolean(Common.USER_SUPERUSER, false);
        String token = mSharedPreferences.getString(Common.USER_TOKEN, "");
        ApiManager.setLoggedUser(id, username,name,superuser,token);

        Intent intent = new Intent(Login.this, Main.class);
        startActivity(intent);
        finish();
    }

    public void markUserAsLoggedIn(int id, String username, String name, Boolean superuser, String token){
        mSharedPreferences.edit().putInt(Common.USER_ID, id).apply();
        mSharedPreferences.edit().putBoolean(Common.USER_LOGGED_IN, true).apply();
        mSharedPreferences.edit().putString(Common.USER_USERNAME, username).apply();
        mSharedPreferences.edit().putString(Common.USER_NAME, name).apply();
        mSharedPreferences.edit().putBoolean(Common.USER_SUPERUSER, superuser).apply();
        mSharedPreferences.edit().putString(Common.USER_TOKEN, token).apply();
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

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] hexToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len/2];

        for(int i = 0; i < len; i+=2){
            data[i/2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }

        return data;
    }


}

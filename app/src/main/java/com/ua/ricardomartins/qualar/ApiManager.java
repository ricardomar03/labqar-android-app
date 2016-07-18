package com.ua.ricardomartins.qualar;

import com.ua.ricardomartins.qualar.activities.Alerts;
import com.ua.ricardomartins.qualar.models.AirIndex;
import com.ua.ricardomartins.qualar.models.Alert;
import com.ua.ricardomartins.qualar.models.ChartData;
import com.ua.ricardomartins.qualar.models.GcmToken;
import com.ua.ricardomartins.qualar.models.IndexChartData;
import com.ua.ricardomartins.qualar.models.Measurement;
import com.ua.ricardomartins.qualar.models.NewSettings;
import com.ua.ricardomartins.qualar.models.User;


import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by ricardo on 18/04/16.
 */
public class ApiManager {
    public interface ApiInterface {
        // Request method and URL specified in the annotation
        // Callback for the parsed response is the last parameter

        @GET("latestupdate/{id}")
        Call<Measurement> getLatestUpdate(@Header("Authorization") String authorization, @Path("id") int id);

        @GET("latestupdate/{id}/airindex")
        Call<AirIndex> getAirIndex(@Header("Authorization") String authorization, @Path("id") int id);

        @GET("latestupdate/{id}/chart/{name}/")
        Call<ChartData> getChart(@Header("Authorization") String authorization, @Path("id") int id, @Path("name") String pollutantName);

        @GET("latestupdate/{id}/chart/airindex/")
        Call<IndexChartData> getIndexChart(@Header("Authorization") String authorization, @Path("id") int id);

        @GET("alerts/{id}/")
        Call<Alert> getAlerts(@Header("Authorization") String authorization, @Path("id") int id);

        @GET("login/{username}/{password}/")
        Call<User> login(@Path("username") String username,@Path("password")String password);

        @FormUrlEncoded
        @POST("editsettings/")
        Call<NewSettings> editSettings(@Header("Authorization") String authorization, @Field("id") int id, @Field("name") String name, @Field("username") String username, @Field("password") String password);

        @FormUrlEncoded
        @POST("savetoken/")
        Call<ResponseBody> sendToken(@Header("Authorization") String authorization, @Field("id") int id, @Field("registration_id") String registration_id);

        @FormUrlEncoded
        @POST("logout/")
        Call<ResponseBody> logout(@Header("Authorization") String authorization, @Field("id") int id, @Field("registration_id") String registration_id);
    }

    //private static final String API_BASE_URL = "http://django-ricardomartins.rhcloud.com/rest/";
    private static final String API_BASE_URL = "http://idad-qualar.rhcloud.com/rest/";

    private static Retrofit retrofit = null;

    private static ApiInterface apiService = null;

    public static ApiInterface getService() {
        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            apiService = retrofit.create(ApiInterface.class);
        }
        return apiService;
    }

    private static User user = null;

    public static void setLoggedUser(int id, String username, String name, Boolean superuser, String token) {
        user = new User(id, username, name, superuser, token);
    }

    public static User getLoggedUser(){
        return user;
    };

}

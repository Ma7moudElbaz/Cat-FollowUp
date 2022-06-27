package com.example.followup.webservice;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;

import com.example.followup.login.LoginActivity;
import com.example.followup.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebserviceContext {

    private static final String MAIN_URL = Constants.MAIN_URL;

    private static WebserviceContext instance;
    private final ServiceInterface api;
    private static Activity mActivity;

    public WebserviceContext(Activity mActivity) {
        WebserviceContext.mActivity = mActivity;
        // OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(160, TimeUnit.SECONDS);
        httpClient.readTimeout(160, TimeUnit.SECONDS);
        httpClient.writeTimeout(160, TimeUnit.SECONDS);
        httpClient.addInterceptor(logging);
        // Add other Interceptors
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            Request request = original.newBuilder()
                    .method(original.method(), original.body())
                    .build();

            Response response = chain.proceed(request);
            Log.e("Response", "Code : " + response.code());

//            mActivity.runOnUiThread(() -> new AlertDialog.Builder(mActivity)
//                    .setTitle("test Done")
//                    .setPositiveButton("Done", (dialog, which) -> {
//                        dialog.dismiss();
//                    })
//                    .show());

            if (response.code() == 401) {
                mActivity.runOnUiThread(() -> new AlertDialog.Builder(mActivity)
                        .setTitle("Session Expired")
                        .setPositiveButton("Log In", (dialog, which) -> {
                            Intent i = new Intent(mActivity, LoginActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            mActivity.startActivity(i);
                        })
                        .setCancelable(false)
                        .show());
                return response;
            }
            return response;
        });

        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(MAIN_URL)
                .build();

        api = retrofit.create(ServiceInterface.class);
        Log.i("Api", "" + api);
    }

    public static WebserviceContext getInstance() {
        if (instance == null) {
            instance = new WebserviceContext(mActivity);
        }
        return instance;
    }

    public ServiceInterface getApi() {
        Log.i("Service Interface", "" + api.toString());
        return api;
    }

}

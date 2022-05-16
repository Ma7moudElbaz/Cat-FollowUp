package com.example.followup.webservice;

import android.util.Log;
import android.widget.Toast;

import com.example.followup.utils.UserUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Webservice {

//    private static final String MAIN_URL = "https://cpadmin.cat.com.eg/api/";
    //dev
    private static final String MAIN_URL = "https://cat-purchasing-app.cat-sw.com/api/";
    private static Webservice instance;
    private final ServiceInterface api;

    public Webservice() {
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

            Response response =  chain.proceed(request);
            Log.e("Response", "Code : "+response.code());
            if (response.code() == 401){
                // Magic is here ( Handle the error as your way )
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
        Log.i("Api", "" + api.toString());
    }

    public static Webservice getInstance() {
        if (instance == null) {
            instance = new Webservice();
        }
        return instance;
    }

    public ServiceInterface getApi() {
        Log.i("Service Interface", "" + api.toString());
        return api;
    }

}

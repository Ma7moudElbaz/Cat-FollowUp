package com.example.followup.webservice;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ServiceInterface {

    @POST("auth/login")
    @FormUrlEncoded
    Call<ResponseBody> login(@FieldMap Map<String, String> map);

    @POST("auth/me")
    @FormUrlEncoded
    Call<ResponseBody> getMyData(@Header("Authorization") String auth,@FieldMap Map<String, String> map);

    @PUT("token")
    @FormUrlEncoded
    Call<ResponseBody> updateToken(@Header("Authorization") String auth,@FieldMap Map<String, String> map);

    @GET("projects")
    Call<ResponseBody> getProjects(@Header("Authorization") String auth, @Query("page") int pageNo);

    @GET("requests")
    Call<ResponseBody> getRequests(@Header("Authorization") String auth, @Query("project_id") int project_id, @Query("type_id") int type_id, @Query("page") int pageNo);

    @POST("projects")
    @FormUrlEncoded
    Call<ResponseBody> addProject(@Header("Authorization") String auth,@FieldMap Map<String, String> map);


    @GET("unreadNotificationsNumber")
    Call<ResponseBody> getNotificationsNumber(@Header("Authorization") String auth);

    @POST("markAsRead")
    @FormUrlEncoded
    Call<ResponseBody> readNotification(@Header("Authorization") String auth,@FieldMap Map<String, String> map);


}
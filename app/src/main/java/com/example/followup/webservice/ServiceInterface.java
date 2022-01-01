package com.example.followup.webservice;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ServiceInterface {

    @POST("auth/login")
    @FormUrlEncoded
    Call<ResponseBody> login(@FieldMap Map<String, String> map);

    @PUT("token")
    @FormUrlEncoded
    Call<ResponseBody> updateToken(@Header("Authorization") String auth,@FieldMap Map<String, String> map);

    @GET("projects")
    Call<ResponseBody> getProjects(@Header("Authorization") String auth, @Query("page") int pageNo);

    @GET("requests")
    Call<ResponseBody> getRequests(@Header("Authorization") String auth, @Query("project_id") int project_id, @Query("type_id") int type_id, @Query("page") int pageNo);

    @GET("requests")
    Call<ResponseBody> getJobOrderRequests(@Header("Authorization") String auth, @Query("status") int status, @Query("project_id") int project_id, @Query("type_id") int type_id, @Query("page") int pageNo);

    @GET("job-orders")
    Call<ResponseBody> getJobOrders(@Header("Authorization") String auth, @Query("project_id") int project_id, @Query("page") int pageNo);

    @POST("projects")
    @FormUrlEncoded
    Call<ResponseBody> addProject(@Header("Authorization") String auth,@FieldMap Map<String, String> map);

    @POST("requests")
    @FormUrlEncoded
    Call<ResponseBody> addRequest(@Header("Authorization") String auth,@FieldMap Map<String, String> map);

    @POST("job-orders")
    @FormUrlEncoded
    Call<ResponseBody> addJobOrder(@Header("Authorization") String auth,@FieldMap Map<String, String> map);

    @POST("job-orders/status")
    @FormUrlEncoded
    Call<ResponseBody> changeJobOrderStatus(@Header("Authorization") String auth,@FieldMap Map<String, String> map);

    @POST("costs/status")
    @FormUrlEncoded
    Call<ResponseBody> changeCostStatus(@Header("Authorization") String auth,@FieldMap Map<String, String> map);

    @GET("requests/{request_id}")
    Call<ResponseBody> getRequestDetails(@Header("Authorization") String auth, @Path("request_id") int request_id);

    @POST("costs")
    @FormUrlEncoded
    Call<ResponseBody> addCost(@Header("Authorization") String auth,@FieldMap Map<String, String> map);

    @GET("unreadNotificationsNumber")
    Call<ResponseBody> getNotificationsNumber(@Header("Authorization") String auth);

    @POST("markAsRead")
    @FormUrlEncoded
    Call<ResponseBody> readNotification(@Header("Authorization") String auth,@FieldMap Map<String, String> map);

    @Multipart
    @POST("requests/attaches")
    Call<ResponseBody> addAttach(@Header("Authorization") String auth,@Part List<MultipartBody.Part> files, @Part("request_id") RequestBody request_id);

}
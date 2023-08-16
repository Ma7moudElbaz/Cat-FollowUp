package com.example.followup.webservice;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ServiceInterface {


    @GET("android_version")
    Call<ResponseBody> checkAppVersion(@Header("app-version") String version);

    //Auth
    @POST("auth/login")
    @FormUrlEncoded
    Call<ResponseBody> login(@FieldMap Map<String, String> map);

    @POST("auth/logout")
    Call<ResponseBody> logout(@Header("Authorization") String auth);

    @POST("password/change")
    @FormUrlEncoded
    Call<ResponseBody> changePassword(@Header("Authorization") String auth, @FieldMap Map<String, String> map);

    @POST("forget-password/create")
    @FormUrlEncoded
    Call<ResponseBody> forgetPassword(@FieldMap Map<String, String> map);

    @PUT("token")
    @FormUrlEncoded
    Call<ResponseBody> updateToken(@Header("Authorization") String auth, @FieldMap Map<String, String> map);

    @POST("auth/myteam")
    Call<ResponseBody> getMyTeam(@Header("Authorization") String auth);

    @GET("companies")
    Call<ResponseBody> getCompanies(@Header("Authorization") String auth, @Query("page") int pageNo, @QueryMap Map<String, String> filters);

    @GET("projects/suppliers/{project_id}")
    Call<ResponseBody> getSuppliers(@Header("Authorization") String auth, @Path("project_id") int project_id);

    @POST("companies")
    @FormUrlEncoded
    Call<ResponseBody> AddCompany(@Header("Authorization") String auth, @FieldMap Map<String, String> map);

    //projects
    @GET("projects")
    Call<ResponseBody> getProjects(@Header("Authorization") String auth, @Query("page") int pageNo, @QueryMap Map<String, String> filters);

    @GET("projects/all")
    Call<ResponseBody> getAllProjects(@Header("Authorization") String auth, @Query("page") int pageNo, @QueryMap Map<String, String> filters);

    @POST("projects")
    @FormUrlEncoded
    Call<ResponseBody> addProject(@Header("Authorization") String auth, @FieldMap Map<String, String> map);

    @GET("projects/{project_id}")
    Call<ResponseBody> getProjectDetails(@Header("Authorization") String auth, @Path("project_id") int project_id);

    @PUT("projects/{project_id}/done")
    Call<ResponseBody> projectDone(@Header("Authorization") String auth, @Path("project_id") int project_id);

    @PUT("projects/{project_id}/cancel")
    Call<ResponseBody> projectCancel(@Header("Authorization") String auth, @Path("project_id") int project_id);

    //requests

    @GET("requests/all")
    Call<ResponseBody> getAllRequests(@Header("Authorization") String auth, @Query("page") int pageNo, @QueryMap Map<String, String> filters);

    @GET("requests")
    Call<ResponseBody> getRequests(@Header("Authorization") String auth, @Query("project_id") int project_id, @Query("type_id") int type_id, @Query("page") int pageNo, @QueryMap Map<String, String> filters);

    @GET("requests")
    Call<ResponseBody> getJobOrderRequests(@Header("Authorization") String auth, @Query("status") int status, @Query("project_id") int project_id, @Query("type_id") int type_id, @Query("page") int pageNo);

    @GET("request/extras")
    Call<ResponseBody> getJobOrderExtras(@Header("Authorization") String auth, @Query("project_id") int project_id, @Query("extra_request_type") int type_id);

    @GET("job-order/edit")
    Call<ResponseBody> getEditJobOrderRequests(@Header("Authorization") String auth, @Query("job_order_id") int job_order_id);

    @POST("requests")
    @FormUrlEncoded
    Call<ResponseBody> addRequest(@Header("Authorization") String auth, @FieldMap Map<String, String> map);

    @POST("request/reject")
    @FormUrlEncoded
    Call<ResponseBody> rejectRequest(@Header("Authorization") String auth, @FieldMap Map<String, String> map);


    @PUT("requests/{request_id}")
    @FormUrlEncoded
    Call<ResponseBody> editRequest(@Header("Authorization") String auth, @Path("request_id") int request_id, @FieldMap Map<String, String> map);

    @Multipart
    @POST("requests/attaches")
    Call<ResponseBody> addAttach(@Header("Authorization") String auth, @Part List<MultipartBody.Part> files, @Part("request_id") RequestBody request_id);

    @GET("requests/{request_id}")
    Call<ResponseBody> getRequestDetails(@Header("Authorization") String auth, @Path("request_id") int request_id);

    @DELETE("requests/{request_id}")
    Call<ResponseBody> deleteRequest(@Header("Authorization") String auth, @Path("request_id") int request_id);


    @POST("requests/action/{request_id}")
    @FormUrlEncoded
    Call<ResponseBody> cancelRequest(@Header("Authorization") String auth, @Path("request_id") int request_id, @FieldMap Map<String, String> map);

    //costs
    @Multipart
    @POST("costs")
    Call<ResponseBody> addCostData(@Header("Authorization") String auth, @Part List<MultipartBody.Part> files, @PartMap Map<String, RequestBody> map);

    @Multipart
    @POST("costs/update/{cost_id}")
    Call<ResponseBody> editCost(@Header("Authorization") String auth, @Path("cost_id") int cost_id, @Part List<MultipartBody.Part> files, @PartMap Map<String, RequestBody> map);

    @POST("costs/status")
    @FormUrlEncoded
    Call<ResponseBody> changeCostStatus(@Header("Authorization") String auth, @FieldMap Map<String, String> map);

    //job orders
    @GET("job-order/all")
    Call<ResponseBody> getAllJobOrders(@Header("Authorization") String auth, @Query("page") int pageNo, @QueryMap Map<String, String> filters);

    @GET("job-orders")
    Call<ResponseBody> getJobOrders(@Header("Authorization") String auth, @Query("project_id") int project_id, @Query("page") int pageNo, @QueryMap Map<String, String> filters);

    @GET("job-orders/{job_order_id}")
    Call<ResponseBody> getJobOrderDetails(@Header("Authorization") String auth, @Path("job_order_id") int job_order_id);

    @POST("job-orders")
    @FormUrlEncoded
    Call<ResponseBody> addJobOrder(@Header("Authorization") String auth, @FieldMap Map<String, String> map);

    @PUT("job-order/update")
    @FormUrlEncoded
    Call<ResponseBody> editJobOrder(@Header("Authorization") String auth, @FieldMap Map<String, String> map);

    @POST("job-order/remove")
    @FormUrlEncoded
    Call<ResponseBody> deleteJobOrderRequest(@Header("Authorization") String auth, @FieldMap Map<String, String> map);

    @POST("job-orders/status")
    @FormUrlEncoded
    Call<ResponseBody> changeJobOrderStatus(@Header("Authorization") String auth, @FieldMap Map<String, String> map);

    @POST("job-orders/reminder")
    @FormUrlEncoded
    Call<ResponseBody> sendJobOrderReminder(@Header("Authorization") String auth, @FieldMap Map<String, String> map);

    @POST("costs/reminder")
    @FormUrlEncoded
    Call<ResponseBody> sendCostReminder(@Header("Authorization") String auth, @FieldMap Map<String, String> map);

    @Multipart
    @POST("payments")
    Call<ResponseBody> addPayment(@Header("Authorization") String auth, @Part List<MultipartBody.Part> files, @PartMap Map<String, RequestBody> map);


    @POST("pos")
    @FormUrlEncoded
    Call<ResponseBody> addPoNumber(@Header("Authorization") String auth, @FieldMap Map<String, String> map);

    //Comment
    @Multipart
    @POST("comments")
    Call<ResponseBody> addComment(@Header("Authorization") String auth, @Part List<MultipartBody.Part> files, @PartMap Map<String, RequestBody> map);

    @GET("comments")
    Call<ResponseBody> getComments(@Header("Authorization") String auth, @Query("job_order_id") int job_order_id, @Query("page") int pageNo);

    @GET("comments/users")
    Call<ResponseBody> getMentionUsers(@Header("Authorization") String auth, @Query("project_id") int project_id);

    //notifications
    @GET("notifications")
    Call<ResponseBody> getNotifications(@Header("Authorization") String auth, @Query("page") int pageNo, @QueryMap Map<String, String> filters);

    @GET("unreadNotificationsNumber")
    Call<ResponseBody> getNotificationsNumber(@Header("Authorization") String auth);

    @POST("markAsRead")
    @FormUrlEncoded
    Call<ResponseBody> readNotification(@Header("Authorization") String auth, @FieldMap Map<String, String> map);
}
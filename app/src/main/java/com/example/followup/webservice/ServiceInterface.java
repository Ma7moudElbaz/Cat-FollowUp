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




    @GET("purchasings")
    Call<ResponseBody> getPurchasingRequests(@Header("Authorization") String auth, @Query("page") int pageNo);

    @GET("purchasings/{id}")
    Call<ResponseBody> getSinglePurchasingRequest(@Header("Authorization") String auth, @Path("id") int id);

    @POST("purchasings/store")
    @FormUrlEncoded
    Call<ResponseBody> addPurchasingRequest(@Header("Authorization") String auth,@FieldMap Map<String, String> map);

    @POST("purchasings/quotation")
    @FormUrlEncoded
    Call<ResponseBody> addPurchasingQuotation(@Header("Authorization") String auth,@FieldMap Map<String, String> map);

    @PUT("purchasings/quotation/status")
    @FormUrlEncoded
    Call<ResponseBody> changePurchasingQuotationStatus(@Header("Authorization") String auth,@FieldMap Map<String, String> map);

    @PUT("purchasings/payment")
    @FormUrlEncoded
    Call<ResponseBody> changePurchasingPayment(@Header("Authorization") String auth, @FieldMap Map<String, String> map);

    @PUT("purchasings/approve")
    @FormUrlEncoded
    Call<ResponseBody> approvePurchasing(@Header("Authorization") String auth, @FieldMap Map<String, String> map);

    @GET("printings")
    Call<ResponseBody> getPrintingRequests(@Header("Authorization") String auth, @Query("page") int pageNo);

    @GET("printings/{id}")
    Call<ResponseBody> getSinglePrintingRequest(@Header("Authorization") String auth, @Path("id") int id);

    @POST("printings/store")
    @FormUrlEncoded
    Call<ResponseBody> addPrintingRequest(@Header("Authorization") String auth,@FieldMap Map<String, String> map);

    @POST("printings/quotation")
    @FormUrlEncoded
    Call<ResponseBody> addPrintingQuotation(@Header("Authorization") String auth,@FieldMap Map<String, String> map);

    @PUT("printings/quotation/status")
    @FormUrlEncoded
    Call<ResponseBody> changePrintingQuotationStatus(@Header("Authorization") String auth, @FieldMap Map<String, String> map);

    @PUT("printings/payment")
    @FormUrlEncoded
    Call<ResponseBody> changePrintingPayment(@Header("Authorization") String auth, @FieldMap Map<String, String> map);

    @PUT("printings/approve")
    @FormUrlEncoded
    Call<ResponseBody> approvePrinting(@Header("Authorization") String auth, @FieldMap Map<String, String> map);

    @GET("productions")
    Call<ResponseBody> getProductionRequests(@Header("Authorization") String auth, @Query("page") int pageNo);

    @GET("productions/{id}")
    Call<ResponseBody> getSingleProductionRequest(@Header("Authorization") String auth, @Path("id") int id);

    @POST("productions/store")
    @FormUrlEncoded
    Call<ResponseBody> addProductionRequest(@Header("Authorization") String auth,@FieldMap Map<String, String> map);

    @POST("productions/quotation")
    @FormUrlEncoded
    Call<ResponseBody> addProductionQuotation(@Header("Authorization") String auth,@FieldMap Map<String, String> map);

    @PUT("productions/quotation/status")
    @FormUrlEncoded
    Call<ResponseBody> changeProductionQuotationStatus(@Header("Authorization") String auth,@FieldMap Map<String, String> map);

    @PUT("productions/payment")
    @FormUrlEncoded
    Call<ResponseBody> changeProductionPayment(@Header("Authorization") String auth, @FieldMap Map<String, String> map);

    @PUT("productions/approve")
    @FormUrlEncoded
    Call<ResponseBody> approveProduction(@Header("Authorization") String auth, @FieldMap Map<String, String> map);

    @GET("notifications")
    Call<ResponseBody> getNotifications(@Header("Authorization") String auth, @Query("page") int pageNo);

    @GET("unreadNotificationsNumber")
    Call<ResponseBody> getNotificationsNumber(@Header("Authorization") String auth);

    @POST("markAsRead")
    @FormUrlEncoded
    Call<ResponseBody> readNotification(@Header("Authorization") String auth,@FieldMap Map<String, String> map);


}
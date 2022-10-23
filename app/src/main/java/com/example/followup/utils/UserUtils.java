package com.example.followup.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class UserUtils {

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("APP_PREF", Context.MODE_PRIVATE);
    }

    public static void setAccessToken(Context context, String accessToken) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("accessToken", "Bearer "+accessToken);
        editor.apply();
    }


    public static String getAccessToken(Context context) {
        return getSharedPreferences(context).getString("accessToken", "default");
    }

    public static void setLoginData(Context context,String loginName,String loginPassword){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("loginName", loginName);
        editor.putString("loginPassword", loginPassword);
        editor.apply();
    }

    public static String getLoginName(Context context) {
        return getSharedPreferences(context).getString("loginName", "");
    }

    public static String getLoginPassword(Context context) {
        return getSharedPreferences(context).getString("loginPassword", "");
    }


    public static void setUserId(Context context, int user_id) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt("user_id", user_id);
        editor.apply();
    }

    public static int getUserId(Context context) {
        return getSharedPreferences(context).getInt("user_id", 0);
    }


    public static void setCountryId(Context context, int country_id) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt("country_id", country_id);
        editor.apply();
    }

    public static int getCountryId(Context context) {
        return getSharedPreferences(context).getInt("country_id", 0);
    }

    public static void setParentId(Context context, int parent_id) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt("parent_id", parent_id);
        editor.apply();
    }

    public static int getParentId(Context context) {
        return getSharedPreferences(context).getInt("parent_id", 0);
    }



    public static void setChildId(Context context, int parent_id) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt("child_id", parent_id);
        editor.apply();
    }

    public static int getChildId(Context context) {
        return getSharedPreferences(context).getInt("child_id", 0);
    }

    public static void setUserName(Context context,String userName){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("userName", userName);
        editor.apply();
    }


    public static String getUserName(Context context) {
        return getSharedPreferences(context).getString("userName", "");
    }

    public static void setUserEmail(Context context,String userName){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("userEmail", userName);
        editor.apply();
    }


    public static String getUserEmail(Context context) {
        return getSharedPreferences(context).getString("userEmail", "");
    }
}

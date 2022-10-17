package com.example.followup.job_orders.job_order_details.comments.mentions;

import androidx.annotation.NonNull;

@SuppressWarnings("WeakerAccess")
public class User {

    private int user_id;
    private String username;

    public User(@NonNull int user_id, @NonNull String username) {
        this.user_id = user_id;
        this.username = username;
    }



    @NonNull
    public String getUsername() {
        return username;
    }

    @NonNull
    public int getUser_id() {
        return user_id;
    }

}

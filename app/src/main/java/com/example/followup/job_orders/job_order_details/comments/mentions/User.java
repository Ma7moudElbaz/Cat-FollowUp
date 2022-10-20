package com.example.followup.job_orders.job_order_details.comments.mentions;

import androidx.annotation.NonNull;

public class User {

    private final int user_id;
    private final String username;

    public User(int user_id, @NonNull String username) {
        this.user_id = user_id;
        this.username = username;
    }



    @NonNull
    public String getUsername() {
        return username;
    }

    public int getUser_id() {
        return user_id;
    }

}

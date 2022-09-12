package com.example.followup.job_orders.job_order_details.comments;

public class Comment_item {
    private final int id, user_id;
    private final String comment,user_name,user_avatar,created_at,attachment;

    public Comment_item(int id, int user_id, String comment, String user_name, String user_avatar, String created_at, String attachment) {
        this.id = id;
        this.user_id = user_id;
        this.comment = comment;
        this.user_name = user_name;
        this.user_avatar = user_avatar;
        this.created_at = created_at;
        this.attachment = attachment;
    }

    public int getId() {
        return id;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getComment() {
        return comment;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getUser_avatar() {
        return user_avatar;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getAttachment() {
        return attachment;
    }
}

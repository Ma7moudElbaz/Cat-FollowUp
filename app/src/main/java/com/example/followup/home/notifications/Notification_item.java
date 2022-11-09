package com.example.followup.home.notifications;

import java.io.Serializable;

public class Notification_item implements Serializable {
    private final int action_id;
    private final String notification_id,from,to,subject,message,action_type,read_at,created_at,updated_at;
    private final boolean needAction;


    public Notification_item(int action_id, String notification_id, String from, String to, String subject, String message, String action_type, String read_at, String created_at, String updated_at, boolean needAction) {
        this.action_id = action_id;
        this.notification_id = notification_id;
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.message = message;
        this.action_type = action_type;
        this.read_at = read_at;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.needAction = needAction;
    }

    public int getAction_id() {
        return action_id;
    }

    public String getNotification_id() {
        return notification_id;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessage() {
        return message;
    }

    public String getAction_type() {
        return action_type;
    }

    public String getRead_at() {
        return read_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public boolean isNeedAction() {
        return needAction;
    }
}

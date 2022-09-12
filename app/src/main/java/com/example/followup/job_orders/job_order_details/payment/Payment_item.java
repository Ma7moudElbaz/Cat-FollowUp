package com.example.followup.job_orders.job_order_details.payment;

public class Payment_item {
    private final int id, job_order_id;
    private final String percentage,attachment,created_at;

    public Payment_item(int id, int job_order_id, String percentage, String attachment, String created_at) {
        this.id = id;
        this.job_order_id = job_order_id;
        this.percentage = percentage;
        this.attachment = attachment;
        this.created_at = created_at;
    }

    public int getId() {
        return id;
    }

    public int getJob_order_id() {
        return job_order_id;
    }

    public String getPercentage() {
        return percentage;
    }

    public String getAttachment() {
        return attachment;
    }

    public String getCreated_at() {
        return created_at;
    }
}

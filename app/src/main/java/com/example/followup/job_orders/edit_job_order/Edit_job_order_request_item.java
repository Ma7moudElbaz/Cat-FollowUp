package com.example.followup.job_orders.edit_job_order;

public class Edit_job_order_request_item {
    private final int request_id;
    private final String request_name;
    private String actual_cost;

    public Edit_job_order_request_item(int request_id, String request_name, String actual_cost) {
        this.request_id = request_id;
        this.request_name = request_name;
        this.actual_cost = actual_cost;
    }

    public int getRequest_id() {
        return request_id;
    }

    public String getRequest_name() {
        return request_name;
    }

    public String getActual_cost() {
        return actual_cost;
    }

    public void setActual_cost(String actual_cost) {
        this.actual_cost = actual_cost;
    }
}

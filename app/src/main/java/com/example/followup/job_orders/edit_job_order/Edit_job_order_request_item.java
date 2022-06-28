package com.example.followup.job_orders.edit_job_order;

public class Edit_job_order_request_item {
    private final int request_id;
    private final String request_name, cost_type,cost_per_id;
    private String actual_cost , quantity ;

    public Edit_job_order_request_item(int request_id, String request_name, String cost_type, String cost_per_id, String quantity, String actual_cost) {
        this.request_id = request_id;
        this.request_name = request_name;
        this.cost_type = cost_type;
        this.cost_per_id = cost_per_id;
        this.quantity = quantity;
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

    public String getCost_type() {
        return cost_type;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getCost_per_id() {
        return cost_per_id;
    }
}

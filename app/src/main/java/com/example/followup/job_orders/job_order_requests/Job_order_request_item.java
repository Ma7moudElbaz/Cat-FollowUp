package com.example.followup.job_orders.job_order_requests;

public class Job_order_request_item {
    private final int id;
    private  String request_id,final_cost,quantity;
    private  boolean checked;

    public Job_order_request_item(int id, String request_id, String final_cost, String quantity, boolean checked) {
        this.id = id;
        this.request_id = request_id;
        this.final_cost = final_cost;
        this.quantity = quantity;
        this.checked = checked;
    }

    public int getId() {
        return id;
    }

    public String getRequest_id() {
        return request_id;
    }

    public String getFinal_cost() {
        return final_cost;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public void setFinal_cost(String final_cost) {
        this.final_cost = final_cost;
    }

    public String getQuantity() {
        return quantity;
    }
}

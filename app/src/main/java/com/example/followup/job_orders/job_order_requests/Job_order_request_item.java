package com.example.followup.job_orders.job_order_requests;

public class Job_order_request_item {
    private final int id ,type_id;
    private final String request_name;
    private String final_cost;
    private final String quantity,cost_type;
    private  boolean checked;

    public Job_order_request_item(int id, int type_id, String request_name, String final_cost, String quantity, String cost_type, boolean checked) {
        this.id = id;
        this.type_id = type_id;
        this.request_name = request_name;
        this.final_cost = final_cost;
        this.quantity = quantity;
        this.cost_type = cost_type;
        this.checked = checked;
    }

    public int getId() {
        return id;
    }

    public String getRequest_name() {
        return request_name;
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


    public void setFinal_cost(String final_cost) {
        this.final_cost = final_cost;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getCost_type() {
        return cost_type;
    }

    public int getType_id() {
        return type_id;
    }
}

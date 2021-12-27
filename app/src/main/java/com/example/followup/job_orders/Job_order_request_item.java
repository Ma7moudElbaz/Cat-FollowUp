package com.example.followup.job_orders;

public class Job_order_request_item {
    private final int id;
    private final String request_id,final_cost;
    private final boolean checked;

    public Job_order_request_item(int id, String request_id, String final_cost, boolean checked) {
        this.id = id;
        this.request_id = request_id;
        this.final_cost = final_cost;
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
}

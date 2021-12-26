package com.example.followup.job_orders;

public class Job_order_item {
    private final int id, project_id_status_code;
    private final String status_message,name,po_number;


    public Job_order_item(int id, int project_id_status_code, String status_message, String name, String po_number) {
        this.id = id;
        this.project_id_status_code = project_id_status_code;
        this.status_message = status_message;
        this.name = name;
        this.po_number = po_number;
    }

    public int getId() {
        return id;
    }

    public int getProject_id_status_code() {
        return project_id_status_code;
    }

    public String getStatus_message() {
        return status_message;
    }

    public String getName() {
        return name;
    }

    public String getPo_number() {
        return po_number;
    }
}

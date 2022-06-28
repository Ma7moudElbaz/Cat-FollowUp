package com.example.followup.job_orders.list;

public class Job_order_item {
    private final int id, project_id,status_code;
    private final String status_message,name,po_number;
    private final boolean have_action;

    public Job_order_item(int id, int project_id, int status_code, String status_message, String name, String po_number, boolean have_action) {
        this.id = id;
        this.project_id = project_id;
        this.status_code = status_code;
        this.status_message = status_message;
        this.name = name;
        this.po_number = po_number;
        this.have_action = have_action;
    }

    public int getId() {
        return id;
    }

    public int getProject_id() {
        return project_id;
    }

    public int getStatus_code() {
        return status_code;
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

    public boolean isHave_action() {
        return have_action;
    }
}

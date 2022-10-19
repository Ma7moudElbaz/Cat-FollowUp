package com.example.followup.home.all_requests;


public class Request_item {

    private final int id, type_id,created_by_id,project_creator_id,project_assign_id, status_code,cost_status_code;

    private final String project_name,request_name,request_type,status,created_by;
    private final boolean have_action;


    public Request_item(int id, int type_id, int created_by_id, int project_creator_id, int project_assign_id, int status_code, int cost_status_code, String project_name, String request_name, String request_type, String status, String created_by, boolean have_action) {
        this.id = id;
        this.type_id = type_id;
        this.created_by_id = created_by_id;
        this.project_creator_id = project_creator_id;
        this.project_assign_id = project_assign_id;
        this.status_code = status_code;
        this.cost_status_code = cost_status_code;
        this.project_name = project_name;
        this.request_name = request_name;
        this.request_type = request_type;
        this.status = status;
        this.created_by = created_by;
        this.have_action = have_action;
    }

    public int getId() {
        return id;
    }

    public int getType_id() {
        return type_id;
    }

    public int getCreated_by_id() {
        return created_by_id;
    }

    public int getProject_creator_id() {
        return project_creator_id;
    }

    public int getProject_assign_id() {
        return project_assign_id;
    }

    public int getStatus_code() {
        return status_code;
    }

    public int getCost_status_code() {
        return cost_status_code;
    }

    public String getProject_name() {
        return project_name;
    }

    public String getRequest_name() {
        return request_name;
    }

    public String getRequest_type() {
        return request_type;
    }

    public String getStatus() {
        return status;
    }

    public String getCreated_by() {
        return created_by;
    }

    public boolean isHave_action() {
        return have_action;
    }
}

package com.example.followup.requests.list.models;


public class Extras_item {

    private final int id, type_id,created_by_id,project_creator_id,project_assign_id, status_code,cost_status_code;
    private final String item_name,description, delivery_address,status_message,created_by_name;
    private final boolean have_action;

    public Extras_item(int id, int type_id, int created_by_id, int project_creator_id, int project_assign_id, int status_code, int cost_status_code, String item_name, String description, String delivery_address, String status_message, String created_by_name, boolean have_action) {
        this.id = id;
        this.type_id = type_id;
        this.created_by_id = created_by_id;
        this.project_creator_id = project_creator_id;
        this.project_assign_id = project_assign_id;
        this.status_code = status_code;
        this.cost_status_code = cost_status_code;
        this.item_name = item_name;
        this.description = description;
        this.delivery_address = delivery_address;
        this.status_message = status_message;
        this.created_by_name = created_by_name;
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

    public String getItem_name() {
        return item_name;
    }

    public String getDescription() {
        return description;
    }

    public String getDelivery_address() {
        return delivery_address;
    }

    public boolean isHave_action() {
        return have_action;
    }

    public String getStatus_message() {
        return status_message;
    }

    public String getCreated_by_name() {
        return created_by_name;
    }
}

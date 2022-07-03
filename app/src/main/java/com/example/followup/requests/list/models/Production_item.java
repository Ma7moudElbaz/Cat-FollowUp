package com.example.followup.requests.list.models;


import com.example.followup.requests.view.attaches.Attach_item;

import java.util.ArrayList;

public class Production_item {

    private final int id, type_id,created_by_id,project_creator_id,project_assign_id, status_code,cost_status_code;
    private final String quantity,status_message,item_name,description,delivery_date,note,country,venue,days,dimensions,screen,designer_name,created_by_name;
    private final ArrayList<Attach_item> attach_files;
    private final boolean have_action;

    public Production_item(int id, int type_id, int created_by_id, int project_creator_id, int project_assign_id, int status_code, int cost_status_code, String quantity, String status_message, String item_name, String description, String delivery_date, String note, String country, String venue, String days, String dimensions, String screen, String designer_name, String created_by_name, ArrayList<Attach_item> attach_files, boolean have_action) {
        this.id = id;
        this.type_id = type_id;
        this.created_by_id = created_by_id;
        this.project_creator_id = project_creator_id;
        this.project_assign_id = project_assign_id;
        this.status_code = status_code;
        this.cost_status_code = cost_status_code;
        this.quantity = quantity;
        this.status_message = status_message;
        this.item_name = item_name;
        this.description = description;
        this.delivery_date = delivery_date;
        this.note = note;
        this.country = country;
        this.venue = venue;
        this.days = days;
        this.dimensions = dimensions;
        this.screen = screen;
        this.designer_name = designer_name;
        this.created_by_name = created_by_name;
        this.attach_files = attach_files;
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

    public int getStatus_code() {
        return status_code;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getStatus_message() {
        return status_message;
    }

    public String getItem_name() {
        return item_name;
    }

    public String getDescription() {
        return description;
    }


    public String getNote() {
        return note;
    }

    public String getCountry() {
        return country;
    }

    public String getVenue() {
        return venue;
    }

    public String getDays() {
        return days;
    }

    public String getDimensions() {
        return dimensions;
    }

    public String getScreen() {
        return screen;
    }

    public String getDesigner_name() {
        return designer_name;
    }

    public String getCreated_by_name() {
        return created_by_name;
    }

    public ArrayList<Attach_item> getAttach_files() {
        return attach_files;
    }

    public String getDelivery_date() {
        return delivery_date;
    }

    public boolean isHave_action() {
        return have_action;
    }

    public int getCost_status_code() {
        return cost_status_code;
    }

    public int getProject_creator_id() {
        return project_creator_id;
    }

    public int getProject_assign_id() {
        return project_assign_id;
    }
}

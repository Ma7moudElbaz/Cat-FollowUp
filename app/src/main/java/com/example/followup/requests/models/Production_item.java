package com.example.followup.requests.models;

import com.example.followup.home.Attach_item;

import java.util.ArrayList;

public class Production_item {

    private final int id, type_id,created_by_id, status_code,quantity;
    private final String status_message,item_name,description,delivery_address,note,country,venue,days,dimensions,screen,designer_name,created_by_name;
    private final ArrayList<Attach_item> attach_files;

    public Production_item(int id, int type_id, int created_by_id, int status_code, int quantity, String status_message, String item_name, String description, String delivery_address, String note, String country, String venue, String days, String dimensions, String screen, String designer_name, String created_by_name, ArrayList<Attach_item> attach_files) {
        this.id = id;
        this.type_id = type_id;
        this.created_by_id = created_by_id;
        this.status_code = status_code;
        this.quantity = quantity;
        this.status_message = status_message;
        this.item_name = item_name;
        this.description = description;
        this.delivery_address = delivery_address;
        this.note = note;
        this.country = country;
        this.venue = venue;
        this.days = days;
        this.dimensions = dimensions;
        this.screen = screen;
        this.designer_name = designer_name;
        this.created_by_name = created_by_name;
        this.attach_files = attach_files;
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

    public int getQuantity() {
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

    public String getDelivery_address() {
        return delivery_address;
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
}

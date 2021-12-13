package com.example.followup.requests.photography;

import com.example.followup.home.Attach_item;

import java.util.ArrayList;

public class Photography_item {

    private final int id, type_id,created_by_id, status_code,quantity;
    private final String status_message,item_name,description,delivery_address,note,country,location,days,project_type,camera_type,numbers_cameras,lighting,chroma,props,created_by_name;
    private final ArrayList<Attach_item> attach_files;

    public Photography_item(int id, int type_id, int created_by_id, int status_code, int quantity, String status_message, String item_name, String description, String delivery_address, String note, String country, String location, String days, String project_type, String camera_type, String numbers_cameras, String lighting, String chroma, String props, String created_by_name, ArrayList<Attach_item> attach_files) {
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
        this.location = location;
        this.days = days;
        this.project_type = project_type;
        this.camera_type = camera_type;
        this.numbers_cameras = numbers_cameras;
        this.lighting = lighting;
        this.chroma = chroma;
        this.props = props;
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

    public String getLocation() {
        return location;
    }

    public String getDays() {
        return days;
    }

    public String getProject_type() {
        return project_type;
    }

    public String getCamera_type() {
        return camera_type;
    }

    public String getNumbers_cameras() {
        return numbers_cameras;
    }

    public String getLighting() {
        return lighting;
    }

    public String getChroma() {
        return chroma;
    }

    public String getProps() {
        return props;
    }

    public String getCreated_by_name() {
        return created_by_name;
    }

    public ArrayList<Attach_item> getAttach_files() {
        return attach_files;
    }
}

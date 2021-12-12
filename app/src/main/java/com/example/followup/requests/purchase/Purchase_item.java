package com.example.followup.requests.purchase;

import java.util.ArrayList;

public class Purchase_item {

    private final int id, type_id, status_code, quantity;
    private final String status_message,item_name, description, delivery_address, note, color, material, brand, created_by_id, created_by_name;
    private final ArrayList<String> attach_files;

    public Purchase_item(int id, int type_id, int status_code, int quantity, String status_message, String item_name, String description, String delivery_address, String note, String color, String material, String brand, String created_by_id, String created_by_name, ArrayList<String> attach_files) {
        this.id = id;
        this.type_id = type_id;
        this.status_code = status_code;
        this.quantity = quantity;
        this.status_message = status_message;
        this.item_name = item_name;
        this.description = description;
        this.delivery_address = delivery_address;
        this.note = note;
        this.color = color;
        this.material = material;
        this.brand = brand;
        this.created_by_id = created_by_id;
        this.created_by_name = created_by_name;
        this.attach_files = attach_files;
    }

    public int getId() {
        return id;
    }

    public int getType_id() {
        return type_id;
    }

    public int getStatus_code() {
        return status_code;
    }

    public int getQuantity() {
        return quantity;
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

    public String getColor() {
        return color;
    }

    public String getMaterial() {
        return material;
    }

    public String getBrand() {
        return brand;
    }

    public String getCreated_by_id() {
        return created_by_id;
    }

    public String getCreated_by_name() {
        return created_by_name;
    }

    public ArrayList<String> getAttach_files() {
        return attach_files;
    }

    public String getStatus_message() {
        return status_message;
    }
}
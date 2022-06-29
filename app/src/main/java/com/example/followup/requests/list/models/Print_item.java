package com.example.followup.requests.list.models;


import com.example.followup.requests.view.attaches.Attach_item;

import java.util.ArrayList;

public class Print_item {
    private final int id, type_id,created_by_id, status_code;
    private final String quantity,status_message,item_name,description,delivery_address,note,pages,paper_weight,print_type,colors, lamination,binding,di_cut,designer_name,created_by_name;
    private final ArrayList<Attach_item> attach_files;
    private final boolean have_action;


    public Print_item(int id, int type_id, int created_by_id, int status_code, String quantity, String status_message, String item_name, String description, String delivery_address, String note, String pages, String paper_weight, String print_type, String colors, String lamination, String binding, String di_cut, String designer_name, String created_by_name, ArrayList<Attach_item> attach_files, boolean have_action) {
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
        this.pages = pages;
        this.paper_weight = paper_weight;
        this.print_type = print_type;
        this.colors = colors;
        this.lamination = lamination;
        this.binding = binding;
        this.di_cut = di_cut;
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

    public String getDelivery_address() {
        return delivery_address;
    }

    public String getNote() {
        return note;
    }

    public String getPages() {
        return pages;
    }

    public String getPaper_weight() {
        return paper_weight;
    }

    public String getPrint_type() {
        return print_type;
    }

    public String getColors() {
        return colors;
    }

    public String getLamination() {
        return lamination;
    }

    public String getBinding() {
        return binding;
    }

    public String getDi_cut() {
        return di_cut;
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

    public boolean isHave_action() {
        return have_action;
    }
}

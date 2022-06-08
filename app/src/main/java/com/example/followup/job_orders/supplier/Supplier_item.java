package com.example.followup.job_orders.supplier;

public class Supplier_item {
    private final String id,name;

    public Supplier_item(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

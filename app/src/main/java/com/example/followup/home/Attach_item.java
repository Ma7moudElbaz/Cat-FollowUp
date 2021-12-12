package com.example.followup.home;


import java.io.Serializable;

public class Attach_item implements Serializable {
    private final int id;
    private final String file,created_at;

    public Attach_item(int id, String file, String created_at) {
        this.id = id;
        this.file = file;
        this.created_at = created_at;
    }

    public int getId() {
        return id;
    }

    public String getFile() {
        return file;
    }

    public String getCreated_at() {
        return created_at;
    }
}
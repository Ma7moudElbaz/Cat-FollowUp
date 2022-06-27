package com.example.followup.requests.view.attaches;


import java.io.Serializable;

public class Attach_item implements Serializable {
    private final int id;
    private final String file_url,created_at;

    public Attach_item(int id, String file_url, String created_at) {
        this.id = id;
        this.file_url = file_url;
        this.created_at = created_at;
    }

    public int getId() {
        return id;
    }

    public String getFile_url() {
        return file_url;
    }

    public String getCreated_at() {
        return created_at;
    }
}
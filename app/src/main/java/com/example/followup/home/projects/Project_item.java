package com.example.followup.home.projects;


import java.io.Serializable;

public class Project_item implements Serializable {
    private final int id, user_id,status;
    private final String client_company,project_name,client_name,project_country,project_timeline,created_at,created_by;

    public Project_item(int id, int user_id, int status, String client_company, String project_name, String client_name, String project_country, String project_timeline, String created_at, String created_by) {
        this.id = id;
        this.user_id = user_id;
        this.status = status;
        this.client_company = client_company;
        this.project_name = project_name;
        this.client_name = client_name;
        this.project_country = project_country;
        this.project_timeline = project_timeline;
        this.created_at = created_at;
        this.created_by = created_by;
    }

    public int getId() {
        return id;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getStatus() {
        return status;
    }

    public String getClient_company() {
        return client_company;
    }

    public String getProject_name() {
        return project_name;
    }

    public String getClient_name() {
        return client_name;
    }

    public String getProject_country() {
        return project_country;
    }

    public String getProject_timeline() {
        return project_timeline;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getCreated_by() {
        return created_by;
    }
}
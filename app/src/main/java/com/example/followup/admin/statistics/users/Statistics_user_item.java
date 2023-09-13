package com.example.followup.admin.statistics.users;

public class Statistics_user_item {
    private final int id, projects_no, requests_no;
    private final String name;


    public Statistics_user_item(int id, String name, int projectsNo, int requestsNo) {
        this.id = id;
        projects_no = projectsNo;
        requests_no = requestsNo;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public int getProjects_no() {
        return projects_no;
    }

    public int getRequests_no() {
        return requests_no;
    }

    public String getName() {
        return name;
    }
}

package com.example.followup.utils;

import android.content.Context;

public class UserType {

    public static String getUserType(int parent_id, int child_id) {
        String userType;
        switch (parent_id) {
            case 1:
                userType = "admin";
                break;
            case 2:
                userType = "sales";
                break;
            case 9:
                userType = "magdi";
                break;
            case 10:
                userType = "hesham";
                break;
            case 11:
                userType = "ceo";
                break;
            case 4:
                if (child_id == 0) {
                    userType = "nagat";
                } else {
                    userType = "nagatTeam";
                }
                break;
            default:
                userType = "unknown";
                break;
        }
        return userType;
    }

    public static boolean canEditProject( Context mContext,int projectOwnerId, int assignedToId) {
        return projectOwnerId == UserUtils.getUserId(mContext) ||
                assignedToId == UserUtils.getUserId(mContext);
    }
}

package com.example.followup.utils;

import android.content.Context;

public class UserType {

    public static String getUserType(int parent_id, int child_id, int country_id) {
        String userType;
        switch (parent_id) {
            case 1:
                userType = "admin";
                break;
            case 2:
                userType = "sales";
                break;
            case 9:
                if (country_id == 1) {
                    userType = "magdi";
                } else {
                    userType = "hazem";
                }
                break;
            case 10:
                if (country_id == 1) {
                    userType = "hesham";
                } else {
                    userType = "hany";
                }
                break;
            case 11:
                userType = "ceo";
                break;
            case 4:
                if (country_id == 1) {
                    if (child_id == 0) {
                        userType = "nagat";
                    } else {
                        userType = "nagatTeam";
                    }
                }else {
                    userType = "speranza";
                }
                break;
            case 18:
                userType = "adel";
                break;
            default:
                userType = "unknown";
                break;
        }
        return userType;
    }

    public static boolean canEditProject(Context mContext, int projectOwnerId, int assignedToId) {
        return projectOwnerId == UserUtils.getUserId(mContext) ||
                assignedToId == UserUtils.getUserId(mContext);
    }
    public static boolean isAdmin(Context mContext, int role_id) {

        return UserUtils.getRoleId(mContext) == 1;
    }
}

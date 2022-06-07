package com.example.followup.utils;

public class NullString {
    public static String returnText(String text) {
        if (text.equals("null")) {
            return "N/A";
        } else {
            return text;
        }
    }
    public static String returnEmpty(String text) {
        if (text.equals("null")) {
            return "";
        } else {
            return text;
        }
    }
}

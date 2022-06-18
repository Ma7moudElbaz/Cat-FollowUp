package com.example.followup.utils;

public class StringCheck {
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

    public static String arabicToDecimal(String number) {
        char[] chars = new char[number.length()];
        for (int i = 0; i < number.length(); i++) {
            char ch = number.charAt(i);
            if (ch >= 0x0660 && ch <= 0x0669)
                ch -= 0x0660 - '0';
            else if (ch >= 0x06f0 && ch <= 0x06F9)
                ch -= 0x06f0 - '0';
            chars[i] = ch;
        }
        return new String(chars);
    }
}

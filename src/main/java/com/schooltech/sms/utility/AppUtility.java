package com.schooltech.sms.utility;

public class AppUtility {

    private static final String EMAIL_REGEX =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    public static String removeCountryCode(String phoneNumber) {

        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return phoneNumber;
        }

        // Remove + if present
        phoneNumber = phoneNumber.replace("+", "");

        // If starts with 91 and length > 10
        if (phoneNumber.startsWith("91") && phoneNumber.length() > 10) {
            return phoneNumber.substring(2);
        }

        return phoneNumber;
    }

    public static boolean isValidPhoneNumber(String phone) {

        if (phone == null) {
            return false;
        }

        return phone.matches("\\d{10}");
    }

    public static boolean isValidEmail(String email) {

        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        return email.matches(EMAIL_REGEX);
    }

    public static boolean isValidSessionFormat(String session) {
        if (session == null || !session.matches("\\d{4}-\\d{4}")) {
            return false; // Ensure the format is YYYY-YYYY
        }

        String[] years = session.split("-");
        int startYear = Integer.parseInt(years[0]);
        int endYear = Integer.parseInt(years[1]);

        return endYear - startYear == 1; // Valid if the difference between years is exactly 1
    }

}

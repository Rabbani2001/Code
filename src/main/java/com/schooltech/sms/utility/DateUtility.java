package com.schooltech.sms.utility;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class DateUtility {

    public static Timestamp getCurrentTimeStamp() {
        ZoneId indiaZone = ZoneId.of("Asia/Kolkata");
        LocalDateTime istLocalDateTime = ZonedDateTime.now(indiaZone).toLocalDateTime();
        return Timestamp.valueOf(istLocalDateTime);
    }

//    public static Timestamp getCurrentTimeStamp() {
//        Instant instant = Instant.now();
//        long timeStampMillis = instant.toEpochMilli();
//        return new Timestamp(timeStampMillis);
//    }

    public static LocalDate getCurrentDate() {
        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.now(ZoneId.of("Asia/Kolkata"));// output format YYYY-MM-DD ex: 2026-02-18
    }

    public static LocalDate convertStringToLocalDate(String dateStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            return LocalDate.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format: " + dateStr);
            return null;
        }
    }

    public static Date getDueDate(String customDateString) {
        return Date.valueOf(customDateString);
    }

//    public static Integer getLateDays(Date dueDate){
//        return Date.valueOf(customDateString);
//    }


    //TIME
    public static LocalTime getLocalTimeHHMM() {
        return LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
    }

    public static String getCurrentAcademicSession() {
        LocalDate today = LocalDate.now();

        int year = today.getYear();
        int month = today.getMonthValue();

        int sessionStartYear;
        int sessionEndYear;

        if (month >= 4) { // April to December
            sessionStartYear = year;
            sessionEndYear = year + 1;
        } else { // January to March
            sessionStartYear = year - 1;
            sessionEndYear = year;
        }

        return sessionStartYear + "-" + sessionEndYear;
    }


    public static String getAcademicSessionByDate(LocalDate date) {
        int year = date.getYear();
        int month = date.getMonthValue();

        int sessionStartYear;
        int sessionEndYear;

        if (month >= 4) { // April to December
            sessionStartYear = year;
            sessionEndYear = year + 1;
        } else { // January to March
            sessionStartYear = year - 1;
            sessionEndYear = year;
        }

        return sessionStartYear + "-" + sessionEndYear;
    }

    public static String getPreviousSession(String currentSession) {

        if (currentSession == null || !currentSession.matches("\\d{4}-\\d{4}")) {
            throw new IllegalArgumentException("Invalid session format. Expected format: YYYY-YYYY");
        }

        String[] years = currentSession.split("-");

        int startYear = Integer.parseInt(years[0]);
        int endYear = Integer.parseInt(years[1]);

        return (startYear - 1) + "-" + (endYear - 1);
    }


}

//package com.schooltech.sms.utility;
//
//import java.time.LocalDate;
//import java.util.Map;
//
//public class FeeUtility {
//    private static final Map<String, Integer> MONTH_MAP = Map.ofEntries(
//            Map.entry("january", 1), Map.entry("february", 2), Map.entry("march", 3),
//            Map.entry("april", 4), Map.entry("may", 5), Map.entry("june", 6),
//            Map.entry("july", 7), Map.entry("august", 8), Map.entry("september", 9),
//            Map.entry("october", 10), Map.entry("november", 11), Map.entry("december", 12)
//    );
//    private FeeUtility() {
//    }
//    /**
//     * Mirrors the frontend's session-year logic: Apr–Dec belong to the session's
//     * start year; Jan–Mar belong to the session's end year.
//     * session format expected: "2025-2026"
//     */
//    public static LocalDate resolveDueDate(String monthName, int lateFeeStartDay, String session) {
//        Integer monthIndex = MONTH_MAP.get(monthName.toLowerCase());
//        if (monthIndex == null) return null;
//
//        String[] parts = session.split("-");
//        if (parts.length != 2) return null;
//        int sessionStartYear = Integer.parseInt(parts[0].trim());
//        int sessionEndYear = Integer.parseInt(parts[1].trim());
//
//        int feeYear = (monthName.equalsIgnoreCase("january")
//                || monthName.equalsIgnoreCase("february")
//                || monthName.equalsIgnoreCase("march"))
//                ? sessionEndYear
//                : sessionStartYear;
//
//        int daysInMonth = LocalDate.of(feeYear, monthIndex, 1).lengthOfMonth();
//        int safeDay = Math.max(1, Math.min(lateFeeStartDay, daysInMonth));
//
//        return LocalDate.of(feeYear, monthIndex, safeDay);
//    }
//}

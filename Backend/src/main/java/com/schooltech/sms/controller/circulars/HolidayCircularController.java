package com.schooltech.sms.controller.circulars;


import com.schooltech.sms.entity.client.circulars.HolidayCircular;
import com.schooltech.sms.service.circular.HolidayCircularService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class HolidayCircularController {
    @Autowired
    private HolidayCircularService holidayCircularService;

    @GetMapping("/getHolidays")
    public ResponseEntity<List<HolidayCircular>> getHolidays() {
        return holidayCircularService.getHolidays();
    }

    @GetMapping("/getHolidaysBySession")
    public ResponseEntity<List<HolidayCircular>> getHolidaysBySession(@RequestParam String session) {
        return holidayCircularService.getHolidaysBySession(session);
    }

    @GetMapping("/getHolidayByDate")
    public ResponseEntity<HolidayCircular> getHolidayByDate(@RequestParam LocalDate date) {
        return holidayCircularService.getHolidayByDate(date);
    }

    @GetMapping("/getHolidayBetweenDates")
    public ResponseEntity<List<HolidayCircular>> getHolidayBetweenDates(@RequestParam("startDate") LocalDate startDate, @RequestParam("endDate") LocalDate endDate) {
        return holidayCircularService.getHolidayBetweenDates(startDate, endDate);
    }

    @PostMapping("/saveHolidays")
    public ResponseEntity<String> saveHolidays(@RequestBody List<HolidayCircular> holidaysList) {
        return holidayCircularService.saveHolidayCirculars(holidaysList);
    }

    @PutMapping("/updateHolidays")
    public ResponseEntity<String> updateHolidays(@RequestBody List<HolidayCircular> holidaysList) {
        return holidayCircularService.updateHolidayCirculars(holidaysList);
    }

    @DeleteMapping("/deleteHolidays/{holidayDate}")
    public ResponseEntity<String> deleteHolidays(@PathVariable LocalDate holidayDate) {
        return holidayCircularService.deleteHolidays(holidayDate);
    }


}

package com.schooltech.sms.service.circular;


import com.schooltech.sms.dao.client.circulars.HolidayCircularRepository;
import com.schooltech.sms.entity.client.circulars.HolidayCircular;
import com.schooltech.sms.exception.CircularNotFoundException;
import com.schooltech.sms.utility.DateUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class HolidayCircularService {
    @Autowired
    private HolidayCircularRepository holidayCircularRepository;

    public ResponseEntity<List<HolidayCircular>> getHolidays() {
        List<HolidayCircular> holidayCircularList = holidayCircularRepository.findAll();      //List<Teacher> teachers = teacherRepository.findAll().stream().filter(Objects::nonNull).collect(Collectors.toList());
        return new ResponseEntity<>(holidayCircularList, HttpStatus.OK);
    }

    public ResponseEntity<List<HolidayCircular>> getHolidaysBySession(String session) {

        // session format expected: "2025-2026"
        String[] years = session.split("-");

        int startYear = Integer.parseInt(years[0]);
        int endYear = Integer.parseInt(years[1]);

        LocalDate startDate = LocalDate.of(startYear, 4, 1);  // 1 April
        LocalDate endDate = LocalDate.of(endYear, 3, 31);     // 31 March

        List<HolidayCircular> holidays =
                holidayCircularRepository.findByDateBetween(startDate, endDate);    //List<Teacher> teachers = teacherRepository.findAll().stream().filter(Objects::nonNull).collect(Collectors.toList());
        return new ResponseEntity<>(holidays, HttpStatus.OK);
    }

    public ResponseEntity<HolidayCircular> getHolidayByDate(LocalDate date) {
        Optional<HolidayCircular> holidayCircularOptional = holidayCircularRepository.findByDate(date);
        HolidayCircular holidayCircular = holidayCircularOptional.orElseThrow(() -> new CircularNotFoundException("Holiday Circular Not Found"));
        return new ResponseEntity<>(holidayCircular, HttpStatus.OK);
    }

    public ResponseEntity<List<HolidayCircular>> getHolidayBetweenDates(LocalDate startDate, LocalDate endDate) {
        List<HolidayCircular> holidayCirculars = holidayCircularRepository.findAllHolidaysBetweenDates(startDate, endDate);
        return new ResponseEntity<>(holidayCirculars, HttpStatus.OK);
    }

    public ResponseEntity<String> saveHolidayCirculars(List<HolidayCircular> holidaysList) {
        holidaysList.stream().forEach(holidayCircular -> {
            Optional<HolidayCircular> holidayCircularOptional =
                    holidayCircularRepository.findByDate(holidayCircular.getDate());
            holidayCircularOptional.ifPresentOrElse(existingCircular -> {
                throw new RuntimeException("Holiday Circular: " + holidayCircular.getDate() + " date already present. Aborting Save");
            }, () -> {
                holidayCircular.setTimestamp(DateUtility.getCurrentTimeStamp());
                holidayCircularRepository.save(holidayCircular);
            });
        });
        return ResponseEntity.ok("Holiday Circular Data Saved");
    }

    public ResponseEntity<String> updateHolidayCirculars(List<HolidayCircular> holidaysList) {
        holidaysList.forEach(holidayCircular -> {
            Optional<HolidayCircular> holidayCircularOptional =
                    holidayCircularRepository.findByDate(holidayCircular.getDate());
            holidayCircularOptional.ifPresentOrElse(existingCircular -> {
                updateNonNullFields(existingCircular, holidayCircular);
                existingCircular.setTimestamp(DateUtility.getCurrentTimeStamp());
                holidayCircularRepository.save(existingCircular);
            }, () -> {
                throw new CircularNotFoundException("Holiday Circular: " + holidayCircular.getDate() + " not present. Aborting Update");
            });
        });
        return ResponseEntity.ok("Holiday Circular Data Updated");
    }

    private void updateNonNullFields(HolidayCircular existingCircular, HolidayCircular newCircular) {
        if (newCircular.getHolidayName() != null) existingCircular.setHolidayName(newCircular.getHolidayName());
    }

    public ResponseEntity<String> deleteHolidays(LocalDate holidayDate) {
        if (!holidayCircularRepository.existsByDate(holidayDate)) {
            throw new CircularNotFoundException("Holiday Circular Not Available for Date: " + holidayDate);
        }
        holidayCircularRepository.deleteByDate(holidayDate);
        return new ResponseEntity<>("Holiday Circular has been deleted for Date: " + holidayDate, HttpStatus.OK);
    }
}

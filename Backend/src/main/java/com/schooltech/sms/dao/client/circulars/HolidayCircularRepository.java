package com.schooltech.sms.dao.client.circulars;


import com.schooltech.sms.entity.client.circulars.HolidayCircular;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HolidayCircularRepository extends JpaRepository<HolidayCircular, Long> {

    Optional<HolidayCircular> findByDate(LocalDate date);

    List<HolidayCircular> findByDateBetween(LocalDate startDate, LocalDate endDate);

    //Optional<HolidayCircular> findByHolidayName(String holidayName);

    @Query("SELECT h.holidayName FROM HolidayCircular h")
    List<String> findAllHolidayNames();


    @Transactional
    void deleteByDate(LocalDate className);

    boolean existsByDate(LocalDate className);


    @Query("SELECT e FROM HolidayCircular e WHERE e.date BETWEEN :startDate AND :endDate")
    List<HolidayCircular> findAllHolidaysBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}

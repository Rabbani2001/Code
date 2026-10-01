package com.schooltech.sms.dao.client.attendence;


import com.schooltech.sms.entity.client.attendence.StaffAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StaffAttendanceRepository extends JpaRepository<StaffAttendance, Long> {

    @Query("SELECT ta FROM StaffAttendance ta WHERE ta.username = :staffId")
    StaffAttendance findStaffAttendenceByStaffId(@Param("staffId") String staffId);


    @Query("SELECT ta FROM StaffAttendance ta WHERE ta.username = :staffId AND ta.date = :date")
    StaffAttendance findStaffAttendenceByStaffIdAndDate(@Param("staffId") String staffId, @Param("date") LocalDate date);

    @Query("SELECT ta FROM StaffAttendance ta WHERE ta.username = :staffId AND ta.date = :date")
    Optional<StaffAttendance> findStaffAttendenceByStaffIdAndDate2(@Param("staffId") String staffId, @Param("date") LocalDate date);


    @Query("SELECT e FROM StaffAttendance e WHERE e.username= :staffId AND e.date BETWEEN :startDate AND :endDate")
    List<StaffAttendance> findAllAttendenceBetweenDates(@Param("staffId") String staffId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);


    List<StaffAttendance> findByDate(LocalDate date);

    Optional<StaffAttendance> findByUsernameAndDate(String username, LocalDate date);
}

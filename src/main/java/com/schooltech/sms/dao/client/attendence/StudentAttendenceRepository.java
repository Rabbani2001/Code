package com.schooltech.sms.dao.client.attendence;


import com.schooltech.sms.entity.client.attendence.StudentAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface StudentAttendenceRepository extends JpaRepository<StudentAttendance, Long> {
    @Query("SELECT sa FROM StudentAttendance sa WHERE sa.username = :studentId")
    StudentAttendance findStudentAttendenceByStudentId(@Param("studentId") String studentId);

    @Query("SELECT sa FROM StudentAttendance sa WHERE sa.username = :studentId AND sa.date = :date")
    StudentAttendance findStudentAttendenceByStudentIdAndDate(@Param("studentId") String studentId, @Param("date") LocalDate date);


    @Query("SELECT e FROM StudentAttendance e WHERE e.username= :studentId AND e.date BETWEEN :startDate AND :endDate")
    List<StudentAttendance> findAllAttendenceBetweenDates(@Param("studentId") String studentId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    //List<StudentAttendance> findByDate(LocalDate date);

    @Query("SELECT e FROM StudentAttendance e WHERE e.className= :className AND e.date= :date")
    List<StudentAttendance> findAllAttendenceByDateAndClass(@Param("className") String className, @Param("date") LocalDate date);

    List<StudentAttendance> findByDate(LocalDate date);

    Optional<StudentAttendance> findByUsernameAndDate(String username, LocalDate date);

    @Query("SELECT COUNT(sa) FROM StudentAttendance sa " +
            "WHERE sa.status = :status AND sa.date = :date AND sa.checkInTime < :cutoffTime")
    long countByStatusAndDateAndCheckInTimeBefore(
            @Param("status") StudentAttendance.Status status,
            @Param("date") LocalDate date,
            @Param("cutoffTime") LocalTime cutoffTime);

}

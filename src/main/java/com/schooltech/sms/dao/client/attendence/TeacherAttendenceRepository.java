package com.schooltech.sms.dao.client.attendence;


import com.schooltech.sms.entity.client.attendence.TeacherAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TeacherAttendenceRepository extends JpaRepository<TeacherAttendance, Long> {

    @Query("SELECT ta FROM TeacherAttendance ta WHERE ta.username = :teacherId")
    TeacherAttendance findTeacherAttendenceByTeacherId(@Param("teacherId") String teacherId);


    @Query("SELECT ta FROM TeacherAttendance ta WHERE ta.username = :teacherId AND ta.date = :date")
    TeacherAttendance findTeacherAttendenceByTeacherIdAndDate(@Param("teacherId") String teacherId, @Param("date") LocalDate date);

    @Query("SELECT ta FROM TeacherAttendance ta WHERE ta.username = :teacherId AND ta.date = :date")
    Optional<TeacherAttendance> findTeacherAttendenceByTeacherIdAndDate2(@Param("teacherId") String teacherId, @Param("date") LocalDate date);


    @Query("SELECT e FROM TeacherAttendance e WHERE e.username= :teacherId AND e.date BETWEEN :startDate AND :endDate")
    List<TeacherAttendance> findAllAttendenceBetweenDates(@Param("teacherId") String teacherId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);


    List<TeacherAttendance> findByDate(LocalDate date);

    Optional<TeacherAttendance> findByUsernameAndDate(String username, LocalDate date);
}

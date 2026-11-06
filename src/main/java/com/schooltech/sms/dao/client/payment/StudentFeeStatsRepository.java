package com.schooltech.sms.dao.client.payment;


import com.schooltech.sms.entity.client.payment.StudentFeeStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentFeeStatsRepository extends JpaRepository<StudentFeeStats, Long> {

    Optional<StudentFeeStats> findBySession(String session);


    //    @Query(value = "SELECT * FROM Student_Attendance WHERE Student_Attendance.student_id = ?1" , nativeQuery=true)
//    public StudentAttendance findStudentAttendenceByStudentId(String theId);
//
//
//    @Query(value = "SELECT * FROM Student_Attendance WHERE Student_Attendance.student_id = ?1 AND Student_Attendance.date = ?2 " , nativeQuery=true)
//    public StudentAttendance findStudentAttendenceByStudentIdAndDate(String studentId, Date date);
//
//    @Query("SELECT e FROM StudentAttendance e WHERE e.studentId= :studentId AND e.date BETWEEN :startDate AND :endDate")
//    List<StudentAttendance> findAllAttendenceBetweenDates(@Param("studentId") String studentId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);
//
//    List<StudentAttendance> findByDate(Date date);
//
//    @Query("SELECT e FROM StudentFee e WHERE e.username= :studentId AND e.session= :session")
//    StudentFee findByStudentIdAndSession(@Param("studentId") String studentId, @Param("session") String session);


}

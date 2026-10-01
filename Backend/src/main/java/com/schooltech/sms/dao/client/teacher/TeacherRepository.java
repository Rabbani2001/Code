package com.schooltech.sms.dao.client.teacher;


import com.schooltech.sms.entity.client.teacher.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    List<Teacher> findAll();

    @Query("SELECT COUNT(t) FROM Teacher t")
    long countTeachers();

    Optional<Teacher> findByUsername(String teacherId);

    //Optional<Teacher> findByEmail(String email);

    // Optional<Teacher> findByPhoneNo(String phoneNo);

    List<Teacher> findAllByOrderByFullNameAsc();

    @Query("SELECT t, ta " +
            "FROM Teacher t LEFT JOIN TeacherAttendance ta " +
            "ON t.username = ta.username " +
            "AND ta.date = :attendanceDate " +
            "ORDER BY t.fullName ASC")
    List<Object[]> findTeachersWithAttendanceStatus(LocalDate attendanceDate);

    @Query(
            value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END " +
                    "FROM teacher WHERE JSON_CONTAINS(class_names, :classNameJson)",
            nativeQuery = true
    )
    boolean existsByClassName(@Param("classNameJson") String classNameJson);

    boolean existsByEmployeeIdIgnoreCase(String employeeId);

    @Query(value = "SELECT COUNT(*) FROM teacher WHERE LOWER(employee_id) = LOWER(:employeeId)", nativeQuery = true)
    int existsByEmployeeIdIgnoreCaseNative(@Param("employeeId") String employeeId);

    boolean existsByUsername(String username);

    boolean existsByAadharNo(String aadharNo);

    boolean existsByPanNo(String panNo);

    @Modifying
    @Transactional
    @Query("UPDATE Teacher s SET s.isActive = false WHERE s.username = :username")
    void deActivateByUsername(String username);

    @Modifying
    @Transactional
    @Query(value = "UPDATE teacher SET is_active = true WHERE username = :username", nativeQuery = true)
    void activateByUsername(String username);


    @Modifying
    @Transactional
    @Query(value = "DELETE FROM teacher WHERE username = :username", nativeQuery = true)
    void deleteByUsername(String username);

    @Query(value = "SELECT * FROM teacher WHERE is_active=false", nativeQuery = true)
    List<Teacher> getDeActivateTeachers();

    @Query(value = "SELECT * FROM teacher WHERE is_active=false AND username = :username", nativeQuery = true)
    Optional<Teacher> findByUsernameDeactivatedStatus(@Param("username") String username);


}

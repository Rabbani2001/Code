package com.schooltech.sms.dao.client.staff;


import com.schooltech.sms.entity.client.staff.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, Long> {
    List<Staff> findAll();

//    List<Staff> findByRole(String role);

    @Query("SELECT s FROM Staff s WHERE s.role = :role AND s.username <> 'superadmin'")
    List<Staff> findByRoleExcludingSuperadmin(@Param("role") String role);

    @Query("SELECT s FROM Staff s WHERE s.username <> 'superadmin'")
    List<Staff> findByUsernameNotSuperadmin();

    Optional<Staff> findByUsername(String username);

    @Query(
            value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END " +
                    "FROM staff WHERE JSON_CONTAINS(class_names, :classNameJson)",
            nativeQuery = true
    )
    boolean existsByClassName(@Param("classNameJson") String classNameJson);

//    Optional<Staff> findByEmail(String email);
//
//    Optional<Staff> findByPhoneNo(String phoneNo);

    boolean existsByEmployeeIdIgnoreCase(String employeeId);

    @Query(value = "SELECT COUNT(*) FROM staff WHERE LOWER(employee_id) = LOWER(:employeeId)", nativeQuery = true)
    int existsByEmployeeIdIgnoreCaseNative(@Param("employeeId") String employeeId);

    boolean existsByUsername(String username);

    boolean existsByAadharNo(String aadharNo);

    boolean existsByPanNo(String panNo);

    @Modifying
    @Transactional
    @Query("UPDATE Staff s SET s.isActive = false WHERE s.username = :username")
    void deActivateByUsername(String username);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM staff WHERE username = :username", nativeQuery = true)
    void deleteByUsername(@Param("username") String username);

    @Modifying
    @Transactional
    @Query(value = "UPDATE staff SET is_active = true WHERE username = :username", nativeQuery = true)
    void activateByUsername(String username);

    @Query(value = "SELECT * FROM staff WHERE is_active=false AND role = :role", nativeQuery = true)
    List<Staff> getDeActivateStaffs(String role);

    @Query(value = "SELECT * FROM staff WHERE is_active=false AND username = :username", nativeQuery = true)
    Optional<Staff> findByUsernameDeactivatedStatus(@Param("username") String username);

//    @Query("SELECT t, ta " +
//            "FROM Staff t LEFT JOIN StaffAttendance ta " +
//            "ON t.username = ta.username " +
//            "AND ta.date = :attendanceDate " +
//            "ORDER BY t.fullName ASC")
//    List<Object[]> findStaffsWithAttendanceStatus(LocalDate attendanceDate);

    @Query("SELECT t, ta " +
            "FROM Staff t LEFT JOIN StaffAttendance ta " +
            "ON t.username = ta.username " +
            "AND ta.date = :attendanceDate " +
            "WHERE t.role <> 'superadmin' " +
            "ORDER BY t.fullName ASC")
    List<Object[]> findStaffsWithAttendanceStatusExcludingSuperadmin(LocalDate attendanceDate);

}

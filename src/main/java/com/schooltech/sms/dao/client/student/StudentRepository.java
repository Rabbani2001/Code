package com.schooltech.sms.dao.client.student;


import com.schooltech.sms.entity.client.student.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByUsername(String username);

    @Query(value = "SELECT * FROM student WHERE is_active=false AND username = :username", nativeQuery = true)
    Optional<Student> findByUsernameDeactivatedStatus(String username);

    @Query(value = "SELECT * FROM student WHERE username = :username", nativeQuery = true)
    Optional<Student> findStudentByUsername(String username);


    Optional<Student> findByAdmissionNo(String admissionNo);

    Optional<Student> findByAadharNo(String aadharNo);

    Optional<Student> findByApaarNo(String apaarNo);

    boolean existsByAadharNo(String aadharNo);

//    Optional<List<Student>> findByPhoneNo(String phoneNo);


    //public Student findByAdmissionNo(String admissionNo);

    //public List<Student> findByClassName(String className);
    public List<Student> findByClassNameOrderByFullNameAsc(String className);

    @Query("""
                SELECT s
                FROM Student s
                WHERE s.className = :className AND s.session = :session
                ORDER BY s.fullName ASC
            """)
    List<Student> findByClassNameAndSessionOrderByFullNameAsc(String className, String session);

    public List<Student> findByBusRouteOrderByFullNameAsc(String busRoute);

    public List<Student> findAllByOrderByFullNameAsc();

    @Query("SELECT s, sa " +
            "FROM Student s LEFT JOIN StudentAttendance sa " +
            "ON s.username = sa.username " +
            "AND sa.date = :attendanceDate " +
            "WHERE s.className = :className AND s.session = :session " +
            "ORDER BY s.rollNo ASC")
    public List<Object[]> findStudentWithAttendenceStatus(String className, LocalDate attendanceDate, String session);

    //    @Query("SELECT s, se " +
//            "FROM Student s LEFT JOIN StudentExams se " +
//            "ON s.username = se.username " +
//            "WHERE s.className = :className " +
//            "ORDER BY s.rollNo ASC")
    @Query("""
                SELECT s, se
                FROM Student s
                LEFT JOIN StudentExams se
                    ON s.username = se.username
                    AND se.session = :session
                WHERE s.className = :className AND s.session = :session
                ORDER BY s.rollNo ASC
            """)
    List<Object[]> findStudentsWithExamSchedule(String className, String session);

    @Query("""
                SELECT u, s, sf
                FROM User u
                JOIN Student s
                    ON u.username = s.parentUsername
                LEFT JOIN StudentFee sf
                    ON s.username = sf.username
                    AND sf.session = :session
                WHERE s.className = :className AND s.session = :session
                ORDER BY s.fullName ASC
            """)
    List<Object[]> findStudentsWithFeeDetails(
            @Param("className") String className,
            @Param("session") String session
    );

    @Query("""
                SELECT u, s, sf
                FROM User u
                JOIN Student s
                    ON u.username = s.parentUsername
                LEFT JOIN StudentFee sf
                    ON s.username = sf.username
                    AND sf.session = :session
                WHERE u.phoneNo = :phoneNo
                ORDER BY s.fullName ASC
            """)
    List<Object[]> findStudentsByPhoneNoWithFeeDetails(String phoneNo, String session);

    @Query("""
             SELECT DISTINCT u, p, s
             FROM User u
             JOIN Parent p
                 ON u.username = p.username
             LEFT JOIN Student s
                 ON p.username = s.parentUsername
             WHERE u.phoneNo = :phoneNo
             ORDER BY s.fullName ASC
            """)
    List<Object[]> findStudentsByPhoneNo(
            String phoneNo
    );

    @Query("""
                SELECT u, s, sf
                FROM User u
                JOIN Student s
                    ON u.username = s.parentUsername
                LEFT JOIN StudentFee sf
                    ON s.username = sf.username
                    AND sf.session = :session
                WHERE s.admissionNo = :admissionNo
                ORDER BY s.fullName ASC
            """)
    List<Object[]> findStudentsByAdmissionNoWithFeeDetails(String admissionNo, String session);

    @Query("""
                SELECT u, s, sf
                FROM User u
                JOIN Student s
                    ON u.username = s.parentUsername
                LEFT JOIN StudentBusFee sf
                    ON s.username = sf.username
                    AND sf.session = :session
                WHERE s.className = :className AND s.session = :session
                ORDER BY s.fullName ASC
            """)
    List<Object[]> findStudentsWithBusFeeDetails(
            @Param("className") String className,
            @Param("session") String session
    );

    @Query("""
                SELECT u, s, sf
                FROM User u
                JOIN Student s
                    ON u.username = s.parentUsername
                LEFT JOIN StudentBusFee sf
                    ON s.username = sf.username
                    AND sf.session = :session
                WHERE u.phoneNo = :phoneNo
                ORDER BY s.fullName ASC
            """)
    List<Object[]> findStudentsByPhoneNoWithBusFeeDetails(String phoneNo, String session);

    @Query("""
                SELECT u, s, sf
                FROM User u
                JOIN Student s
                    ON u.username = s.parentUsername
                LEFT JOIN StudentBusFee sf
                    ON s.username = sf.username
                    AND sf.session = :session
                WHERE s.admissionNo = :admissionNo
                ORDER BY s.fullName ASC
            """)
    List<Object[]> findStudentsByAdmissionNoWithBusFeeDetails(String admissionNo, String session);


//    @Query("SELECT s FROM Student s WHERE LOWER(s.fullName) LIKE LOWER(CONCAT(:searchTerm, '%'))")
//    List<Student> findStudentsByFullNameStartingWith(@Param("searchTerm") String searchTerm);

    List<Student> findByFullNameContainingIgnoreCase(String searchTerm);


    @Query("SELECT s FROM Student s WHERE LOWER(s.admissionNo) LIKE LOWER(CONCAT(:searchTerm, '%'))")
    List<Student> findStudentsByAdmissionNoStartingWith(@Param("searchTerm") String searchTerm);

//    @Query("SELECT s FROM Student s WHERE LOWER(s.phoneNo) LIKE LOWER(CONCAT(:searchTerm, '%'))")
//    List<Student> findStudentsByPhoneNoStartingWith(@Param("searchTerm") String searchTerm);

    List<Student> findByParentUsername(String username);

    @Query("SELECT s FROM Student s WHERE LOWER(s.fatherName) LIKE LOWER(CONCAT(:searchTerm, '%'))")
    List<Student> findStudentsByFatherNameStartingWith(@Param("searchTerm") String searchTerm);

    @Query("SELECT s FROM Student s WHERE LOWER(s.username) LIKE LOWER(CONCAT(:searchTerm, '%'))")
    List<Student> findStudentsByStudentIdStartingWith(@Param("searchTerm") String searchTerm);


    //public Optional<User> findByUsername(String username);


    @Modifying
    @Transactional
    @Query("UPDATE Student s SET s.busRoute = :busRoute WHERE s.username = :username")
    int updateStudentBusRoute(@Param("username") String username,
                              @Param("busRoute") String busRoute);


    //boolean existsByClassName(String className);
    boolean existsByClassNameAndSession(String className, String session);


    //Here native query needed as we have a filter check on Entity
    @Query(value = "SELECT * FROM student WHERE is_active=false", nativeQuery = true)
    List<Student> getDeActivatedStudents();

    boolean existsByUsername(String username);

    @Modifying
    @Transactional
    @Query("UPDATE Student s SET s.isActive = false WHERE s.username = :username")
    void deActivateByUsername(String username);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM student WHERE username = :username", nativeQuery = true)
    void deleteByUsername(@Param("username") String username);

    boolean existsByParentUsername(String parentUsername);

    @Modifying
    @Transactional
    @Query(value = "UPDATE student SET is_active = true WHERE username = :username", nativeQuery = true)
    void activateByUsername(String username);

    @Modifying
    @Transactional
    @Query("UPDATE Student s SET s.rollNo = null WHERE s.username = :username")
    void clearRollNoByUsername(@Param("username") String username);


    //The native COUNT(*) query has no such filter and counts every student row in the table belonging to that parent.
    @Query(value = "SELECT COUNT(*) FROM student WHERE parent_username = :parentUsername", nativeQuery = true)
    int countStudentsByParentUsernameNative(@Param("parentUsername") String parentUsername);
}
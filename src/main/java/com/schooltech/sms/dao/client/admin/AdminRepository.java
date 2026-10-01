//package com.schooltech.sms.dao.client.admin;
//
//
//import com.schooltech.sms.entity.client.admin.Admin;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Optional;
//
//public interface AdminRepository extends JpaRepository<Admin, Long> {
//    List<Admin> findAll();
//
//    Optional<Admin> findByUsername(String username);
//
//    @Query(
//            value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END " +
//                    "FROM admin WHERE JSON_CONTAINS(class_names, :classNameJson)",
//            nativeQuery = true
//    )
//    boolean existsByClassName(@Param("classNameJson") String classNameJson);
//
////    Optional<Admin> findByEmail(String email);
////
////    Optional<Admin> findByPhoneNo(String phoneNo);
//
//    boolean existsByEmployeeIdIgnoreCase(String employeeId);
//
//    boolean existsByUsername(String username);
//
//    @Modifying
//    @Transactional
//    @Query("UPDATE Admin s SET s.isActive = false WHERE s.username = :username")
//    void deActivateByUsername(String username);
//
//    @Modifying
//    @Transactional
//    @Query(value = "UPDATE admin SET is_active = true WHERE username = :username", nativeQuery = true)
//    void activateByUsername(String username);
//
//
//    @Query("SELECT a FROM Admin a WHERE a.username IN (SELECT u.username FROM User u WHERE u.role = 'admin')")
//    List<Admin> findAllAdmins();
//
//    @Query("SELECT a FROM Admin a WHERE a.username IN (SELECT u.username FROM User u WHERE u.role = 'superadmin') AND a.username <> 'superadmin'")
//    List<Admin> findAllSuperAdminsExcludingDefault();
//
//    @Query(value = "SELECT * FROM admin WHERE is_active=false", nativeQuery = true)
//    List<Admin> getDeActivatedAdmins();
//
//    @Query(value = "SELECT * FROM admin a WHERE a.username IN (SELECT u.username FROM user u WHERE u.role = 'superadmin') AND a.username <> 'superadmin'", nativeQuery = true)
//    List<Admin> findAllDeActivatedSuperAdminsExcludingDefault();
//}

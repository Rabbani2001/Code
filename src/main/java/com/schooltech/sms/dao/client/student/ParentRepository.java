package com.schooltech.sms.dao.client.student;


import com.schooltech.sms.entity.client.student.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ParentRepository extends JpaRepository<Parent, Long> {

    Optional<Parent> findByUsername(String username);

    @Query("SELECT COUNT(p) FROM Parent p")
    long countParents();

    @Query(value = "SELECT * FROM parent WHERE is_active=false", nativeQuery = true)
    List<Parent> getDeActivatedParents();

    //  Optional<Parent> findByEmail(String email);

    //Optional<Parent> findByPhoneNo(String phoneNo);

    boolean existsByUsername(String username);

    boolean existsByAadharNo(String aadharNo);

    boolean existsByPanNo(String panNo);

    //find single deactivated parent by username
    @Query(value = "SELECT * FROM parent WHERE is_active=false AND username = :username", nativeQuery = true)
    Optional<Parent> findByUsernameDeactivatedStatus(@Param("username") String username);


    @Modifying
    @Transactional
    @Query("UPDATE Parent s SET s.isActive = false WHERE s.username = :username")
    void deActivateByUsername(String username);

    @Modifying
    @Transactional
    @Query(value = "UPDATE parent SET is_active = true WHERE username = :username", nativeQuery = true)
    void activateByUsername(String username);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM parent WHERE username = :username", nativeQuery = true)
    void deleteByUsername(String username);

    @Query("SELECT p FROM Parent p WHERE LOWER(p.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Parent> findByFullNameContainingIgnoreCase(@Param("name") String name);

    @Query("SELECT p FROM Parent p WHERE p.username NOT IN (SELECT DISTINCT s.parentUsername FROM Student s)")
    List<Parent> findAbandonedParents();
}

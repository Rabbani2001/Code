package com.schooltech.sms.dao.client.user;

import com.schooltech.sms.entity.client.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    @Query("SELECT e FROM User e WHERE e.username= :usernameOrEmailOrPhoneNo OR e.email=: usernameOrEmailOrPhoneNo OR e.phoneNo=: usernameOrEmailOrPhoneNo")
    public Optional<User> findByUsernameOrEmailOrPhoneNo(String usernameOrEmailOrPhoneNo);

    public User findByUsername(String username);

    public User findByPhoneNo(String phoneNo);

    //public Optional<User> findByUserId(String id);

    boolean existsByUsername(String username);

    @Modifying
    @Transactional
    @Query("UPDATE User s SET s.isActive = false WHERE s.username = :username")
    void deActivateByUsername(String username);

    @Modifying
    @Transactional
    @Query(value = "UPDATE user SET is_active = true WHERE username = :username", nativeQuery = true)
    void activateByUsername(String username);

    @Query(value = "SELECT * FROM user WHERE is_active=false", nativeQuery = true)
    List<User> getDeActivatedUsers();

    @Query(value = "SELECT * FROM user WHERE is_active=false AND username = :username", nativeQuery = true)
    User getDeActivatedUser(String username);

    @Modifying
    @Transactional
    @Query("DELETE FROM User u WHERE u.username = :username")
    void deleteByUsername(String username);

}

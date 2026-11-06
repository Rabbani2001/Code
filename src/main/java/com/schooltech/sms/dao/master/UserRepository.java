//package com.resourcetech1.sms.dao.master;
//
//import com.resourcetech1.sms.entity.master.User;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//
//import java.util.Optional;
//
//public interface UserRepository extends JpaRepository<User,String> {
//
//    @Query("SELECT e FROM User e WHERE e.username= :usernameOrEmail OR e.email=: usernameOrEmail OR e.phoneNo=: usernameOrEmail")
//    public Optional<User> findByUsernameOrEmail(String usernameOrEmail);
//
//    //public Optional<User> findByUsername(String username);
//
//    //public Optional<User> findByUserId(String id);
//
//}

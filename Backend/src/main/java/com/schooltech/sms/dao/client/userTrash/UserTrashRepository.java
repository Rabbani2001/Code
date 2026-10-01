package com.schooltech.sms.dao.client.userTrash;

import com.schooltech.sms.entity.client.user.UserTrash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserTrashRepository extends JpaRepository<UserTrash, Long> {
    Optional<UserTrash> findByUsername(String username);

    @Query("SELECT ut FROM UserTrash ut WHERE ut.username = :username")
    UserTrash getDeActivatedUser(String username);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM UserTrash ut WHERE ut.username = :username")
    void deleteByUsername(String username);

}
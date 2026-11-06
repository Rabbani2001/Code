package com.schooltech.sms.dao.client.student;


import com.schooltech.sms.entity.client.student.StudentExams;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface StudentExamsRepository extends JpaRepository<StudentExams, Long> {
    Optional<StudentExams> findByUsernameAndSession(String username, String session);

    boolean existsByUsername(String username);

    @Transactional
    void deleteByUsername(String username);

    @Query("SELECT e FROM StudentExams e WHERE e.className = :className AND e.session = :session")
    List<StudentExams> findAllByClassNameAndSession(@Param("className") String className, @Param("session") String session);

}

package com.schooltech.sms.dao.client.student;


import com.schooltech.sms.entity.client.student.Student;
import com.schooltech.sms.entity.client.student.StudentOtherInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StudentOtherInfoRepository extends JpaRepository<StudentOtherInfo, Long> {

    Optional<StudentOtherInfo> findByUsername(String username);

    @Query(value = "SELECT * FROM student_other_info WHERE username = :username", nativeQuery = true)
    Optional<StudentOtherInfo> findStudentOtherInfoByUsername(String username);


    List<StudentOtherInfo> findByUsernameIn(List<String> usernames);
}

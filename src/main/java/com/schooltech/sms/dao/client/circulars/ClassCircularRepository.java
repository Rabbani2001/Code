package com.schooltech.sms.dao.client.circulars;


import com.schooltech.sms.entity.client.circulars.ClassCircular;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ClassCircularRepository extends JpaRepository<ClassCircular, Long> {

    Optional<ClassCircular> findByClassName(String className);

    List<ClassCircular> findBySession(String session);

    @Query("SELECT e FROM ClassCircular e WHERE e.className= :className AND e.session= :session")
    Optional<ClassCircular> findByClassNameAndSession(@Param("className") String className, @Param("session") String session);

    @Query("""
             SELECT e
             FROM ClassCircular e
             WHERE e.className IN :classNames
             AND e.session = :session
            """)
    List<ClassCircular> findByClassNameInAndSession(
            @Param("classNames") List<String> classNames,
            @Param("session") String session);


    @Transactional
    void deleteByClassName(String className);

    boolean existsByClassName(String className);

    @Transactional
    void deleteByClassNameAndSession(String className, String session);

    boolean existsByClassNameAndSession(String className, String session);


    @Query("SELECT c.className FROM ClassCircular c")
    List<String> findAllClassNameFromClassCircular();

    @Query("SELECT c.className FROM ClassCircular c WHERE c.session= :session")
    List<String> findAllClassNameBySessionFromClassCircular(@Param("session") String session);

}

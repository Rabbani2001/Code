package com.schooltech.sms.dao.client.attendence;


import com.schooltech.sms.entity.client.attendence.FaceEmbedding;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FaceEmbeddingRepository extends JpaRepository<FaceEmbedding, Long> {

    Optional<FaceEmbedding> findByUsername(String username);
}

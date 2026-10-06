package com.schooltech.sms.entity.client.attendence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;


@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "face_embeddings")
public class FaceEmbedding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(name = "username", nullable = false, unique = true)//,unique = true)
    private String username;

    @Column(columnDefinition = "json", nullable = false)
    private String embedding;

    @Column(name = "timestamp", nullable = false)
    private Timestamp timestamp;


}
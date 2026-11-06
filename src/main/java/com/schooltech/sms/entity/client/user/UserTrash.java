package com.schooltech.sms.entity.client.user;


import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@ToString
@Entity
@Table(name = "user_trash")
public class UserTrash {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(name = "username", unique = true, nullable = false)  //username will be treated as User_id
    private String username;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "phone_no", unique = true, nullable = false)
    private String phoneNo;

    @Column(name = "alt_phone_no")
    private String altPhoneNo;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "timestamp", nullable = false)
    private Timestamp timestamp;
}

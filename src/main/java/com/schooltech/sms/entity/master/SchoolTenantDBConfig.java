package com.schooltech.sms.entity.master;


import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@ToString
@Entity
@Table(name = "stclient_db_config")
public class SchoolTenantDBConfig {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(name = "tenant_id", nullable = false, unique = true)  //username will be trated as User_id
    private String tenantId;

    @Column(name = "db_url", nullable = false)  //username will be trated as User_id
    private String dbUrl;

    @Column(name = "db_username")
    private String dbUsername;

    @Column(name = "db_password", nullable = false)
    private String dbPassword;

    @Column(name = "db_driver")
    private String dbDriver;

    @Column(name = "timestamp", nullable = false)
    private Timestamp timestamp;

}

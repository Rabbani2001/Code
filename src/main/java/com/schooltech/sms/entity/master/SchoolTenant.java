package com.schooltech.sms.entity.master;


import com.schooltech.sms.entity.BaseEntity;
import com.schooltech.sms.utility.converters.STConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.sql.Timestamp;
import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@ToString
@Entity
@Table(name = "school_tenant")
@Where(clause = "is_active=true")
public class SchoolTenant extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(name = "tenant_code", nullable = false, unique = true)  //username will be trated as User_id
    private String tenantCode;

    @Column(name = "tenant_name", nullable = false)  //username will be trated as User_id
    private String tenantName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_no", nullable = false, unique = true)
    private String phoneNo;

    @Column(name = "alt_phone_no")
    private String altPhoneNo;

    @Column(name = "tenant_id", unique = true)
    private String tenantId;

    @Column(name = "tenant_info", columnDefinition = "JSON")
    @Convert(converter = STConverter.MapOfStringKeyStringValueConverter.class)
    private Map<String, String> tenantInfo;

    @Column(name = "attendance_control", columnDefinition = "JSON")
    @Convert(converter = STConverter.MapOfStringKeyStringValueConverter.class)
    private Map<String, String> attendanceControl;

    @Column(name = "timestamp", nullable = false)
    private Timestamp timestamp;

}

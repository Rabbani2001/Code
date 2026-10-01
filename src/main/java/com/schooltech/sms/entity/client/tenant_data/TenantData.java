package com.schooltech.sms.entity.client.tenant_data;

import com.schooltech.sms.utility.converters.STConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Map;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "tenant_data")
public class TenantData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sms_quota", nullable = false, columnDefinition = "JSON")
    @Convert(converter = STConverter.MapOfStringKeyIntegerValueConverter.class)
    private Map<String, Integer> smsQuota;

    @Column(name = "attendance_control", columnDefinition = "JSON")
    @Convert(converter = STConverter.MapOfStringKeyStringValueConverter.class)
    private Map<String, String> attendanceControl;

    @Column(name = "tenant_info", columnDefinition = "JSON")
    @Convert(converter = STConverter.MapOfStringKeyStringValueConverter.class)
    private Map<String, String> tenantInfo;

    @Column(name = "timestamp", nullable = false)
    private Timestamp timestamp;
}
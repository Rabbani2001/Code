package com.schooltech.sms.entity.client.circulars;


import com.schooltech.sms.utility.converters.STConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "role_circular", uniqueConstraints = @UniqueConstraint(columnNames = {"role", "session"}))
public class RoleCircular {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "session", nullable = false) //example 2024-2025
    private String session;
    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "paid_leaves")
    private Integer paidLeaves;

    @Column(name = "sick_leaves")
    private Integer sickLeaves;

    @Column(name = "casual_leaves")
    private Integer casualLeaves;

    @Column(name = "basic_pay_percent")
    private Integer basicPayPercent;

    @Column(name = "hra_percent")
    private Integer hraPercent;

    @Column(name = "pf_percent")
    private Integer pfPercent;

    @Column(name = "gratuity_percent")
    private Integer gratuityPercent;

    @Column(name = "leave_rules", columnDefinition = "JSON")
    @Convert(converter = STConverter.ListOfStringConverter.class)
    private List<String> leaveRules;

    @Column(name = "timestamp", nullable = false)
    private Timestamp timestamp;


}




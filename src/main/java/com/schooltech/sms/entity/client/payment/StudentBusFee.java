package com.schooltech.sms.entity.client.payment;

import com.schooltech.sms.utility.converters.STConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.Map;


@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "student_bus_fee", uniqueConstraints = @UniqueConstraint(columnNames = {"username", "session"}))
public class StudentBusFee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @Column(name = "username", nullable = false)//,unique = true)
    private String username;   //It is uniqueID
    @Column(name = "session", nullable = false) //example 2024-2025
    private String session;

    @Column(name = "bus_monthly_fees", columnDefinition = "JSON")
    @Convert(converter = STConverter.MapOfStringKeyDoubleValueConverter.class)
    private Map<String, Double> busMonthlyFees;

    @Column(name = "late_fee_charges")
    private Double lateFeeCharges;
    @Column(name = "concession")
    private Double concession;
    @Column(name = "total")
    private Double total;
    @Column(name = "total_late_concession")
    private Double totalLateConcession;
    @Column(name = "outstanding")
    private Double outstanding;

    @Column(name = "timestamp", nullable = false)
    private Timestamp timestamp;

}

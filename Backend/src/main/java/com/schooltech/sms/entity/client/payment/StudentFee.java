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
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,property = "id")
@ToString
@Table(name = "student_fee", uniqueConstraints = @UniqueConstraint(columnNames = {"username", "session"}))
public class StudentFee {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @Column(name = "username", nullable = false)//,unique = true)
    private String username;   //It is uniqueID
    @Column(name = "session", nullable = false) //example 2024-2025
    private String session;

    @Column(name = "school_monthly_fees", columnDefinition = "JSON")
    @Convert(converter = STConverter.MapOfStringKeyDoubleValueConverter.class)
    private Map<String, Double> schoolMonthlyFees;

    @Column(name = "school_misc_fees", columnDefinition = "JSON")
    @Convert(converter = STConverter.MapOfStringKeyDoubleValueConverter.class)
    private Map<String, Double> schoolMiscFees;

    @Column(name = "late_fee_charges")
    private Double lateFeeCharges;
    @Column(name = "concession")
    private Double concession;
    @Column(name = "scholarship")
    private Double scholarship;
    @Column(name = "total")
    private Double total;
    @Column(name = "total_late_conc_schlr")
    private Double totalLateConcSchlr;
    @Column(name = "outstanding")
    private Double outstanding;

    @Column(name = "timestamp", nullable = false)
    private Timestamp timestamp;


}

package com.schooltech.sms.entity.client.payment;

import com.schooltech.sms.utility.converters.STConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;


@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "student_combine_fee_stats")
public class StudentCombineFeeStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @Column(name = "session", nullable = false, unique = true)
    private String session;
    @Column(name = "tuition_fee")
    private Double tuitionFee;
    @Column(name = "tuition_scholarship")
    private Double tuitionScholarship;
    @Column(name = "admission_fee")
    private Double admissionFee;
    @Column(name = "registration_fee")
    private Double registrationFee;
    @Column(name = "annual_fee")
    private Double annualFee;
    @Column(name = "concession")
    private Double concession;

    @Column(name = "other_fee_type", columnDefinition = "JSON")
    @Convert(converter = STConverter.ListOfMapOfStringKeyDoubleValueConverter.class)
    private List<Map<String, Double>> otherFeeType;

    @Column(name = "timestamp", nullable = false)
    private Timestamp timestamp;

}

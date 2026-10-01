package com.schooltech.sms.entity.client.payment;


import com.schooltech.sms.utility.converters.STConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Map;


@Entity
@NoArgsConstructor
@Getter
@Setter
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,property = "id")
@ToString
@Table(name = "student_fee_receipt")
public class StudentFeeReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @Column(name = "username", nullable = false)//,unique = true)
    private String username;   //It is uniqueID
    //@Pattern(regexp = "^2025-2026$", message = "Academic year must be in the format '2025-2026'")
    @Column(name = "session", nullable = false)
    private String session;
    @Column(name = "receipt_no", nullable = false, unique = true)
    private String receiptNo;
    @Column(name = "date", nullable = false)
    private LocalDate date;
    @Column(name = "schedules", nullable = false)
    private String schedules;
    @Enumerated(EnumType.STRING)
    @Column(name = "mode", nullable = false)
    private Mode mode;
    @Column(name = "total", nullable = false)
    private Double total;
    @Column(name = "total_late_conc_schlr", nullable = false)
    private Double totalLateConcSchlr;
    @Column(name = "late_fee_charges")
    private Double lateFeeCharges;
    @Column(name = "concession")
    private Double concession;
    @Column(name = "concession_split", columnDefinition = "JSON")
    @Convert(converter = STConverter.MapOfStringKeyDoubleValueConverter.class)
    private Map<String, Double> concessionSplit;
    @Column(name = "scholarship")
    private Double scholarship;
    @Column(name = "remarks")
    private String remarks;
    @Column(name = "collected_by")
    private String collectedBy;
    @Column(name = "timestamp", nullable = false)
    private Timestamp timestamp;

    public enum Mode {
        cash, online, bank
    }

}
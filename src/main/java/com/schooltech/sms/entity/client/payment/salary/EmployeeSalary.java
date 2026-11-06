package com.schooltech.sms.entity.client.payment.salary;

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
@Table(name = "employee_salary", uniqueConstraints = @UniqueConstraint(columnNames = {"username", "session"}))
public class EmployeeSalary {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @Column(name = "username", nullable = false)//,unique = true)
    private String username;   //It is uniqueID
    @Column(name = "session", nullable = false) //example 2024-2025
    private String session;

    @Column(name = "ctc")
    private Double ctc;
    @Column(name = "basic_pay")
    private Double basicPay;
    @Column(name = "hra")
    private Double hra;
    @Column(name = "pf")
    private Double pf;
    @Column(name = "gratuity")
    private Double gratuity;
    @Column(name = "bonus")
    private Double bonus;
    @Column(name = "tax")
    private Double tax;
    @Column(name = "arrear")
    private Double arrear;
    @Column(name = "deduction")
    private Double deduction;

    @Column(name = "pay_out", columnDefinition = "JSON")
    @Convert(converter = STConverter.MapOfStringKeySalaryComponentValueConverter.class)
    //key -> month value -> SalaryComponent
    private Map<String, SalaryComponent> payOut;

    @Column(name = "lwp")
    private Double lwp;

    @Column(name = "timestamp", nullable = false)
    private Timestamp timestamp;


}

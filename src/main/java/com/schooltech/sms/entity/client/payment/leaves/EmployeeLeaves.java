package com.schooltech.sms.entity.client.payment.leaves;

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
@Table(name = "employee_leaves", uniqueConstraints = @UniqueConstraint(columnNames = {"username", "session"}))
public class EmployeeLeaves {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @Column(name = "username", nullable = false)//,unique = true)
    private String username;   //It is uniqueID
    @Column(name = "session", nullable = false) //example 2024-2025
    private String session;

    @Column(name = "paid_leaves")
    private Float paidLeaves;
    @Column(name = "sick_leaves")
    private Float sickLeaves;
    @Column(name = "casual_leaves")
    private Float casualLeaves;

    @Column(name = "leaves_applied", columnDefinition = "JSON")
    @Convert(converter = STConverter.MapOfStringKeyListofAppliedLeavesValueConverter.class)
    //key -> month value -> AppliedLeavesComponent
    private Map<String, List<AppliedLeavesComponent>> leavesApplied;

    @Column(name = "leaves_taken", columnDefinition = "JSON")
    @Convert(converter = STConverter.MapOfStringKeyLeaveComponentValueConverter.class)
    //key -> month value -> LeaveComponent
    private Map<String, LeaveComponent> leavesTaken;

    @Column(name = "timestamp", nullable = false)
    private Timestamp timestamp;


}

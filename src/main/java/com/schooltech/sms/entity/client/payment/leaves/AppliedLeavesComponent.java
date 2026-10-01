package com.schooltech.sms.entity.client.payment.leaves;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppliedLeavesComponent {
    private String date;
    @Enumerated(EnumType.STRING)
    private AppliedLeavesComponent.LeaveType leaveType;
    private Float leaveNo;
    @Enumerated(EnumType.STRING)
    private AppliedLeavesComponent.Approved approved;

    public enum Approved {
        yes, no, pending
    }

    public enum LeaveType {
        paidLeave, sickLeave, casualLeave, unpaidLeave
    }
}

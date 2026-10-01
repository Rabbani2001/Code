package com.schooltech.sms.entity.client.payment.leaves;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveComponent {
    private Float monthPaidLeaves;
    private Float monthSickLeaves;
    private Float monthCasualLeave;
    private Float monthUnpaidLeaves;
}

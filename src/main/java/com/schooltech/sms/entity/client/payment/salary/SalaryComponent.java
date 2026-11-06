package com.schooltech.sms.entity.client.payment.salary;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaryComponent {
    private Double monthPf;
    private Double monthHra;
    private Double monthBasicPay;
    private Double monthSalary;
    private Double monthPerDaySalary;
    private Double monthLwp;
    private Double monthLwpLatePresent;
    private Double monthLwpAbsent;
    private Double monthLwpUnpaidHalfDay;
    private Double monthDeduction;
    private Double monthBonus;
    private Double monthArrearPayMore;
    private Double monthArrearPayLess;
    private Double monthArrearPayMorePrevious;
    private Double monthArrearPayLessPrevious;
    private Double monthSalaryPaid;
    private Double monthGratuity;
    private Double monthTax;
}

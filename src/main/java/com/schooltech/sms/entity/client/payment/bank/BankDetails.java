package com.schooltech.sms.entity.client.payment.bank;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "bank_details")
public class BankDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username cannot be empty")
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "bank_name", nullable = false)
    private String bankName;

    @Column(name = "bank_account_no", nullable = false, unique = true)
    private String bankAccountNo;

    @Column(name = "account_holder_name", nullable = false)
    private String accountHolderName;

    @Column(name = "ifsc_code", nullable = false)
    private String ifscCode;

    @Column(name = "upi_id")
    private String upiId;

    @Column(nullable = false)
    private Timestamp timestamp;
}


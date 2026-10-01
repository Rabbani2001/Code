package com.schooltech.sms.entity.client.sequence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sequence_username",
        uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_code", "role"}))
@Getter
@Setter
public class UsernameSequence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_code", nullable = false)
    private String tenantCode;

    @Column(nullable = false)
    private String role;

    @Column(name = "next_value", nullable = false)
    private Long nextValue;
}

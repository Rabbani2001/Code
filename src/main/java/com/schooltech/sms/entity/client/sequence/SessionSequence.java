package com.schooltech.sms.entity.client.sequence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sequence_session",
        uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_code", "session_key", "type"}))
@Getter
@Setter
public class SessionSequence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "tenant_code", nullable = false)
    private String tenantCode;

    @Column(name = "session_key", nullable = false)
    private String sessionKey;

    @Column(name = "next_value", nullable = false)
    private Long nextValue;
}
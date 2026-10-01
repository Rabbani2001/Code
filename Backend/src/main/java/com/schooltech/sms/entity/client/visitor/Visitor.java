package com.schooltech.sms.entity.client.visitor;

import com.schooltech.sms.utility.converters.STConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "visitor")
public class Visitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "visitor_id", nullable = false, unique = true)
    private String visitorId;

    @NotBlank(message = "Visitor name cannot be empty or null")
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "phone_no")
    private String phoneNo;

    @Column(name = "email")
    private String email;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "check_in_time")
    private LocalTime checkInTime;

    @Column(name = "check_out_time")
    private LocalTime checkOutTime;

    @Column(name = "resolution_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Resolution resolutionStatus;

    @Column(name = "address")
    private String address;

    @NotBlank(message = "Visit type cannot be empty or null")
    @Column(name = "visit_type", nullable = false)
    private String visitType;

    @Column(name = "visit_comments", columnDefinition = "JSON")
    @Convert(converter = STConverter.MapOfStringKeyCommentValueConverter.class)
    private Map<String, Comment> visitComments;


    @Column(name = "documents", columnDefinition = "JSON")
    @Convert(converter = STConverter.MapOfStringKeyStringValueConverter.class)
    private Map<String, String> documents;


    @Column(name = "timestamp", nullable = false)
    private Timestamp timestamp;

    public enum Resolution {
        pending, resolved
    }
}
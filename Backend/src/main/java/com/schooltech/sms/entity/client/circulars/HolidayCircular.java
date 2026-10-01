package com.schooltech.sms.entity.client.circulars;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "holiday_circular")
public class HolidayCircular {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "my_sequence")
    //@SequenceGenerator(name = "my_sequence", sequenceName = "my_sequence", allocationSize = 1)
    private Long id;
    @Column(name = "date", nullable = false, unique = true)
    private LocalDate date;
    @Column(name = "holiday_name", nullable = false)
    private String holidayName;
    @Column(name = "timestamp", nullable = false)
    @NotNull
    private Timestamp timestamp;

}

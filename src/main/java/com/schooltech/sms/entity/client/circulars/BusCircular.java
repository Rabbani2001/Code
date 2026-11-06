package com.schooltech.sms.entity.client.circulars;


import com.schooltech.sms.utility.converters.STConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Map;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "bus_circular", uniqueConstraints = @UniqueConstraint(columnNames = {"busRoute", "session"}))
public class BusCircular {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "session", nullable = false) //example 2024-2025
    private String session;
    @Column(name = "bus_route", nullable = false)
    private String busRoute;

    @Column(name = "bus_monthly_fees", columnDefinition = "JSON")
    @Convert(converter = STConverter.MapOfStringKeyDoubleValueConverter.class)
    private Map<String, Double> busMonthlyFees;

    @Column(name = "bus_fees_rules", columnDefinition = "JSON")
    @Convert(converter = STConverter.MapOfStringKeyDoubleValueConverter.class)
    private Map<String, Double> busFeesRules;


    @Column(name = "timestamp", nullable = false)
    private Timestamp timestamp;

}

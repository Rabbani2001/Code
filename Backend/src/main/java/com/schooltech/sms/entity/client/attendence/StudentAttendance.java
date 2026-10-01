package com.schooltech.sms.entity.client.attendence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;


@Entity
@NoArgsConstructor
@Getter
@Setter
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,property = "id")
@Table(name = "student_attendance")
public class StudentAttendance {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(name = "username", nullable = false)
    private String username;   //It is uniqueID

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "check_in_time")
    private LocalTime checkInTime;

    @Column(name = "check_out_time")
    private LocalTime checkOutTime;

    @Column(name = "class_name")
    private String className;

    @Enumerated(EnumType.STRING)
    private Status status;
    @Enumerated(EnumType.STRING)
    private TeacherAttendance.Approved approved;
    @Column(name = "timestamp", nullable = false)
    private Timestamp timestamp;

    @Override
    public String toString() {
        return "StudentAttendance{" +
                "Id=" + Id +
                ", studentId=" + username +
                ", date=" + date +
                ", checkInTime=" + checkInTime +
                ", checkOutTime=" + checkOutTime +
                ", status=" + status +
                ", timestamp=" + timestamp +
                ", className=" + className +
                '}';
    }

    public enum Status {
        present, absent, leave, holiday
    }


    public enum Approved {
        yes, no
    }
}

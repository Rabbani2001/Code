package com.schooltech.sms.entity.client.attendence;

import com.schooltech.sms.utility.converters.STConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;


@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "teacher_attendance")
public class TeacherAttendance {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(name = "username", nullable = false)//,unique = true)
    private String username;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "check_in_time")
    private LocalTime checkInTime;

    @Column(name = "check_out_time")
    private LocalTime checkOutTime;

    @Column(name = "updated_by", columnDefinition = "JSON")
    @Convert(converter = STConverter.AttendanceAuditListConverter.class)
    //@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = UpdatedByDeserializer.class)
    private List<AttendanceAuditEntry> updatedBy;

    @Column(name = "meta_data", columnDefinition = "JSON")
    @Convert(converter = STConverter.MapOfStringKeyStringValueConverter.class)
    private Map<String, String> metaData;

    @Enumerated(EnumType.STRING)
    private Status status;
    @Enumerated(EnumType.STRING)
    private Approved approved;
    @Column(name = "timestamp", nullable = false)
    private Timestamp timestamp;

    @Override
    public String toString() {
        return "TeacherAttendance{" +
                "Id=" + Id +
                ", teacherId='" + username + '\'' +
                ", date=" + date +
                ", checkInTime=" + checkInTime +
                ", checkOutTime=" + checkOutTime +
                ", updatedBy=" + updatedBy +
                ", metaData=" + metaData +
                ", timestamp=" + timestamp +
                ", status=" + status +
                '}';
    }

    public enum Status {
        present, absent, leave, holiday
    }


    public enum Approved {
        yes, no
    }
}


/*
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,property = "id")


@ManyToOne(fetch = FetchType.EAGER,cascade = {CascadeType.PERSIST,CascadeType.DETACH,CascadeType.MERGE,CascadeType.REFRESH})
    @JoinColumn(name = "teacher_id")
    //@JsonIgnore
    private Teacher teacher;

 */
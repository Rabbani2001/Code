package com.schooltech.sms.utility.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.schooltech.sms.entity.client.attendence.AttendanceAuditEntry;
import com.schooltech.sms.entity.client.circulars.*;
import com.schooltech.sms.entity.client.payment.leaves.AppliedLeavesComponent;
import com.schooltech.sms.entity.client.payment.leaves.LeaveComponent;
import com.schooltech.sms.entity.client.payment.salary.SalaryComponent;
import com.schooltech.sms.entity.client.student.StudentExamSchedule;
import com.schooltech.sms.entity.client.student.StudentExamScheduleGrade;
import com.schooltech.sms.entity.client.visitor.Comment;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class STConverter {

    //MAP converters
    @Converter
    public static class MapOfStringKeyDoubleValueConverter implements AttributeConverter<Map<String, Double>, String> {
        private static final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public String convertToDatabaseColumn(Map<String, Double> comments) {
            if (comments == null) return null;
            try {
                return objectMapper.writeValueAsString(comments);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error converting MapOfStringKeyDoubleValue to JSON", e);
            }
        }

        @Override
        public Map<String, Double> convertToEntityAttribute(String json) {
            if (json == null || json.isEmpty()) return Map.of();
            try {
                return objectMapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<>() {
                });
            } catch (IOException e) {
                throw new RuntimeException("Error reading MapOfStringKeyDoubleValue from JSON", e);
            }
        }
    }

    @Converter
    public static class MapOfStringKeyIntegerValueConverter implements AttributeConverter<Map<String, Integer>, String> {
        private static final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public String convertToDatabaseColumn(Map<String, Integer> attribute) {
            if (attribute == null) return null;
            try {
                return objectMapper.writeValueAsString(attribute);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error converting MapOfStringKeyIntegerValue to JSON", e);
            }
        }

        @Override
        public Map<String, Integer> convertToEntityAttribute(String dbData) {
            if (dbData == null || dbData.isEmpty()) return new HashMap<>();
            try {
                return objectMapper.readValue(dbData, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Integer>>() {});
            } catch (IOException e) {
                throw new RuntimeException("Error converting JSON to MapOfStringKeyIntegerValue", e);
            }
        }
    }

    @Converter
    public static class MapOfStringKeySalaryComponentValueConverter implements AttributeConverter<Map<String, SalaryComponent>, String> {
        private static final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public String convertToDatabaseColumn(Map<String, SalaryComponent> comments) {
            if (comments == null) return null;
            try {
                return objectMapper.writeValueAsString(comments);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error converting MapOfStringKeySalaryComponentValue to JSON", e);
            }
        }

        @Override
        public Map<String, SalaryComponent> convertToEntityAttribute(String json) {
            if (json == null || json.isEmpty()) return Map.of();
            try {
                return objectMapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<>() {
                });
            } catch (IOException e) {
                throw new RuntimeException("Error reading MapOfStringKeySalaryComponentValue from JSON", e);
            }
        }
    }

    @Converter
    public static class MapOfStringKeyLeaveComponentValueConverter implements AttributeConverter<Map<String, LeaveComponent>, String> {
        private static final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public String convertToDatabaseColumn(Map<String, LeaveComponent> comments) {
            if (comments == null) return null;
            try {
                return objectMapper.writeValueAsString(comments);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error converting MapOfStringKeyLeaveComponentValue to JSON", e);
            }
        }

        @Override
        public Map<String, LeaveComponent> convertToEntityAttribute(String json) {
            if (json == null || json.isEmpty()) return Map.of();
            try {
                return objectMapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<>() {
                });
            } catch (IOException e) {
                throw new RuntimeException("Error reading MapOfStringKeyListOfLeaveValue from JSON", e);
            }
        }
    }

    @Converter
    public static class MapOfStringKeyListofAppliedLeavesValueConverter implements AttributeConverter<Map<String, List<AppliedLeavesComponent>>, String> {
        private static final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public String convertToDatabaseColumn(Map<String, List<AppliedLeavesComponent>> attribute) {
            if (attribute == null) {
                return null;
            }
            try {
                return objectMapper.writeValueAsString(attribute);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error converting MapOfStringKeyListofAppliedLeavesValue to JSON ", e);
            }
        }

        @Override
        public Map<String, List<AppliedLeavesComponent>> convertToEntityAttribute(String dbData) {
            if (dbData == null || dbData.isEmpty()) {
                return new HashMap<>();
            }
            try {
                return objectMapper.readValue(dbData, new com.fasterxml.jackson.core.type.TypeReference<Map<String, List<AppliedLeavesComponent>>>() {
                });
            } catch (IOException e) {
                throw new RuntimeException("Error converting JSON to MapOfStringKeyListofAppliedLeavesValue ", e);
            }
        }
    }

    @Converter
    public static class MapOfStringKeyStringValueConverter implements AttributeConverter<Map<String, String>, String> {
        private static final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public String convertToDatabaseColumn(Map<String, String> attribute) {
            if (attribute == null) {
                return null;
            }
            try {
                return objectMapper.writeValueAsString(attribute);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error converting MapOfStringKeyStringValue to JSON", e);
            }
        }

        @Override
        public Map<String, String> convertToEntityAttribute(String dbData) {
            if (dbData == null || dbData.isEmpty()) {
                return new HashMap<>();
            }
            try {
                return objectMapper.readValue(dbData, new com.fasterxml.jackson.core.type.TypeReference<Map<String, String>>() {
                });
            } catch (IOException e) {
                throw new RuntimeException("Error converting JSON to MapOfStringKeyStringValue", e);
            }
        }
    }

    @Converter
    public static class MapOfStringKeyCommentValueConverter implements AttributeConverter<Map<String, Comment>, String> {
        private static final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public String convertToDatabaseColumn(Map<String, Comment> attribute) {
            if (attribute == null) {
                return null;
            }
            try {
                return objectMapper.writeValueAsString(attribute);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error converting MapOfStringKeyCommentValue to JSON", e);
            }
        }

        @Override
        public Map<String, Comment> convertToEntityAttribute(String dbData) {
            if (dbData == null || dbData.isEmpty()) {
                return new HashMap<>();
            }
            try {
                return objectMapper.readValue(dbData, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Comment>>() {
                });
            } catch (IOException e) {
                throw new RuntimeException("Error converting JSON to MapOfStringKeyCommentValue", e);
            }
        }
    }

    @Converter
    public static class MapOfStringKeyListOfStringValueConverter implements AttributeConverter<Map<String, List<String>>, String> {
        private static final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public String convertToDatabaseColumn(Map<String, List<String>> classQualities) {
            if (classQualities == null) return null;
            try {
                return objectMapper.writeValueAsString(classQualities);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error converting classQualities to JSON", e);
            }
        }

        @Override
        public Map<String, List<String>> convertToEntityAttribute(String json) {
            if (json == null || json.isEmpty()) return new HashMap<>();
            try {
                return objectMapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<>() {
                });
            } catch (IOException e) {
                throw new RuntimeException("Error reading classQualities from JSON", e);
            }
        }
    }

    @Converter
    public static class MapOfStringKeyShiftTimeValueConverter implements AttributeConverter<Map<String, ShiftTime>, String> {
        private static final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public String convertToDatabaseColumn(Map<String, ShiftTime> shifts) {
            if (shifts == null) return null;
            try {
                return objectMapper.writeValueAsString(shifts);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error converting MapOfStringKeyShiftTimeValue to JSON", e);
            }
        }

        @Override
        public Map<String, ShiftTime> convertToEntityAttribute(String json) {
            if (json == null || json.isEmpty()) return Map.of();
            try {
                return objectMapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<>() {
                });
            } catch (IOException e) {
                throw new RuntimeException("Error reading MapOfStringKeyShiftTimeValue from JSON", e);
            }
        }
    }

    ///////////////////////////////////////////////////List converters////////////////////////////////////////
    @Converter
    public static class ListOfStringConverter implements AttributeConverter<List<String>, String> {
        private static final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public String convertToDatabaseColumn(List<String> subjects) {
            if (subjects == null) return null;
            try {
                return objectMapper.writeValueAsString(subjects);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error converting ListOfString to JSON", e);
            }
        }

        @Override
        public List<String> convertToEntityAttribute(String json) {
            if (json == null || json.isEmpty()) return List.of();
            try {
                return objectMapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<>() {
                });
            } catch (IOException e) {
                throw new RuntimeException("Error reading ListOfString from JSON", e);
            }
        }
    }

    @Converter
    public static class ListOfMapOfStringKeyDoubleValueConverter implements AttributeConverter<List<Map<String, Double>>, String> {
        private static final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public String convertToDatabaseColumn(List<Map<String, Double>> comments) {
            if (comments == null) return null;
            try {
                return objectMapper.writeValueAsString(comments);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error converting MapOfStringKeyLongValue to JSON", e);
            }
        }

        @Override
        public List<Map<String, Double>> convertToEntityAttribute(String json) {
            if (json == null || json.isEmpty()) return (List<Map<String, Double>>) Map.of();
            try {
                return objectMapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<>() {
                });
            } catch (IOException e) {
                throw new RuntimeException("Error reading MapOfStringKeyLongValue from JSON", e);
            }
        }
    }



    @Converter
    public static class ExamScheduleConverter implements AttributeConverter<Map<String, List<ExamSchedule>>, String> {
        private static final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public String convertToDatabaseColumn(Map<String, List<ExamSchedule>> examSchedule) {
            if (examSchedule == null) return null;
            try {
                return objectMapper.writeValueAsString(examSchedule);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error converting examSchedule to JSON", e);
            }
        }

        @Override
        public Map<String, List<ExamSchedule>> convertToEntityAttribute(String json) {
            if (json == null || json.isEmpty()) return new HashMap<>();
            try {
                return objectMapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<>() {
                });
            } catch (IOException e) {
                throw new RuntimeException("Error reading examSchedule from JSON", e);
            }
        }
    }

    @Converter
    public static class ExamScheduleGradeConverter implements AttributeConverter<Map<String, List<ExamScheduleGrade>>, String> {
        private static final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public String convertToDatabaseColumn(Map<String, List<ExamScheduleGrade>> ExamSchedulesGrade) {
            if (ExamSchedulesGrade == null) return null;
            try {
                return objectMapper.writeValueAsString(ExamSchedulesGrade);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error converting examScheduleGrade to JSON", e);
            }
        }


        @Override
        public Map<String, List<ExamScheduleGrade>> convertToEntityAttribute(String json) {
            if (json == null || json.isEmpty()) return new HashMap<>();
            try {
                return objectMapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<>() {
                });
            } catch (IOException e) {
                throw new RuntimeException("Error reading examScheduleGrade from JSON", e);
            }
        }
    }

    @Converter
    public static class DateSheetConverter implements AttributeConverter<Map<String, List<Datesheetsubject>>, String> {
        private static final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public String convertToDatabaseColumn(Map<String, List<Datesheetsubject>> dateSheet) {
            if (dateSheet == null) return null;
            try {
                return objectMapper.writeValueAsString(dateSheet);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error converting dateSheet to JSON", e);
            }
        }

        @Override
        public Map<String, List<Datesheetsubject>> convertToEntityAttribute(String json) {
            if (json == null || json.isEmpty()) return Map.of();
            try {
                return objectMapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<>() {
                });
            } catch (IOException e) {
                throw new RuntimeException("Error reading dateSheet from JSON", e);
            }
        }
    }

    @Converter
    public static class GradeCircularConverter implements AttributeConverter<List<ResultGrade>, String> {
        private static final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public String convertToDatabaseColumn(List<ResultGrade> gradeCircular) {
            if (gradeCircular == null) return null;
            try {
                return objectMapper.writeValueAsString(gradeCircular);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error converting gradeCircular to JSON", e);
            }
        }

        @Override
        public List<ResultGrade> convertToEntityAttribute(String json) {
            if (json == null || json.isEmpty()) return List.of();
            try {
                return objectMapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<>() {
                });
            } catch (IOException e) {
                throw new RuntimeException("Error reading gradeCircular from JSON", e);
            }
        }
    }

    @Converter
    public static class StudentExamScheduleConverter implements AttributeConverter<Map<String, List<StudentExamSchedule>>, String> {
        private static final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public String convertToDatabaseColumn(Map<String, List<StudentExamSchedule>> examSchedule) {
            if (examSchedule == null) return null;
            try {
                return objectMapper.writeValueAsString(examSchedule);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error converting examSchedule to JSON", e);
            }
        }

        @Override
        public Map<String, List<StudentExamSchedule>> convertToEntityAttribute(String json) {
            if (json == null || json.isEmpty()) return new HashMap<>();
            try {
                return objectMapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<>() {
                });
            } catch (IOException e) {
                throw new RuntimeException("Error reading examSchedule from JSON", e);
            }
        }
    }

    @Converter
    public static class StudentExamScheduleGradeConverter implements AttributeConverter<Map<String, List<StudentExamScheduleGrade>>, String> {
        private static final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public String convertToDatabaseColumn(Map<String, List<StudentExamScheduleGrade>> examSchedule) {
            if (examSchedule == null) return null;
            try {
                return objectMapper.writeValueAsString(examSchedule);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error converting examScheduleGrade to JSON", e);
            }
        }

        @Override
        public Map<String, List<StudentExamScheduleGrade>> convertToEntityAttribute(String json) {
            if (json == null || json.isEmpty()) return Map.of();
            try {
                return objectMapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<>() {
                });
            } catch (IOException e) {
                throw new RuntimeException("Error reading examScheduleGrade from JSON", e);
            }
        }
    }


    @Converter
    public static class ClassQualitiesConverter implements AttributeConverter<Map<String, List<ClassQuality>>, String> {
        private static final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public String convertToDatabaseColumn(Map<String, List<ClassQuality>> classQualities) {
            if (classQualities == null) return null;
            try {
                return objectMapper.writeValueAsString(classQualities);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error converting classQualities to JSON", e);
            }
        }

        @Override
        public Map<String, List<ClassQuality>> convertToEntityAttribute(String json) {
            if (json == null || json.isEmpty()) return Map.of();
            try {
                return objectMapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<>() {
                });
            } catch (IOException e) {
                throw new RuntimeException("Error reading classQualities from JSON", e);
            }
        }
    }

    // turns the Java list into JSON text to store in MySQL
    @Converter
    public static class AttendanceAuditListConverter implements AttributeConverter<List<AttendanceAuditEntry>, String> {
        private static final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public String convertToDatabaseColumn(List<AttendanceAuditEntry> entries) {
            if (entries == null) return null;
            try {
                return objectMapper.writeValueAsString(entries);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error converting AttendanceAuditList to JSON", e);
            }
        }

        @Override
        public List<AttendanceAuditEntry> convertToEntityAttribute(String json) {
            if (json == null || json.isEmpty()) return new ArrayList<>();
            try {
                return objectMapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<>() {
                });
            } catch (IOException e) {
                throw new RuntimeException("Error reading AttendanceAuditList from JSON", e);
            }
        }
    }

}

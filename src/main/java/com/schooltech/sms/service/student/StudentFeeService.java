package com.schooltech.sms.service.student;


import com.schooltech.sms.constant.AppConstant;
import com.schooltech.sms.controller.student.dto.*;
import com.schooltech.sms.dao.client.circulars.BusCircularRepository;
import com.schooltech.sms.dao.client.circulars.ClassCircularRepository;
import com.schooltech.sms.dao.client.payment.*;
import com.schooltech.sms.dao.client.student.ParentRepository;
import com.schooltech.sms.dao.client.student.StudentRepository;
import com.schooltech.sms.entity.client.circulars.BusCircular;
import com.schooltech.sms.entity.client.circulars.ClassCircular;
import com.schooltech.sms.entity.client.payment.*;
import com.schooltech.sms.entity.client.student.Parent;
import com.schooltech.sms.entity.client.student.Student;
import com.schooltech.sms.exception.CircularNotFoundException;
import com.schooltech.sms.exception.PaymentNotFoundException;
import com.schooltech.sms.exception.dto.StudentNotFoundException;
import com.schooltech.sms.utility.DateUtility;
import com.schooltech.sms.utility.SessionSequenceUtility;
import com.schooltech.sms.utility.UsernameUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.schooltech.sms.constant.AppConstant.FEE_TYPE_LIST;
import static com.schooltech.sms.constant.AppConstant.OTHER_FEE_SUPPORT_IN_FEES;

@Service
public class StudentFeeService {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private StudentFeeRepository studentFeeRepository;
    @Autowired
    private StudentFeeStatsRepository studentFeeStatsRepository;
    @Autowired
    private StudentCombineFeeStatsRepository studentCombineFeeStatsRepository;
    @Autowired
    private StudentBusFeeRepository studentBusFeeRepository;
    @Autowired
    private ClassCircularRepository classCircularRepository;
    @Autowired
    private BusCircularRepository busCircularRepository;
    @Autowired
    private StudentFeeReceiptRepository studentFeeReceiptRepository;
    @Autowired
    private StudentBusFeeReceiptRepository studentBusFeeReceiptRepository;
    @Autowired
    private UsernameUtility usernameUtility;
    @Autowired
    private SessionSequenceUtility sessionSequenceUtility;
    @Autowired
    private ParentRepository parentRepository;

    ///////////////////// STUDENT FEE///////////////////////////////
    public ResponseEntity<StudentFee> getStudentFee(String studentId, String session) {
        StudentFee studentFee = studentFeeRepository.findByStudentIdAndSession(studentId, session);
        if (studentFee == null) {
            throw new PaymentNotFoundException("Student Payment Not Found");
        }
        return new ResponseEntity<>(studentFee, HttpStatus.OK);
    }

    public ResponseEntity<List<StudentFee>> getStudentFees() {
        List<StudentFee> studentFees = (List<StudentFee>) studentFeeRepository.findAll();
        return new ResponseEntity<>(studentFees, HttpStatus.OK);
    }

    public ResponseEntity<String> saveStudentFees(List<StudentFeeDTO> studentFeesDTO) {
        List<StudentFee> studentFeeList = new ArrayList<>(); //this list will be prepared and saved together
        studentFeesDTO.stream().forEach(studentFeeDTO -> {
            StudentFee studentFee = studentFeeDTO.getStudentFee();
            StudentFee studentFeeDB = studentFeeRepository.findByStudentIdAndSession(studentFee.getUsername(), studentFee.getSession());
            if (studentFeeDB == null) {
                //check that student exist or not
                Optional<Student> studentOptional = studentRepository.findByUsername(studentFee.getUsername());
                Student student = studentOptional.orElseThrow(() -> new RuntimeException("Student: " + studentFee.getUsername() + " Not Found"));
                //finding class circluar
                Optional<ClassCircular> classCircularOptional = classCircularRepository.findByClassNameAndSession(studentFeeDTO.getClassName(), studentFee.getSession());
                ClassCircular classCircular = classCircularOptional.orElseThrow(() -> new CircularNotFoundException("Class Circular Not Found"));
                //now process data
                processSaveStudentFeesData(studentFee, studentFeeList, classCircular, student.getAdmissionStatus());
            } else {
                throw new RuntimeException("Student: " + studentFee.getUsername() + " School fee already Present. Aborting Save");
            }
        });
        studentFeeRepository.saveAll(studentFeeList);
        return ResponseEntity.ok("Student Fee Data Saved");
    }

    public void processSaveStudentFeesData(StudentFee studentFee, List<StudentFee> studentFeeList, ClassCircular classCircular, Student.AdmissionStatus admissionStatus) {
        Map<String, Double> schoolMonthlyFeesInCircular = classCircular.getSchoolMonthlyFees();
        Map<String, Double> schoolMiscFeesInCircular = classCircular.getSchoolMiscFees();

        double miscTotal = sumMiscFeesBasedOnAdmission(schoolMiscFeesInCircular, admissionStatus);

        double totalFees =
                sumMapValues(schoolMonthlyFeesInCircular)
                        + miscTotal;

        studentFee.setTotal(totalFees);
        studentFee.setTotalLateConcSchlr(totalFees);
        studentFee.setOutstanding(totalFees);

        studentFee.setTimestamp(DateUtility.getCurrentTimeStamp());
        studentFeeList.add(studentFee);
    }


    private double sumMiscFeesBasedOnAdmission(
            Map<String, Double> miscFees,
            Student.AdmissionStatus admissionStatus) {

        if (admissionStatus == Student.AdmissionStatus.OLD) {
            return miscFees.entrySet().stream()
                    .filter(entry ->
                            !entry.getKey().equalsIgnoreCase("admissionFee") &&
                                    !entry.getKey().equalsIgnoreCase("registrationFee"))
                    .mapToDouble(Map.Entry::getValue)
                    .sum();
        }

        return sumMapValues(miscFees);
    }


    public ResponseEntity<String> updateStudentFees(List<StudentFeeDTO> studentFeesDTO) {
        List<StudentFee> studentFeeList = new ArrayList<>();
        List<StudentFeeReceipt> studentFeeReceiptList = new ArrayList<>();
        Map<String, StudentFeeStats> statsMap = new HashMap<>();

        studentFeesDTO.forEach(studentFeeDTO -> {
            //prepare fee data for saving
            StudentFee studentFee = studentFeeDTO.getStudentFee();
            StudentFee studentFeeDB = studentFeeRepository.findByStudentIdAndSession(studentFee.getUsername(), studentFee.getSession());
            if (studentFeeDB != null) {
                //check that student exist or not
                Optional<Student> studentOptional = studentRepository.findByUsername(studentFee.getUsername());
                Student student = studentOptional.orElseThrow(() -> new RuntimeException("Student: " + studentFee.getUsername() + " Not Found"));
                //finding class circluar
                Optional<ClassCircular> classCircularOptional = classCircularRepository.findByClassNameAndSession(studentFeeDTO.getClassName(), studentFee.getSession());
                ClassCircular classCircular = classCircularOptional.orElseThrow(() -> new CircularNotFoundException("Class Circular Not Found"));
                processStudentFeeExistingData(studentFee, studentFeeDB, classCircular, studentFeeList, student.getAdmissionStatus());
            } else {
                throw new RuntimeException("Student: " + studentFee.getUsername() + "  School Fee data not found. Aborting Update");
            }

            //create stats
            processStudentFeeStatsData(statsMap, studentFeeDTO);
            //prepare fee receipt data for saving
            //StudentFeeReceipt studentFeeReceipt = studentFeeDTO.getStudentFeeReceipt();
            if (studentFeeDTO.getStudentFeeReceipt() != null)
                studentFeeReceiptList.add(studentFeeDTO.getStudentFeeReceipt());

        });
        studentFeeRepository.saveAll(studentFeeList);
        studentFeeStatsRepository.saveAll(statsMap.values());
        if (!studentFeeReceiptList.isEmpty()) saveSchoolFeeReceipts(studentFeeReceiptList);
        return ResponseEntity.ok("Student Fee Data Saved");
    }

    public void processStudentFeeStatsData(Map<String, StudentFeeStats> statsMap, StudentFeeDTO studentFeeDTO) {
        StudentFeeStats incomingStats = studentFeeDTO.getStudentFeeStats();
        if (incomingStats != null) {
            String session = incomingStats.getSession();
            StudentFeeStats stats = statsMap.get(session);
            if (stats == null) {
                // Fetch from DB or create new
                stats = studentFeeStatsRepository.findBySession(session)
                        .orElseGet(() -> {
                            StudentFeeStats newStats = new StudentFeeStats();
                            newStats.setSession(session);
                            newStats.setTimestamp(DateUtility.getCurrentTimeStamp());
                            return newStats;
                        });
            }
            //tuition fee
            stats.setTuitionFee(
                    (stats.getTuitionFee() == null ? 0 : stats.getTuitionFee()) +
                            (incomingStats.getTuitionFee() == null ? 0 : incomingStats.getTuitionFee())
            );
            stats.setTuitionLateFee(
                    (stats.getTuitionLateFee() == null ? 0 : stats.getTuitionLateFee()) +
                            (incomingStats.getTuitionLateFee() == null ? 0 : incomingStats.getTuitionLateFee())
            );
            stats.setTuitionConcession(
                    (stats.getTuitionConcession() == null ? 0 : stats.getTuitionConcession()) +
                            (incomingStats.getTuitionConcession() == null ? 0 : incomingStats.getTuitionConcession())
            );
            stats.setTuitionScholarship(
                    (stats.getTuitionScholarship() == null ? 0 : stats.getTuitionScholarship()) +
                            (incomingStats.getTuitionScholarship() == null ? 0 : incomingStats.getTuitionScholarship())
            );

            //admission fee
            stats.setAdmissionFee(
                    (stats.getAdmissionFee() == null ? 0 : stats.getAdmissionFee()) +
                            (incomingStats.getAdmissionFee() == null ? 0 : incomingStats.getAdmissionFee())
            );
            stats.setAdmissionFeeConcession(
                    (stats.getAdmissionFeeConcession() == null ? 0 : stats.getAdmissionFeeConcession()) +
                            (incomingStats.getAdmissionFeeConcession() == null ? 0 : incomingStats.getAdmissionFeeConcession())
            );
            //registration fee
            stats.setRegistrationFee(
                    (stats.getRegistrationFee() == null ? 0 : stats.getRegistrationFee()) +
                            (incomingStats.getRegistrationFee() == null ? 0 : incomingStats.getRegistrationFee())
            );
            stats.setRegistrationFeeConcession(
                    (stats.getRegistrationFeeConcession() == null ? 0 : stats.getRegistrationFeeConcession()) +
                            (incomingStats.getRegistrationFeeConcession() == null ? 0 : incomingStats.getRegistrationFeeConcession())
            );
            //annualFee
            stats.setAnnualFee(
                    (stats.getAnnualFee() == null ? 0 : stats.getAnnualFee()) +
                            (incomingStats.getAnnualFee() == null ? 0 : incomingStats.getAnnualFee())
            );
            stats.setAnnualFeeConcession(
                    (stats.getAnnualFeeConcession() == null ? 0 : stats.getAnnualFeeConcession()) +
                            (incomingStats.getAnnualFeeConcession() == null ? 0 : incomingStats.getAnnualFeeConcession())
            );


            // Handle JSON field (merge lists)
//            if (incomingStats.getOtherFeeType() != null) {
//                if (stats.getOtherFeeType() == null) {
//                    stats.setOtherFeeType(new ArrayList<>());
//                }
//                stats.getOtherFeeType().addAll(incomingStats.getOtherFeeType());
//            }
            // Handle JSON field (aggregate fee types dynamically)
            if (incomingStats.getOtherFeeType() != null) {

                if (stats.getOtherFeeType() == null) {
                    stats.setOtherFeeType(new ArrayList<>());
                }

                List<Map<String, Double>> existingList = stats.getOtherFeeType();

                // Add all supported generic fee types here
                List<String> supportedFeeTypes = OTHER_FEE_SUPPORT_IN_FEES;

                for (Map<String, Double> incomingMap : incomingStats.getOtherFeeType()) {

                    for (Map.Entry<String, Double> entry : incomingMap.entrySet()) {

                        String incomingKey = entry.getKey();

                        Double incomingValue = entry.getValue() == null
                                ? 0.0
                                : entry.getValue();

                        String normalizedKey = incomingKey.toLowerCase();

                        boolean matched = false;

                        for (String feeType : supportedFeeTypes) {

                            if (normalizedKey.contains(feeType.toLowerCase())) {

                                boolean isConcession =
                                        normalizedKey.contains("concession");

                                String finalKey = isConcession
                                        ? feeType + "Concession"
                                        : feeType;

                                mergeOtherFee(existingList, finalKey, incomingValue);

                                matched = true;
                                break;
                            }
                        }

                        // Ignore unsupported fee types
                        if (!matched) {
                            continue;
                        }
                    }
                }
            }


            statsMap.put(session, stats);
        }
    }

    public void processStudentCombineFeeStatsData(Map<String, StudentCombineFeeStats> statsMap, StudentFeeDTO studentFeeDTO) {
        StudentCombineFeeStats incomingStats = studentFeeDTO.getStudentCombineFeeStats();
        if (incomingStats != null) {
            String session = incomingStats.getSession();
            StudentCombineFeeStats stats = statsMap.get(session);
            if (stats == null) {
                // Fetch from DB or create new
                stats = studentCombineFeeStatsRepository.findBySession(session)
                        .orElseGet(() -> {
                            StudentCombineFeeStats newStats = new StudentCombineFeeStats();
                            newStats.setSession(session);
                            newStats.setTimestamp(DateUtility.getCurrentTimeStamp());
                            return newStats;
                        });
            }
            //tuition fee
            stats.setTuitionFee(
                    (stats.getTuitionFee() == null ? 0 : stats.getTuitionFee()) +
                            (incomingStats.getTuitionFee() == null ? 0 : incomingStats.getTuitionFee())
            );
            stats.setTuitionScholarship(
                    (stats.getTuitionScholarship() == null ? 0 : stats.getTuitionScholarship()) +
                            (incomingStats.getTuitionScholarship() == null ? 0 : incomingStats.getTuitionScholarship())
            );
            //admission fee
            stats.setAdmissionFee(
                    (stats.getAdmissionFee() == null ? 0 : stats.getAdmissionFee()) +
                            (incomingStats.getAdmissionFee() == null ? 0 : incomingStats.getAdmissionFee())
            );
            //registration fee
            stats.setRegistrationFee(
                    (stats.getRegistrationFee() == null ? 0 : stats.getRegistrationFee()) +
                            (incomingStats.getRegistrationFee() == null ? 0 : incomingStats.getRegistrationFee())
            );
            //annualFee
            stats.setAnnualFee(
                    (stats.getAnnualFee() == null ? 0 : stats.getAnnualFee()) +
                            (incomingStats.getAnnualFee() == null ? 0 : incomingStats.getAnnualFee())
            );
            stats.setConcession(
                    (stats.getConcession() == null ? 0 : stats.getConcession()) +
                            (incomingStats.getConcession() == null ? 0 : incomingStats.getConcession())
            );


            // Handle JSON field (merge lists)
//            if (incomingStats.getOtherFeeType() != null) {
//                if (stats.getOtherFeeType() == null) {
//                    stats.setOtherFeeType(new ArrayList<>());
//                }
//                stats.getOtherFeeType().addAll(incomingStats.getOtherFeeType());
//            }
            // Handle JSON field (aggregate fee types dynamically)
            if (incomingStats.getOtherFeeType() != null) {

                if (stats.getOtherFeeType() == null) {
                    stats.setOtherFeeType(new ArrayList<>());
                }

                List<Map<String, Double>> existingList = stats.getOtherFeeType();

                // Add all supported generic fee types here
                List<String> supportedFeeTypes = OTHER_FEE_SUPPORT_IN_FEES;

                for (Map<String, Double> incomingMap : incomingStats.getOtherFeeType()) {

                    for (Map.Entry<String, Double> entry : incomingMap.entrySet()) {

                        String incomingKey = entry.getKey();

                        Double incomingValue = entry.getValue() == null
                                ? 0.0
                                : entry.getValue();

                        String normalizedKey = incomingKey.toLowerCase();

                        boolean matched = false;

                        for (String feeType : supportedFeeTypes) {

                            if (normalizedKey.contains(feeType.toLowerCase())) {

                                mergeOtherFee(existingList, feeType, incomingValue);

                                matched = true;
                                break;
                            }
                        }

                        // Ignore unsupported fee types
                        if (!matched) {
                            continue;
                        }
                    }
                }
            }


            statsMap.put(session, stats);
        }
    }

    private void mergeOtherFee(
            List<Map<String, Double>> existingList,
            String key,
            Double value
    ) {
        // This converts BOTH keys into same base name: "examfee1Concession" → "examfee1", "examfee1" → "examfee1"
        String baseKey = key.endsWith("Concession")
                ? key.substring(0, key.length() - "Concession".length())
                : key;

        // Find the map that belongs to this base fee type (contains either baseKey or baseKeyConcession)
        Map<String, Double> existingMap = existingList.stream()
                .filter(map -> map.containsKey(baseKey) || map.containsKey(baseKey + "Concession"))
                .findFirst()
                .orElse(null);

        if (existingMap != null) {
            // Add to existing group map
            Double existingValue =
                    existingMap.getOrDefault(key, 0.0);

            existingMap.put(key, existingValue + value);

        } else {
            // Create new group map for this fee type
            Map<String, Double> newMap = new HashMap<>();

            newMap.put(key, value);

            existingList.add(newMap);
        }
    }


    public ResponseEntity<String> updateStudentFeesCombined(StudentFeeCombinedDTO studentFeeCombinedDTO) {
        List<StudentFee> studentFeeList = new ArrayList<>();
        List<StudentFeeReceipt> studentFeeReceiptList = new ArrayList<>();
        Map<String, StudentCombineFeeStats> statsMap = new HashMap<>();

        studentFeeCombinedDTO.getStudentFeeDTOList().stream().forEach(studentFeeDTO -> {
            //prepare fee data for saving
            StudentFee studentFee = studentFeeDTO.getStudentFee();
            StudentFee studentFeeDB = studentFeeRepository.findByStudentIdAndSession(studentFee.getUsername(), studentFee.getSession());
            if (studentFeeDB != null) {
                //check that student exist or not
                Optional<Student> studentOptional = studentRepository.findByUsername(studentFee.getUsername());
                Student student = studentOptional.orElseThrow(() -> new RuntimeException("Student: " + studentFee.getUsername() + " Not Found"));
                //finding class circluar
                Optional<ClassCircular> classCircularOptional = classCircularRepository.findByClassNameAndSession(studentFeeDTO.getClassName(), studentFee.getSession());
                ClassCircular classCircular = classCircularOptional.orElseThrow(() -> new CircularNotFoundException("Class Circular Not Found"));
                processStudentFeeExistingData(studentFee, studentFeeDB, classCircular, studentFeeList, student.getAdmissionStatus());
            } else {
                throw new RuntimeException("Student: " + studentFee.getUsername() + "  School Fee data not found. Aborting Update");
            }
            //create stats
            processStudentCombineFeeStatsData(statsMap, studentFeeDTO);
        });
        studentFeeRepository.saveAll(studentFeeList);
        studentCombineFeeStatsRepository.saveAll(statsMap.values());
        studentFeeReceiptList.add(studentFeeCombinedDTO.getStudentFeeReceipt());
        saveSchoolFeeReceipts(studentFeeReceiptList);
        return ResponseEntity.ok("Student Fee Combine Data Saved");
    }

    public void processStudentFeeExistingData(StudentFee studentFee, StudentFee studentFeeDB, ClassCircular classCircular, List<StudentFee> studentFeeList, Student.AdmissionStatus admissionStatus) {

        Map<String, Double> schoolMonthlyFeesInCircular = classCircular.getSchoolMonthlyFees();
        Map<String, Double> schoolMiscFeesInCircular = classCircular.getSchoolMiscFees();

        Map<String, Double> schoolMonthlyFeesFromDB = studentFeeDB.getSchoolMonthlyFees();
        Map<String, Double> schoolMiscFeesFromDB = studentFeeDB.getSchoolMiscFees();

        Map<String, Double> schoolMonthlyFeesFromUI = studentFee.getSchoolMonthlyFees();
        Map<String, Double> schoolMiscFeesFromUI = studentFee.getSchoolMiscFees();

        //filtering fees as UI might send already submitted fees
        Map<String, Double> filteredMonthlyUI =
                filterAlreadyPaidFees(
                        schoolMonthlyFeesFromUI,
                        schoolMonthlyFeesFromDB
                );
        Map<String, Double> filteredMiscUI =
                filterAlreadyPaidFees(
                        schoolMiscFeesFromUI,
                        schoolMiscFeesFromDB
                );

        //if there is alrready any late & concession fee in db it will be merged with total
//        double totalFees =
//                sumMapValues(schoolMonthlyFeesInCircular) +
//                        sumMapValues(schoolMiscFeesInCircular);
        double monthlyTotal = sumMapValues(schoolMonthlyFeesInCircular);

        double miscTotal = sumMiscFeesBasedOnAdmission(
                schoolMiscFeesInCircular,
                admissionStatus
        );

        double totalFees = monthlyTotal + miscTotal;

        double paidFeesInDB =
                sumMapValues(schoolMonthlyFeesFromDB)
                        + sumMapValues(schoolMiscFeesFromDB);

        double paidFeesInUI =
                sumMapValues(filteredMonthlyUI)
                        + sumMapValues(filteredMiscUI);

//        long totalLateConcession = totalFees +
//                ((studentFeeDB.getLateFeeCharges() != null ? studentFeeDB.getLateFeeCharges() : 0) -
//                        (studentFeeDB.getConcession() != null ? studentFeeDB.getConcession() : 0)) +
//                ((studentFee.getLateFeeCharges() != null ? studentFee.getLateFeeCharges() : 0) -
//                        (studentFee.getConcession() != null ? studentFee.getConcession() : 0));
        double totalLateConcessionScholarship = totalFees +
                (
                        (studentFeeDB.getLateFeeCharges() != null ? studentFeeDB.getLateFeeCharges() : 0) -
                                (
                                        (studentFeeDB.getConcession() != null ? studentFeeDB.getConcession() : 0) +
                                                (studentFeeDB.getScholarship() != null ? studentFeeDB.getScholarship() : 0)
                                )
                )
                +
                (
                        (studentFee.getLateFeeCharges() != null ? studentFee.getLateFeeCharges() : 0) -
                                (
                                        (studentFee.getConcession() != null ? studentFee.getConcession() : 0) +
                                                (studentFee.getScholarship() != null ? studentFee.getScholarship() : 0)
                                )
                );

        double outstandingFees = totalFees - paidFeesInDB - paidFeesInUI;

        studentFee.setTotal(totalFees);
        studentFee.setTotalLateConcSchlr(totalLateConcessionScholarship);
        studentFee.setOutstanding(outstandingFees);

        //handling late fee with db late fee
        if (studentFeeDB.getLateFeeCharges() != null) {
            if (studentFee.getLateFeeCharges() != null) {
                studentFee.setLateFeeCharges(studentFee.getLateFeeCharges() + studentFeeDB.getLateFeeCharges());
            } else
                studentFee.setLateFeeCharges(studentFeeDB.getLateFeeCharges());
        }
        //handling concession with db concession
        if (studentFeeDB.getConcession() != null) {
            if (studentFee.getConcession() != null) {
                studentFee.setConcession(studentFee.getConcession() + studentFeeDB.getConcession());
            } else
                studentFee.setConcession(studentFeeDB.getConcession());
        }

        //logic to add monthlyfee in db if ui also sending monthly fee then adding it with db fees
        if (schoolMonthlyFeesFromDB != null && filteredMonthlyUI != null) {//&& !schoolMonthlyFeesFromDB.isEmpty()
            Map<String, Double> merged = new LinkedHashMap<>();
            merged.putAll(schoolMonthlyFeesFromDB);
            merged.putAll(filteredMonthlyUI);
            studentFee.setSchoolMonthlyFees(merged);

        }
        //logic to add miscfee in db if ui also sending misc fee then adding it with db fees
        if (schoolMiscFeesFromDB != null && filteredMiscUI != null) {// && !schoolMiscFeesFromDB.isEmpty()
            Map<String, Double> merged = new LinkedHashMap<>();
            merged.putAll(schoolMiscFeesFromDB);
            merged.putAll(filteredMiscUI);
            studentFee.setSchoolMiscFees(merged);
        }

        studentFee.setId(studentFeeDB.getId());
        studentFee.setScholarship(studentFee.getScholarship() != null ? studentFee.getScholarship() : studentFeeDB.getScholarship());
        studentFee.setTimestamp(DateUtility.getCurrentTimeStamp());
        studentFeeList.add(studentFee);
    }

    private double sumMapValues(Map<String, Double> map) {
        return map == null ? 0 :
                map.values().stream()
                        .filter(Objects::nonNull)
                        .mapToDouble(Double::doubleValue)
                        .sum();
    }

    private Map<String, Double> filterAlreadyPaidFees(
            Map<String, Double> uiFees,
            Map<String, Double> dbFees) {

        if (uiFees == null || uiFees.isEmpty()) return Map.of();
        if (dbFees == null || dbFees.isEmpty()) return uiFees;

        return uiFees.entrySet().stream()
                .filter(entry -> !dbFees.containsKey(entry.getKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    ///////////////////// PAYMENT STUDENT FEE///////////////////////////////
    public ResponseEntity<List<FeeReceiptsDTO>> getSchoolFeeReceiptsByStudentId(String studentId, String session) {
        List<FeeReceiptsDTO> feeReceiptsDTOList = new ArrayList<>();
        List<StudentFeeReceipt> studentFeeReceiptList = studentFeeReceiptRepository.findByUsernameAndSessionOrderByReceiptNoDesc(studentId, session);
        studentFeeReceiptList.forEach(studentFeeReceipt -> {
            FeeReceiptsDTO feeReceiptsDTO = new FeeReceiptsDTO();
            feeReceiptsDTO.setUsername(studentFeeReceipt.getUsername());
            feeReceiptsDTO.setSession(studentFeeReceipt.getSession());
            feeReceiptsDTO.setReceiptNo(studentFeeReceipt.getReceiptNo());
            feeReceiptsDTO.setDate(studentFeeReceipt.getDate());
            feeReceiptsDTO.setSchedules(studentFeeReceipt.getSchedules());
            feeReceiptsDTO.setMode(studentFeeReceipt.getMode());
            feeReceiptsDTO.setTotal(studentFeeReceipt.getTotal());
            feeReceiptsDTO.setTotalLateConcSchlr(studentFeeReceipt.getTotalLateConcSchlr());
            feeReceiptsDTO.setLateFeeCharges(studentFeeReceipt.getLateFeeCharges());
            feeReceiptsDTO.setConcession(studentFeeReceipt.getConcession());
            feeReceiptsDTO.setConcessionSplit(studentFeeReceipt.getConcessionSplit());
            feeReceiptsDTO.setScholarship(studentFeeReceipt.getScholarship());
            feeReceiptsDTO.setRemarks(studentFeeReceipt.getRemarks());
            feeReceiptsDTO.setCollectedBy(studentFeeReceipt.getCollectedBy());
            //feeReceiptsDTO.setStudentFeeReceipt(studentFeeReceipt);
            if (studentFeeReceipt.getUsername() != null && studentFeeReceipt.getUsername().contains(AppConstant.STUDENT_ROLE)) {
                Optional<Student> studentOptional = studentRepository.findByUsername(studentFeeReceipt.getUsername());
                Student student = studentOptional.orElseThrow(() -> new RuntimeException("Student Username: " + studentFeeReceipt.getUsername() + " Not Found"));
                feeReceiptsDTO.setFullName(student.getFullName());
                feeReceiptsDTO.setClassName(student.getClassName());

                StudentFee studentFeeDB = studentFeeRepository.findByStudentIdAndSession(studentId, session);
                if (studentFeeDB != null) {
                    Map<String, Double> schedulesBifurcated =
                            getSchedulesBifurcated(
                                    studentFeeReceipt.getSchedules(),
                                    studentFeeDB.getSchoolMonthlyFees(),
                                    studentFeeDB.getSchoolMiscFees()
                            );

                    feeReceiptsDTO.setSchedulesBifurcated(schedulesBifurcated);
                }
            }
            feeReceiptsDTOList.add(feeReceiptsDTO);
        });
        return new ResponseEntity<>(feeReceiptsDTOList, HttpStatus.OK);
    }

    private Map<String, Double> getSchedulesBifurcated(
            String schedules,
            Map<String, Double> schoolMonthlyFees,
            Map<String, Double> schoolMiscFees) {

        Map<String, Double> schedulesBifurcated = new LinkedHashMap<>();

        if (schedules == null || schedules.isEmpty()) {
            return schedulesBifurcated;
        }

        String[] scheduleArray = schedules.split(",");

        for (String schedule : scheduleArray) {

            String trimmedSchedule = schedule.trim();

            // check in monthly fees
            if (schoolMonthlyFees != null &&
                    schoolMonthlyFees.containsKey(trimmedSchedule)) {

                schedulesBifurcated.put(
                        trimmedSchedule,
                        schoolMonthlyFees.get(trimmedSchedule)
                );
            }

            // check in misc fees
            else if (schoolMiscFees != null &&
                    schoolMiscFees.containsKey(trimmedSchedule)) {

                schedulesBifurcated.put(
                        trimmedSchedule,
                        schoolMiscFees.get(trimmedSchedule)
                );
            }
        }

        return schedulesBifurcated;
    }

    public ResponseEntity<List<StudentFeeReceipt>> getSchoolFeeReceipts() {
        List<StudentFeeReceipt> studentFeeReceipts = (List<StudentFeeReceipt>) studentFeeReceiptRepository.findAll();
        return new ResponseEntity<>(studentFeeReceipts, HttpStatus.OK);
    }

    public ResponseEntity<List<FeeReceiptsDTO>> getSchoolFeeReceiptsBetweenDates(LocalDate startDate, LocalDate endDate, String session) {
        List<FeeReceiptsDTO> feeReceiptsDTOList = new ArrayList<>();
        List<StudentFeeReceipt> studentFeeReceiptList = studentFeeReceiptRepository.findByDateBetweenAndSessionOrderByTimestampDesc(startDate, endDate, session);
        studentFeeReceiptList.forEach(studentFeeReceipt -> {
            FeeReceiptsDTO feeReceiptsDTO = new FeeReceiptsDTO();
            feeReceiptsDTO.setUsername(studentFeeReceipt.getUsername());
            feeReceiptsDTO.setSession(studentFeeReceipt.getSession());
            feeReceiptsDTO.setReceiptNo(studentFeeReceipt.getReceiptNo());
            feeReceiptsDTO.setDate(studentFeeReceipt.getDate());
            feeReceiptsDTO.setSchedules(studentFeeReceipt.getSchedules());
            feeReceiptsDTO.setMode(studentFeeReceipt.getMode());
            feeReceiptsDTO.setTotal(studentFeeReceipt.getTotal());
            feeReceiptsDTO.setTotalLateConcSchlr(studentFeeReceipt.getTotalLateConcSchlr());
            feeReceiptsDTO.setLateFeeCharges(studentFeeReceipt.getLateFeeCharges());
            feeReceiptsDTO.setConcession(studentFeeReceipt.getConcession());
            feeReceiptsDTO.setScholarship(studentFeeReceipt.getScholarship());
            feeReceiptsDTO.setRemarks(studentFeeReceipt.getRemarks());
            feeReceiptsDTO.setCollectedBy(studentFeeReceipt.getCollectedBy());
            //feeReceiptsDTO.setStudentFeeReceipt(studentFeeReceipt);
            if (studentFeeReceipt.getUsername() != null && studentFeeReceipt.getUsername().contains(AppConstant.STUDENT_ROLE)) {
                Optional<Student> studentOptional = studentRepository.findByUsername(studentFeeReceipt.getUsername());
                Optional<Student> studentOptionalDeactivated = studentRepository.findByUsernameDeactivatedStatus(studentFeeReceipt.getUsername());
                Student student = null;
                if (studentOptional.isPresent()) {
                    student = studentOptional.get();
                } else if (studentOptionalDeactivated.isPresent()) {
                    student = studentOptionalDeactivated.get();
                } else {
                    throw new RuntimeException("Student Username: " + studentFeeReceipt.getUsername() + " Not Found");
                }
                //Student student = studentOptional.orElseThrow(() -> new RuntimeException("Student Username: " + studentFeeReceipt.getUsername() + " Not Found"));
                feeReceiptsDTO.setFullName(student.getFullName());
                feeReceiptsDTO.setFatherName(student.getFatherName());
                feeReceiptsDTO.setFeeType(FEE_TYPE_LIST.get(0));
                feeReceiptsDTO.setClassName(student.getClassName());
            } else if (studentFeeReceipt.getUsername() != null && studentFeeReceipt.getUsername().contains(AppConstant.PARENT_ROLE)) {
                Optional<Parent> parentOptional = parentRepository.findByUsername(studentFeeReceipt.getUsername());
                Parent parent = parentOptional.orElseThrow(() -> new RuntimeException("Parent Username: " + studentFeeReceipt.getUsername() + " Not Found"));
                feeReceiptsDTO.setFullName(parent.getFullName());
                feeReceiptsDTO.setFeeType(FEE_TYPE_LIST.get(1));
            }
            feeReceiptsDTOList.add(feeReceiptsDTO);
        });
        return new ResponseEntity<>(feeReceiptsDTOList, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<String> saveSchoolFeeReceipts(List<StudentFeeReceipt> studentFeeReceipts) {
        studentFeeReceipts.stream().forEach(studentFeeReceipt -> {

            Double totalLateConcessionScholarship = studentFeeReceipt.getTotal() +
                    (studentFeeReceipt.getLateFeeCharges() != null ? studentFeeReceipt.getLateFeeCharges() : 0) -
                    (studentFeeReceipt.getConcession() != null ? studentFeeReceipt.getConcession() : 0) -
                    (studentFeeReceipt.getScholarship() != null ? studentFeeReceipt.getScholarship() : 0);

            studentFeeReceipt.setTotalLateConcSchlr(totalLateConcessionScholarship);
            String receiptNo = sessionSequenceUtility.generateSessionSequence(AppConstant.FEE_TYPE_SEQUENCE);
            studentFeeReceipt.setReceiptNo(receiptNo);
            studentFeeReceipt.setTimestamp(DateUtility.getCurrentTimeStamp());
        });
        studentFeeReceiptRepository.saveAll(studentFeeReceipts);
        return ResponseEntity.ok("Payment Receipt Data Saved");
    }

    /////////////////////// deleteStudentFeeReceipt - to delete the receipt fees by receiptNo ///////////////////////////////
    @Transactional
    public void deleteStudentFeeReceipt(String receiptNo, String username, String session, String deletedBy) {

        // Fetch receipt
        StudentFeeReceipt receipt = studentFeeReceiptRepository
                .findByReceiptNo(receiptNo)
                .orElseThrow(() -> new RuntimeException("Receipt not found: " + receiptNo));

        // Guard: already deleted
        if (receipt.getRemarks() != null && receipt.getRemarks().contains("_deleted")) {
            throw new RuntimeException("Receipt " + receiptNo + " is already deleted.");
        }

        // Fetch studentFee
        StudentFee studentFee = studentFeeRepository.findByStudentIdAndSession(username, session);
        if (studentFee == null) {
            throw new RuntimeException("Student fee not found for: " + username + " session: " + session);
        }

        // Parse schedules, eg of receiptSchedules = ["june_1", "july_1", "august_1"];
        String[] receiptSchedules = receipt.getSchedules() != null
                ? receipt.getSchedules().split(",")
                : new String[0];

        // Compute schedulesBifurcated the same way as we compute in getSchoolFeeReceiptsByStudentId
        // because schedulesBifurcated is not stored in receipt table, it derived using StudentFee monthly/misc maps
        // eg of schedulesBifurcated : { "june_1" : 1100.0, "july_1" : 1100.0, "august_1" : 1100.0 }
        Map<String, Double> schedulesBifurcated = getSchedulesBifurcated(
                receipt.getSchedules(),
                studentFee.getSchoolMonthlyFees(),
                studentFee.getSchoolMiscFees()
        );

        // get concessionSplit
        Map<String, Double> concessionSplit = receipt.getConcessionSplit() != null
                ? receipt.getConcessionSplit()
                : new HashMap<>();


        // Fetch StudentFeeStats
        StudentFeeStats studentFeeStats = studentFeeStatsRepository.findBySession(session)
                .orElseThrow(() -> new RuntimeException("Fee stats not found for session: " + session));

        // ── BRANCH: Scholarship receipt vs normal receipt ──
        boolean isScholarshipReceipt = receipt.getRemarks() != null
                && receipt.getRemarks().toLowerCase().contains("scholarship");

        if (isScholarshipReceipt) {
            updateStudentFeeOnDeleteScholarship(studentFee, receiptSchedules, schedulesBifurcated, receipt);
            updateStudentFeeStatsOnDeleteScholarship(studentFeeStats, receipt);
        } else {
            // Update StudentFee
            updateStudentFeeOnDelete(studentFee, receiptSchedules, schedulesBifurcated,
                    receipt.getLateFeeCharges(), receipt.getConcession());

            // Update StudentFeeStats
            updateStudentFeeStatsOnDelete(studentFeeStats, receiptSchedules, schedulesBifurcated,
                    concessionSplit, receipt);
        }


        // Mark receipt as deleted in remarks
        // eg, Paid via UPI_deleted by john on 26-06-2026
        String deletedRemark = "_deleted" +
                (deletedBy != null && !deletedBy.isBlank() ? " by " + deletedBy : "") +
                " on " + DateUtility.getCurrentDate();

        receipt.setRemarks(
                (receipt.getRemarks() != null && !receipt.getRemarks().isBlank()
                        ? receipt.getRemarks()
                        : "") + deletedRemark
        );

        // Save all
        studentFeeReceiptRepository.save(receipt);
        studentFeeRepository.save(studentFee);
        studentFeeStatsRepository.save(studentFeeStats);
    }

    // Updating StudentFee Table for Scholarship deletion
    private void updateStudentFeeOnDeleteScholarship(
            StudentFee studentFee,
            String[] receiptSchedules,
            Map<String, Double> schedulesBifurcated,
            StudentFeeReceipt receipt) {

        // Get Monthly Fees (scholarship only applies to monthly fees)
        Map<String, Double> monthlyFees = studentFee.getSchoolMonthlyFees() != null
                ? studentFee.getSchoolMonthlyFees()
                : new HashMap<>();

        // Loop Through Receipt Schedules — remove only from monthly fees
        for (String key : receiptSchedules) {
            key = key.trim();
            if (monthlyFees.containsKey(key)) {
                monthlyFees.remove(key);
            }
        }

        studentFee.setSchoolMonthlyFees(monthlyFees);
        // schoolMiscFees not touched at all

        double receiptTotal = receipt.getTotal() == null ? 0 : receipt.getTotal();

        // ── scholarship: scholarship - receiptTotal ──
        double newScholarship = Math.max(
                (studentFee.getScholarship() == null ? 0 : studentFee.getScholarship()) - receiptTotal,
                0
        );
        studentFee.setScholarship(newScholarship);

        // ── totalLateConcSchlr: add back receiptTotal(scholarship paid amount)
        // (since totalLateConcSchlr is total + lateFee - Concession - Scholarship)
        double currentTotalLateConcSchlr = studentFee.getTotalLateConcSchlr() == null
                ? 0 : studentFee.getTotalLateConcSchlr();
        studentFee.setTotalLateConcSchlr(currentTotalLateConcSchlr + receiptTotal);

        // ── outstanding: add back receiptTotal ──
        double newOutstanding = (studentFee.getOutstanding() == null ? 0 : studentFee.getOutstanding())
                + receiptTotal;
        studentFee.setOutstanding(newOutstanding);
    }

    // Updating StudentFeeStats Table for Scholarship deletion
    private void updateStudentFeeStatsOnDeleteScholarship(
            StudentFeeStats studentFeeStats,
            StudentFeeReceipt receipt) {

        double receiptTotal = receipt.getTotal() == null ? 0 : receipt.getTotal();

        // ── tuitionFee: tuitionFee - receiptTotal ──
        studentFeeStats.setTuitionFee(Math.max(
                (studentFeeStats.getTuitionFee() == null ? 0 : studentFeeStats.getTuitionFee()) - receiptTotal, 0));

        // ── tuitionScholarship: tuitionScholarship - receiptTotal ──
        studentFeeStats.setTuitionScholarship(Math.max(
                (studentFeeStats.getTuitionScholarship() == null ? 0 : studentFeeStats.getTuitionScholarship()) - receiptTotal, 0));
    }

    // Updating StudentFee Table
    private void updateStudentFeeOnDelete(
            StudentFee studentFee,
            String[] receiptSchedules,
            Map<String, Double> schedulesBifurcated,
            Double receiptLateFee,
            Double receiptConcession) {

        // Get Monthly Fees
        Map<String, Double> monthlyFees = studentFee.getSchoolMonthlyFees() != null
                ? studentFee.getSchoolMonthlyFees()
                : new HashMap<>();

        // Get Misc Fees
        Map<String, Double> miscFees = studentFee.getSchoolMiscFees() != null
                ? studentFee.getSchoolMiscFees()
                : new HashMap<>();

        // Loop Through Receipt Schedules
        for (String key : receiptSchedules) {
            key = key.trim();

            // Remove Paid Monthly/Misc Fee from StudentFee Table
            if (monthlyFees.containsKey(key)) {
                // ── Remove from schoolMonthlyFees ──
                monthlyFees.remove(key);

            } else if (miscFees.containsKey(key)) {
                // ── Remove from schoolMiscFees ──
                miscFees.remove(key);
            }
        }

        // Save Updated Maps
        studentFee.setSchoolMonthlyFees(monthlyFees);
        studentFee.setSchoolMiscFees(miscFees);

        // ── lateFeeCharges: lateFeeCharges - receipt's lateFee ──
        double newLateFee = Math.max(
                (studentFee.getLateFeeCharges() == null ? 0 : studentFee.getLateFeeCharges())
                        - (receiptLateFee == null ? 0 : receiptLateFee),
                0
        );
        studentFee.setLateFeeCharges(newLateFee);

        // ── concession: concession - receipt's concession ──
        double newConcession = Math.max(
                (studentFee.getConcession() == null ? 0 : studentFee.getConcession())
                        - (receiptConcession == null ? 0 : receiptConcession),
                0
        );
        studentFee.setConcession(newConcession);

        // ── totalLateConcSchlr = total + lateFeeCharges - concession - scholarship ──
        double total = studentFee.getTotal() == null ? 0 : studentFee.getTotal();
        double scholarship = studentFee.getScholarship() == null ? 0 : studentFee.getScholarship();
        studentFee.setTotalLateConcSchlr(total + newLateFee - newConcession - scholarship);

        // ── outstanding: add back the receipt's paid amount (schedulesBifurcated sum) ──

        // eg schedulesBifurcated : { "june_1" : 1100.0, "july_1" : 1100.0, "august_1" : 1100.0 }
        // receiptPaidAmount = 1100.0 + 1100.0 + 1100.0 = 3300.0
        double receiptPaidAmount = schedulesBifurcated.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        double newOutstanding = (studentFee.getOutstanding() == null ? 0 : studentFee.getOutstanding())
                + receiptPaidAmount;
        studentFee.setOutstanding(newOutstanding);
    }

    private void updateStudentFeeStatsOnDelete(
            StudentFeeStats studentFeeStats,
            String[] receiptSchedules,
            Map<String, Double> schedulesBifurcated,
            Map<String, Double> concessionSplit,
            StudentFeeReceipt receipt) {

        // ── Identify which schedules are monthly vs misc ──────────────────

        // Create Amount Counters
        double tuitionAmountToRemove = 0.0;
        double admissionAmountToRemove = 0.0;
        double admissionConcessionToRemove = 0.0;
        double registrationAmountToRemove = 0.0;
        double registrationConcessionToRemove = 0.0;
        double annualAmountToRemove = 0.0;
        double annualConcessionToRemove = 0.0;

        // Loop Through Schedules
        for (String receiptSchedule : receiptSchedules) {
            receiptSchedule = receiptSchedule.trim(); // receiptSchedule = "june_1"

            // Get Amount
            Double amount = schedulesBifurcated.getOrDefault(receiptSchedule, 0.0);

            // Create Base Key eg, "june_1" → "june", "admissionFee_1" → "admissionfee"
            String baseKey = receiptSchedule.replaceAll("_\\d+$", "").toLowerCase();

            if (AppConstant.MONTH_NAMES_LIST_FEE.contains(baseKey)) {
                // Monthly fee → goes to tuitionFee eg, "june" their amount will add in tuitionAmountToRemove
                tuitionAmountToRemove += amount;

            } else if (baseKey.equals("admissionfee")) {
                // Admission Fee
                admissionAmountToRemove += amount;

                // if concessionSplit have "admissionFee_1" : 500 then this will add in admissionConcessionToRemove
                admissionConcessionToRemove += concessionSplit.getOrDefault(receiptSchedule, 0.0);

            } else if (baseKey.equals("registrationfee")) {
                registrationAmountToRemove += amount;
                registrationConcessionToRemove += concessionSplit.getOrDefault(receiptSchedule, 0.0);

            } else if (baseKey.equals("annualfee")) {
                annualAmountToRemove += amount;
                annualConcessionToRemove += concessionSplit.getOrDefault(receiptSchedule, 0.0);

            } else {
                // otherFeeType (examfee, computerfee etc.)
                removeFromOtherFeeType(studentFeeStats, receiptSchedule, amount, concessionSplit.getOrDefault(receiptSchedule, 0.0));
            }
        }

        // tuitionFee = tuitionFee - tuitionAmountToRemove ────────────────────────────────────────────────────
        studentFeeStats.setTuitionFee(Math.max(
                (studentFeeStats.getTuitionFee() == null ? 0 : studentFeeStats.getTuitionFee()) - tuitionAmountToRemove, 0));


        // tuitionLateFee =  tuitionLateFee - LateFeeCharges ────────────────────────────────────────────────
        double receiptLateFee = receipt.getLateFeeCharges() == null ? 0 : receipt.getLateFeeCharges();
        studentFeeStats.setTuitionLateFee(Math.max(
                (studentFeeStats.getTuitionLateFee() == null ? 0 : studentFeeStats.getTuitionLateFee()) - receiptLateFee, 0));

        // Calculate Tuition Concession to remove
        // filter monthly keys or "lateFee" from concessionSplit and then sum their values to get tuitionConcessionToRemove
        double tuitionConcessionToRemove = concessionSplit.entrySet().stream()
                .filter(e -> {
                    String baseKey = e.getKey().replaceAll("_\\d+$", "").toLowerCase();
                    return AppConstant.MONTH_NAMES_LIST_FEE.contains(baseKey) || baseKey.equals("latefee");
                })
                .mapToDouble(Map.Entry::getValue)
                .sum();

        studentFeeStats.setTuitionConcession(Math.max(
                (studentFeeStats.getTuitionConcession() == null ? 0 : studentFeeStats.getTuitionConcession())
                        - tuitionConcessionToRemove, 0));

        // ── admissionFee ──────────────────────────────────────────────────
        studentFeeStats.setAdmissionFee(Math.max(
                (studentFeeStats.getAdmissionFee() == null ? 0 : studentFeeStats.getAdmissionFee()) - admissionAmountToRemove, 0));
        studentFeeStats.setAdmissionFeeConcession(Math.max(
                (studentFeeStats.getAdmissionFeeConcession() == null ? 0 : studentFeeStats.getAdmissionFeeConcession())
                        - admissionConcessionToRemove, 0));

        // ── registrationFee ───────────────────────────────────────────────
        studentFeeStats.setRegistrationFee(Math.max(
                (studentFeeStats.getRegistrationFee() == null ? 0 : studentFeeStats.getRegistrationFee())
                        - registrationAmountToRemove, 0));
        studentFeeStats.setRegistrationFeeConcession(Math.max(
                (studentFeeStats.getRegistrationFeeConcession() == null ? 0 : studentFeeStats.getRegistrationFeeConcession())
                        - registrationConcessionToRemove, 0));

        // ── annualFee ─────────────────────────────────────────────────────
        studentFeeStats.setAnnualFee(Math.max(
                (studentFeeStats.getAnnualFee() == null ? 0 : studentFeeStats.getAnnualFee()) - annualAmountToRemove, 0));
        studentFeeStats.setAnnualFeeConcession(Math.max(
                (studentFeeStats.getAnnualFeeConcession() == null ? 0 : studentFeeStats.getAnnualFeeConcession())
                        - annualConcessionToRemove, 0));
    }

    private void removeFromOtherFeeType(
            StudentFeeStats studentFeeStats,
            String receiptSchedule,
            Double amountToRemove,
            Double concessionToRemove) {

        if (studentFeeStats.getOtherFeeType() == null || studentFeeStats.getOtherFeeType().isEmpty()) return;

        // Create baseKey,  receiptSchedule = "computerfeeapril_1" → baseKey = "computerfee"
        String baseKey = receiptSchedule.replaceAll("_\\d+$", "").toLowerCase();

        // Find which supported fee type this belongs to
        String matchedFeeType = OTHER_FEE_SUPPORT_IN_FEES.stream()
                .filter(feeType -> baseKey.contains(feeType.toLowerCase())) //eg,"computerfeeapril".contains("computerfee"), So matchedFeeType = computerfee
                .findFirst()
                .orElse(null);

        if (matchedFeeType == null) return;

        List<Map<String, Double>> otherFeeList = studentFeeStats.getOtherFeeType();

        // Find the map for this fee type
        // eg, map { "computerfee": 29100, "computerfeeConcession": 11350 } , it Contains matchedFeeType i.e computerfee
        Map<String, Double> feeMap = otherFeeList.stream()
                .filter(map -> map.containsKey(matchedFeeType)
                        || map.containsKey(matchedFeeType + "Concession"))
                .findFirst()
                .orElse(null);

        if (feeMap != null) {
            // Subtract amount
            double existingFeeAmount = feeMap.getOrDefault(matchedFeeType, 0.0);
            feeMap.put(matchedFeeType, Math.max(existingFeeAmount - amountToRemove, 0.0));

            // Subtract concession
            if (concessionToRemove != null && concessionToRemove > 0) {
                double existingConcession = feeMap.getOrDefault(matchedFeeType + "Concession", 0.0);
                feeMap.put(matchedFeeType + "Concession",
                        Math.max(existingConcession - concessionToRemove, 0.0));
            }
        }
    }

    ///////////////////// STUDENT FEE STATS///////////////////////////////

    public ResponseEntity<StudentFeeStats> getStudentFeeStats(String session) {
        StudentFeeStats studentFeeStats = studentFeeStatsRepository
                .findBySession(session)
                .orElseThrow(() -> new RuntimeException("Student Fee Stats Not Found for session: " + session));

        return ResponseEntity.ok(studentFeeStats);
    }

    public ResponseEntity<StudentCombineFeeStats> getStudentCombineFeeStats(String session) {
        StudentCombineFeeStats studentCombineFeeStats = studentCombineFeeStatsRepository
                .findBySession(session)
                .orElseThrow(() -> new RuntimeException("Student Combine Fee Stats Not Found for session: " + session));

        return ResponseEntity.ok(studentCombineFeeStats);
    }

    /////////////////////  STUDENT BUS FEE///////////////////////////////
    public ResponseEntity<StudentBusFee> getStudentBusFee(String studentId, String session) {
        StudentBusFee studentBusFee = studentBusFeeRepository.findByStudentIdAndSession(studentId, session);
        if (studentBusFee == null) {
            throw new PaymentNotFoundException("Student Bus Payment Not Found");
        }
        return new ResponseEntity<>(studentBusFee, HttpStatus.OK);
    }

    public ResponseEntity<List<StudentBusFee>> getStudentBusFees() {
        List<StudentBusFee> studentBusFees = (List<StudentBusFee>) studentBusFeeRepository.findAll();
        return new ResponseEntity<>(studentBusFees, HttpStatus.OK);
    }

    public ResponseEntity<String> saveStudentBusFees(List<StudentBusFeeDTO> studentBusFeesDTO) {
        List<StudentBusFee> studentBusFeeList = new ArrayList<>();
        studentBusFeesDTO.stream().forEach(studentBusFeeDTO -> {
            StudentBusFee studentBusFee = studentBusFeeDTO.getStudentBusFee();
            StudentBusFee studentBusFeeDB = studentBusFeeRepository.findByStudentIdAndSession(studentBusFee.getUsername(), studentBusFee.getSession());
            if (studentBusFeeDB == null) {
                //validating student username exists
                Optional<Student> studentOptional = studentRepository.findByUsername(studentBusFee.getUsername());
                Student student = studentOptional.orElseThrow(() -> new StudentNotFoundException("Student: " + studentBusFee.getUsername() + " Not Found"));
                //validating busRoute name
                Optional<BusCircular> busCircularOptional = busCircularRepository.findByBusRouteAndSession(studentBusFeeDTO.getBusRoute(), studentBusFeeDTO.getStudentBusFee().getSession());
                BusCircular busCircular = busCircularOptional.orElseThrow(() -> new CircularNotFoundException("Bus Circular: " + studentBusFeeDTO.getBusRoute() + " Not Found"));
                //updating student bus route
                if (student.getBusRoute() == null)
                    studentRepository.updateStudentBusRoute(studentBusFee.getUsername(), studentBusFeeDTO.getBusRoute());
                //process bus fee data
                processStudentBusFeeDataForSaving(studentBusFee, studentBusFeeList, busCircular);
            } else {
                throw new RuntimeException("Student: " + studentBusFee.getUsername() + " Bus fee already Present. Aborting Save");
            }
        });
        studentBusFeeRepository.saveAll(studentBusFeeList);
        return ResponseEntity.ok("Student Bus Fee Data Saved");
    }

    private void processStudentBusFeeDataForSaving(StudentBusFee studentBusFee, List<StudentBusFee> studentBusFeeList, BusCircular busCircular) {
        Map<String, Double> busMonthlyFeesInBusCircular = busCircular.getBusMonthlyFees();
        double totalFees = sumMapValues(busMonthlyFeesInBusCircular);

        studentBusFee.setTotal(totalFees);
        studentBusFee.setTotalLateConcession(totalFees);
        studentBusFee.setOutstanding(totalFees);
        studentBusFee.setTimestamp(DateUtility.getCurrentTimeStamp());
        studentBusFeeList.add(studentBusFee);
    }

    /////////////////////// deleteStudentBusFeeReceipt - to delete the bus fee receipt by receiptNo ///////////////////////////////
    @Transactional
    public void deleteStudentBusFeeReceipt(String receiptNo, String username, String session, String deletedBy) {

        // Fetch receipt
        StudentBusFeeReceipt receipt = studentBusFeeReceiptRepository
                .findByReceiptNo(receiptNo)
                .orElseThrow(() -> new RuntimeException("Bus Fee Receipt not found: " + receiptNo));

        // Guard: already deleted
        if (receipt.getRemarks() != null && receipt.getRemarks().contains("_deleted")) {
            throw new RuntimeException("Bus Fee Receipt " + receiptNo + " is already deleted.");
        }

        // Fetch studentBusFee
        StudentBusFee studentBusFee = studentBusFeeRepository.findByStudentIdAndSession(username, session);
        if (studentBusFee == null) {
            throw new RuntimeException("Student bus fee not found for: " + username + " session: " + session);
        }

        // Parse schedules, eg of receiptSchedules = ["june_1", "july_1", "august_1"];
        String[] receiptSchedules = receipt.getSchedules() != null
                ? receipt.getSchedules().split(",")
                : new String[0];

        // Compute schedulesBifurcated the same way as we compute in getBusFeeReceiptsByStudentId
        // because schedulesBifurcated is not stored in receipt table, it derived using StudentBusFee monthly map
        Map<String, Double> schedulesBifurcated = getSchedulesBifurcatedBusFee(
                receipt.getSchedules(),
                studentBusFee.getBusMonthlyFees()
        );

        // Update StudentBusFee
        updateStudentBusFeeOnDelete(studentBusFee, receiptSchedules, schedulesBifurcated,
                receipt.getLateFeeCharges(), receipt.getConcession());

        // Mark receipt as deleted in remarks
        // eg, Paid via UPI_deleted by john on 26-06-2026
        String deletedRemark = "_deleted" +
                (deletedBy != null && !deletedBy.isBlank() ? " by " + deletedBy : "") +
                " on " + DateUtility.getCurrentDate();

        receipt.setRemarks(
                (receipt.getRemarks() != null && !receipt.getRemarks().isBlank()
                        ? receipt.getRemarks()
                        : "") + deletedRemark
        );

        // Save all
        studentBusFeeReceiptRepository.save(receipt);
        studentBusFeeRepository.save(studentBusFee);
    }

    // Updating StudentBusFee Table
    private void updateStudentBusFeeOnDelete(
            StudentBusFee studentBusFee,
            String[] receiptSchedules,
            Map<String, Double> schedulesBifurcated,
            Double receiptLateFee,
            Double receiptConcession) {

        // Get Monthly Fees
        Map<String, Double> monthlyFees = studentBusFee.getBusMonthlyFees() != null
                ? studentBusFee.getBusMonthlyFees()
                : new HashMap<>();

        // Loop Through Receipt Schedules — remove paid schedule from monthly fees
        for (String key : receiptSchedules) {
            key = key.trim();
            if (monthlyFees.containsKey(key)) {
                monthlyFees.remove(key);
            }
        }

        studentBusFee.setBusMonthlyFees(monthlyFees);

        // ── lateFeeCharges: lateFeeCharges - receipt's lateFee ──
        double newLateFee = Math.max(
                (studentBusFee.getLateFeeCharges() == null ? 0 : studentBusFee.getLateFeeCharges())
                        - (receiptLateFee == null ? 0 : receiptLateFee),
                0
        );
        studentBusFee.setLateFeeCharges(newLateFee);

        // ── concession: concession - receipt's concession ──
        double newConcession = Math.max(
                (studentBusFee.getConcession() == null ? 0 : studentBusFee.getConcession())
                        - (receiptConcession == null ? 0 : receiptConcession),
                0
        );
        studentBusFee.setConcession(newConcession);

        // ── totalLateConcession = total + lateFeeCharges - concession ──
        double total = studentBusFee.getTotal() == null ? 0 : studentBusFee.getTotal();
        studentBusFee.setTotalLateConcession(total + newLateFee - newConcession);

        // ── outstanding: add back the receipt's paid amount (schedulesBifurcated sum) ──
        double receiptPaidAmount = schedulesBifurcated.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        double newOutstanding = (studentBusFee.getOutstanding() == null ? 0 : studentBusFee.getOutstanding())
                + receiptPaidAmount;
        studentBusFee.setOutstanding(newOutstanding);
    }

    public ResponseEntity<String> updateStudentBusFees(List<StudentBusFeeDTO> studentBusFeesDTOList) {
        List<StudentBusFee> studentBusFeeList = new ArrayList<>();
        List<StudentBusFeeReceipt> studentBusFeeReceiptList = new ArrayList<>();
        studentBusFeesDTOList.stream().forEach(studentBusFeeDTO -> {
            StudentBusFee studentBusFee = studentBusFeeDTO.getStudentBusFee();
            StudentBusFee studentBusFeeDB = studentBusFeeRepository.findByStudentIdAndSession(studentBusFee.getUsername(), studentBusFee.getSession());
            if (studentBusFeeDB != null) {
                //validating bus Circular name
                Optional<BusCircular> busCircularOptional = busCircularRepository.findByBusRouteAndSession(studentBusFeeDTO.getBusRoute(), studentBusFeeDTO.getStudentBusFee().getSession());
                BusCircular busCircular = busCircularOptional.orElseThrow(() -> new CircularNotFoundException("Bus Circular Not Found"));
                //now process data
                processStudentBusFeeDataForUpdate(studentBusFee, studentBusFeeDB, busCircular, studentBusFeeList);
                //prepare fee receipt data for saving
                StudentBusFeeReceipt studentBusFeeReceipt = studentBusFeeDTO.getStudentBusFeeReceipt();
                studentBusFeeReceiptList.add(studentBusFeeReceipt);
            } else {
                throw new RuntimeException("Student: " + studentBusFee.getUsername() + " Bus fee data not found. Aborting Update");
            }
        });
        studentBusFeeRepository.saveAll(studentBusFeeList);
        saveBusFeeReceipts(studentBusFeeReceiptList);
        //studentBusFeeReceiptRepository.saveAll(studentBusFeeReceiptList);
        return ResponseEntity.ok("Student Bus Fee Data Saved");
    }

    public void processStudentBusFeeDataForUpdate(StudentBusFee studentBusFee, StudentBusFee studentBusFeeDB, BusCircular busCircular, List<StudentBusFee> studentBusFeeList) {

        Map<String, Double> busMonthlyFeesInCircular = busCircular.getBusMonthlyFees();

        Map<String, Double> busMonthlyFeesFromDB = studentBusFeeDB.getBusMonthlyFees();

        Map<String, Double> busMonthlyFeesFromUI = studentBusFee.getBusMonthlyFees();

        //filtering fees as UI might send already submitted fees
        Map<String, Double> filteredMonthlyUI =
                filterAlreadyPaidFees(
                        busMonthlyFeesFromUI,
                        busMonthlyFeesFromDB
                );

        //if there is already any late & concession fee in db it will be merged with total
        double totalFees = sumMapValues(busMonthlyFeesInCircular);

        double paidFeesInDB = sumMapValues(busMonthlyFeesFromDB);

        double paidFeesInUI = sumMapValues(filteredMonthlyUI);

        double totalLateConcession = totalFees +
                ((studentBusFeeDB.getLateFeeCharges() != null ? studentBusFeeDB.getLateFeeCharges() : 0) -
                        (studentBusFeeDB.getConcession() != null ? studentBusFeeDB.getConcession() : 0)) +
                ((studentBusFee.getLateFeeCharges() != null ? studentBusFee.getLateFeeCharges() : 0) -
                        (studentBusFee.getConcession() != null ? studentBusFee.getConcession() : 0));

        double outstandingFees = totalFees - paidFeesInDB - paidFeesInUI;

        studentBusFee.setTotal(totalFees);
        studentBusFee.setTotalLateConcession(totalLateConcession);
        studentBusFee.setOutstanding(outstandingFees);

        //handling late fee with db late fee
        if (studentBusFeeDB.getLateFeeCharges() != null) {
            if (studentBusFee.getLateFeeCharges() != null) {
                studentBusFee.setLateFeeCharges(studentBusFee.getLateFeeCharges() + studentBusFeeDB.getLateFeeCharges());
            } else
                studentBusFee.setLateFeeCharges(studentBusFeeDB.getLateFeeCharges());
        }
        //handling concession with db concession
        if (studentBusFeeDB.getConcession() != null) {
            if (studentBusFee.getConcession() != null) {
                studentBusFee.setConcession(studentBusFee.getConcession() + studentBusFeeDB.getConcession());
            } else
                studentBusFee.setConcession(studentBusFeeDB.getConcession());
        }


        //logic to add monthlyfee in db if ui also sending monthly fee then adding it with db fees
        if (busMonthlyFeesFromDB != null && filteredMonthlyUI != null) {
            Map<String, Double> merged = new LinkedHashMap<>();
            merged.putAll(busMonthlyFeesFromDB);
            merged.putAll(filteredMonthlyUI);
            studentBusFee.setBusMonthlyFees(merged);
        }


        studentBusFee.setId(studentBusFeeDB.getId());
        studentBusFee.setTimestamp(DateUtility.getCurrentTimeStamp());
        studentBusFeeList.add(studentBusFee);
    }

    ///////////////////// PAYMENT STUDENT BUS///////////////////////////////

//    public ResponseEntity<List<StudentBusFeeReceipt>> getBusFeeReceiptsByStudentId(String studentId, String session) {
//        List<StudentBusFeeReceipt> studentBusFeeReceiptList = studentBusFeeReceiptRepository.findByUsernameAndSessionOrderByReceiptNoAsc(studentId, session);
//        return new ResponseEntity<>(studentBusFeeReceiptList, HttpStatus.OK);
//    }
    private Map<String, Double> getSchedulesBifurcatedBusFee(
            String schedules,
            Map<String, Double> schoolMonthlyFees) {

        Map<String, Double> schedulesBifurcated = new LinkedHashMap<>();

        if (schedules == null || schedules.isEmpty()) {
            return schedulesBifurcated;
        }

        String[] scheduleArray = schedules.split(",");

        for (String schedule : scheduleArray) {

            String trimmedSchedule = schedule.trim();

            // check in monthly fees
            if (schoolMonthlyFees != null &&
                    schoolMonthlyFees.containsKey(trimmedSchedule)) {

                schedulesBifurcated.put(
                        trimmedSchedule,
                        schoolMonthlyFees.get(trimmedSchedule)
                );
            }
        }

        return schedulesBifurcated;
    }

    public ResponseEntity<List<BusFeeReceiptsDTO>> getBusFeeReceiptsByStudentId(String studentId, String session) {
        List<BusFeeReceiptsDTO> busFeeReceiptsDTOList = new ArrayList<>();
        List<StudentBusFeeReceipt> studentBusFeeReceiptList =
                studentBusFeeReceiptRepository.findByUsernameAndSessionOrderByReceiptNoDesc(studentId, session);
        studentBusFeeReceiptList.forEach(studentBusFeeReceipt -> {
            BusFeeReceiptsDTO busFeeReceiptsDTO = new BusFeeReceiptsDTO();
            busFeeReceiptsDTO.setUsername(studentBusFeeReceipt.getUsername());
            busFeeReceiptsDTO.setSession(studentBusFeeReceipt.getSession());
            busFeeReceiptsDTO.setReceiptNo(studentBusFeeReceipt.getReceiptNo());
            busFeeReceiptsDTO.setDate(studentBusFeeReceipt.getDate());
            busFeeReceiptsDTO.setSchedules(studentBusFeeReceipt.getSchedules());
            busFeeReceiptsDTO.setMode(studentBusFeeReceipt.getMode());
            busFeeReceiptsDTO.setTotal(studentBusFeeReceipt.getTotal());
            busFeeReceiptsDTO.setTotalLateConcSchlr(studentBusFeeReceipt.getTotalLateConcession());
            busFeeReceiptsDTO.setLateFeeCharges(studentBusFeeReceipt.getLateFeeCharges());
            busFeeReceiptsDTO.setConcession(studentBusFeeReceipt.getConcession());
            busFeeReceiptsDTO.setConcessionSplit(studentBusFeeReceipt.getConcessionSplit());
            busFeeReceiptsDTO.setRemarks(studentBusFeeReceipt.getRemarks());
            busFeeReceiptsDTO.setCollectedBy(studentBusFeeReceipt.getCollectedBy());
            //feeReceiptsDTO.setStudentFeeReceipt(studentFeeReceipt);
            if (studentBusFeeReceipt.getUsername() != null && studentBusFeeReceipt.getUsername().contains(AppConstant.STUDENT_ROLE)) {
                Optional<Student> studentOptional = studentRepository.findByUsername(studentBusFeeReceipt.getUsername());
                Student student = studentOptional.orElseThrow(() -> new RuntimeException("Student Username: " + studentBusFeeReceipt.getUsername() + " Not Found"));
                busFeeReceiptsDTO.setFullName(student.getFullName());
                busFeeReceiptsDTO.setClassName(student.getClassName());

                StudentBusFee studentBusFeeDB = studentBusFeeRepository.findByStudentIdAndSession(studentId, session);
                if (studentBusFeeDB != null) {
                    Map<String, Double> schedulesBifurcatedBusFee =
                            getSchedulesBifurcatedBusFee(
                                    studentBusFeeReceipt.getSchedules(),
                                    studentBusFeeDB.getBusMonthlyFees()

                            );

                    busFeeReceiptsDTO.setSchedulesBifurcated(schedulesBifurcatedBusFee);
                }
            }
            busFeeReceiptsDTOList.add(busFeeReceiptsDTO);
        });
        return new ResponseEntity<List<BusFeeReceiptsDTO>>(busFeeReceiptsDTOList, HttpStatus.OK);
    }

    public ResponseEntity<List<StudentBusFeeReceipt>> getBusFeeReceipts() {
        List<StudentBusFeeReceipt> paymentStudentsBus = studentBusFeeReceiptRepository.findAll();
        return new ResponseEntity<>(paymentStudentsBus, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<String> saveBusFeeReceipts(List<StudentBusFeeReceipt> paymentStudentsBus) {
        paymentStudentsBus.forEach(StudentBusFeeReceipt -> {
            Double totalLateConcession = StudentBusFeeReceipt.getTotal() +
                    (StudentBusFeeReceipt.getLateFeeCharges() != null ? StudentBusFeeReceipt.getLateFeeCharges() : 0) -
                    (StudentBusFeeReceipt.getConcession() != null ? StudentBusFeeReceipt.getConcession() : 0);
            StudentBusFeeReceipt.setTotalLateConcession(totalLateConcession);

            String recieptNo = sessionSequenceUtility.generateSessionSequence(AppConstant.BUSFEE_TYPE_SEQUENCE);
            StudentBusFeeReceipt.setReceiptNo(recieptNo);
            StudentBusFeeReceipt.setTimestamp(DateUtility.getCurrentTimeStamp());
        });
        studentBusFeeReceiptRepository.saveAll(paymentStudentsBus);
        return ResponseEntity.ok("Payement Student Bus Data Saved");
    }


}
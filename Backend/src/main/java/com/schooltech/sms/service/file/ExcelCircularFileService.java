package com.schooltech.sms.service.file;

import com.schooltech.sms.dao.client.circulars.BusCircularRepository;
import com.schooltech.sms.dao.client.circulars.ClassCircularRepository;
import com.schooltech.sms.dao.client.circulars.HolidayCircularRepository;
import com.schooltech.sms.entity.client.circulars.BusCircular;
import com.schooltech.sms.entity.client.circulars.ClassCircular;
import com.schooltech.sms.entity.client.circulars.ExamSchedule;
import com.schooltech.sms.entity.client.circulars.ResultGrade;
import com.schooltech.sms.service.circular.BusCircularService;
import com.schooltech.sms.service.circular.ClassCircularService;
import com.schooltech.sms.service.circular.HolidayCircularService;
import com.schooltech.sms.utility.ExcelFileUtility;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import static com.schooltech.sms.constant.AppConstant.*;
import static com.schooltech.sms.utility.AppUtility.isValidSessionFormat;
import static com.schooltech.sms.utility.ExcelFileUtility.throwExcelException;

@Service
public class ExcelCircularFileService {
    private static final String COL_ADD = "Add";
    private static final String COL_SESSION = "Class Name";
    private static final String COL_CLASS_NAME = "Class Name";
    private static final String COL_HOLIDAY_NAME = "Holiday Name";
    private static final String COL_HOLIDAY_DATE = "Date";
    private static final String COL_BUS_ROUTE = "Bus Route";
    @Autowired
    private ClassCircularService classCircularService;
    @Autowired
    private BusCircularService busCircularService;
    @Autowired
    private HolidayCircularService holidayCircularService;
    @Autowired
    private HolidayCircularRepository holidayCircularRepository;
    @Autowired
    private BusCircularRepository busCircularRepository;
    @Autowired
    private ClassCircularRepository classCircularRepository;

    ///////////////////////////// CLASS CIRCULAR////////////////////////////
    public ResponseEntity<String> processClassCircularExcelData(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {
            processSheet1(workbook.getSheetAt(0), file.getOriginalFilename()); // First sheet
        }
        return ResponseEntity.ok().body("Uploaded Class Circulars successfully");
    }

    private void processSheet1(Sheet sheet, String fileName) {

        List<ClassCircular> classCircularList = new ArrayList<>();

        Set<String> excelClassName = new HashSet<>();

        Iterator<Row> rowIterator = sheet.iterator();
        int rowIndex = 0;

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();

            if (rowIndex < 1) { // Skip header row
                rowIndex++;
                continue;
            }

            if (ExcelFileUtility.isRowEmpty(row)) { // Skip empty rows
                continue;
            }

            String isAddAllowed = ExcelFileUtility.getCellValue(row, 0, rowIndex, fileName);
            if (isAddAllowed == null)
                throwExcelException(fileName, rowIndex, COL_ADD, isAddAllowed, "Add Column cannot be empty");
            if (!isAddAllowed.equalsIgnoreCase("yes")) {
                continue;
            }

            int columnIndex = 0;
            String session = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String className = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            Double monthlyFee = ExcelFileUtility.parseDouble(ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName));
            Double lateFeeDayCharges = ExcelFileUtility.parseDouble(ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName));
            Double dueStartDate = ExcelFileUtility.parseDouble(ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName));
            String subjects = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            Double registrationFee = ExcelFileUtility.parseDouble(ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName));
            Double annualFee = ExcelFileUtility.parseDouble(ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName));
            Double admissionFee = ExcelFileUtility.parseDouble(ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName));


            //mandatory check for class name and session
            if (className != null && isValidSessionFormat(session)) {
                if (!excelClassName.add(session + className))
                    throwExcelException(fileName, rowIndex, COL_CLASS_NAME + "&" + COL_SESSION, session + " - " + className, "ClassName-Session combination is already present in Excel");
                validateClassNameSessionInExcel(className, session, fileName, rowIndex);
            } else {
                throwExcelException(fileName, rowIndex, COL_CLASS_NAME + "&" + COL_SESSION, session + " or " + className, "Class Name or Session is Invalid or empty");
            }


            ClassCircular classCircular = new ClassCircular();
            classCircular.setClassName(className);
            classCircular.setSession(session);

            Map<String, Double> schoolMonthlyFeesMap = new LinkedHashMap<>();
            int countList = -1;
            schoolMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
            schoolMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
            schoolMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
            schoolMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
            schoolMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
            schoolMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
            schoolMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
            schoolMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
            schoolMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
            schoolMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
            schoolMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
            schoolMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
            classCircular.setSchoolMonthlyFees(schoolMonthlyFeesMap);

            Map<String, Double> schoolMiscFeesMap = new LinkedHashMap<>();
            schoolMiscFeesMap.put(MISC_FEE_LIST.get(0), registrationFee);
            schoolMiscFeesMap.put(MISC_FEE_LIST.get(1), admissionFee);
            schoolMiscFeesMap.put(MISC_FEE_LIST.get(2), annualFee);
            classCircular.setSchoolMiscFees(schoolMiscFeesMap);

            Map<String, Double> schoolFeesRulesMap = new LinkedHashMap<>();
            schoolFeesRulesMap.put(SCHOOL_FEE_RULES_LIST.get(0), lateFeeDayCharges);
            schoolFeesRulesMap.put(SCHOOL_FEE_RULES_LIST.get(1), dueStartDate);
            classCircular.setSchoolFeesRules(schoolFeesRulesMap);

            List<String> subjectList = subjects != null
                    ? Arrays.stream(subjects.split(","))
                    .map(String::trim)          // optional: remove spaces
                    .collect(Collectors.toList())
                    : new ArrayList<>();
            classCircular.setSubjects(subjectList);

            //saving default values for exam schedule
            Map<String, List<ExamSchedule>> exampSchedulesMap = new HashMap<>();
            classCircular.setExamSchedules(exampSchedulesMap);
            //saving default values class qualities
            Map<String, List<String>> classQualitiesMap = new HashMap<>();
            classCircular.setClassQualities(classQualitiesMap); //since qualities have same structure as for exam schedule
            //saving default values for grade circular
            List<ResultGrade> resultGradeList = new ArrayList<>();
            classCircular.setGradeCircular(resultGradeList);

            classCircularList.add(classCircular);
            rowIndex++;
        }

        classCircularService.saveClassCirculars(classCircularList);
    }

    private void validateClassNameSessionInExcel(String className, String session, String fileName, int rowIndex) {
        Set<String> classNameInDBNormalized = classCircularRepository
                .findAllClassNameBySessionFromClassCircular(session)
                .stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .collect(Collectors.toSet());

        if (classNameInDBNormalized.contains(className))
            throwExcelException(fileName, rowIndex, COL_CLASS_NAME + "&" + COL_SESSION, session + " - " + className, "ClassName-Session combination is already present in Class Circular Data");
    }

    ///////////////////////////// BUS CIRCULAR////////////////////////////
    public ResponseEntity<String> processBusCircularExcelData(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {
            processBusSheet(workbook.getSheetAt(0), file.getOriginalFilename());
        }
        return ResponseEntity.ok().body("Uploaded Bus Circular successfully");
    }

    private void processBusSheet(Sheet sheet, String fileName) {
        List<BusCircular> busCircularList = new ArrayList<>();
        Set<String> excelBusRoute = new HashSet<>();

        Iterator<Row> rowIterator = sheet.iterator();
        int rowIndex = 0;

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();

            if (rowIndex < 1) { // Skip header row
                rowIndex++;
                continue;
            }

            if (ExcelFileUtility.isRowEmpty(row)) { // Skip empty rows
                continue;
            }

            String isAddAllowed = ExcelFileUtility.getCellValue(row, 0, rowIndex, fileName);
            if (isAddAllowed == null)
                throwExcelException(fileName, rowIndex, COL_ADD, isAddAllowed, "Add Column cannot be empty");
            if (!isAddAllowed.equalsIgnoreCase("yes")) {
                continue;
            }

            int columnIndex = 0;
            String session = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            String busRoute = ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName);
            Double monthlyFee = ExcelFileUtility.parseDouble(ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName));
            Double lateFeeDayCharges = ExcelFileUtility.parseDouble(ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName));
            Double lateFeeStartDate = ExcelFileUtility.parseDouble(ExcelFileUtility.getCellValue(row, ++columnIndex, rowIndex, fileName));

            //mandatory check for bus route and session
            if (busRoute != null && isValidSessionFormat(session)) {
                if (!excelBusRoute.add(busRoute + session))
                    throwExcelException(fileName, rowIndex, COL_BUS_ROUTE + " & " + COL_SESSION, session + " - " + busRoute, "BusRoute-Session combination is already present in Excel");
                validateBusRouteInExcel(busRoute, session, fileName, rowIndex);
            } else {
                throwExcelException(fileName, rowIndex, COL_BUS_ROUTE + " & " + COL_SESSION, session + " or " + busRoute, "BusRoute or Session is Invalid or empty");
            }

            BusCircular busCircular = new BusCircular();
            busCircular.setSession(session);
            busCircular.setBusRoute(busRoute);

            Map<String, Double> busMonthlyFeesMap = new LinkedHashMap<>();
            int countList = -1;
            busMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
            busMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
            busMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
            busMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
            busMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
            busMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
            busMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
            busMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
            busMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
            busMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
            busMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
            busMonthlyFeesMap.put(MONTH_NAMES_LIST_FEE.get(++countList), monthlyFee);
            busCircular.setBusMonthlyFees(busMonthlyFeesMap);

            Map<String, Double> busFeesRulesMap = new LinkedHashMap<>();
            busFeesRulesMap.put(SCHOOL_FEE_RULES_LIST.get(0), lateFeeDayCharges);
            busFeesRulesMap.put(SCHOOL_FEE_RULES_LIST.get(1), lateFeeStartDate);
            busCircular.setBusFeesRules(busFeesRulesMap);

            busCircularList.add(busCircular);
            rowIndex++;
        }

        busCircularService.saveBusCirculars(busCircularList);
    }

    private void validateBusRouteInExcel(String busRoute, String session, String fileName, int rowIndex) {
        Set<String> busRouteInDBNormalized = busCircularRepository
                .findAllBusRoutesBySession(session)
                .stream()
                .filter(Objects::nonNull)
                .map(s -> s.trim())
                .collect(Collectors.toSet());
        if (busRouteInDBNormalized.contains(busRoute))
            throwExcelException(fileName, rowIndex, COL_BUS_ROUTE + "&" + COL_SESSION, busRoute + " - " + session, "BussRoute-Session combination is already present in Bus Circular Data");

    }


    ///////////////////////////// HOLIDAY CIRCULAR////////////////////////////
//    public ResponseEntity<String> processHolidayCircularExcelData(MultipartFile file) throws IOException {
//        try (InputStream inputStream = file.getInputStream();
//             Workbook workbook = WorkbookFactory.create(inputStream)) {
//            processSheet1Holiday(workbook.getSheetAt(0), file.getOriginalFilename());
//        }
//        return ResponseEntity.ok().body("Uploaded Holiday Circulars successfully");
//    }
//
//    private void processSheet1Holiday(Sheet sheet, String fileName) {
//        List<HolidayCircular> holidayCircularList = new ArrayList<>();
//        Iterator<Row> rowIterator = sheet.iterator();
//        int rowIndex = 0;
//        List<HolidayCircular> holidayCircularsDB = holidayCircularRepository.findAll();
//
//        Set<String> excelHolidayName = new HashSet<>();
//
//        while (rowIterator.hasNext()) {
//            Row row = rowIterator.next();
//            if (rowIndex < 1) { // Skip header row
//                rowIndex++;
//                continue;
//            }
//
//            if (ExcelFileUtility.isRowEmpty(row)) { // Skip empty rows
//                continue;
//            }
//
//            String isAddAllowed = ExcelFileUtility.getCellValue(row, 0, rowIndex, fileName);
//            if (isAddAllowed == null)
//                throwExcelException(fileName, rowIndex, COL_ADD, isAddAllowed, "Add Column cannot be empty");
//            if (!isAddAllowed.equalsIgnoreCase("yes")) {
//                continue;
//            }
//
//            //test holiday null date
//            String holidayDateExcel = (ExcelFileUtility.getCellValue(row, 1, rowIndex, fileName));
//            LocalDate holidayDate = null;
//            String holidayName = ExcelFileUtility.getCellValue(row, 2, rowIndex, fileName);
//
//            if (holidayName != null) {
//                if (!excelHolidayName.add(holidayName))
//                    throwExcelException(fileName, rowIndex, COL_HOLIDAY_NAME, holidayName, "Holiday is already present in Excel");
//                validateHolidayNameInExcel(holidayCircularsDB, holidayName, fileName, rowIndex);
//            } else {
//                throwExcelException(fileName, rowIndex, COL_HOLIDAY_NAME, holidayName, "Holiday Name is Invalid or empty");
//            }
//
//            if (holidayDateExcel != null) {
//                holidayDate = ExcelFileUtility.parseDateDDhyphenMMhyphenYY(holidayDateExcel);
//                validateHolidayDateInExcel(holidayCircularsDB, holidayDate, fileName, rowIndex);
//            } else {
//                throwExcelException(fileName, rowIndex, COL_HOLIDAY_DATE, holidayDate.toString(), "Holiday cannot be empty");
//
//            }
//
//            HolidayCircular holidayCircular = new HolidayCircular();
//            holidayCircular.setDate(holidayDate);
//            holidayCircular.setHolidayName(holidayName);
//            holidayCircularList.add(holidayCircular);
//            rowIndex++;
//        }
//
//        holidayCircularService.saveHolidayCirculars(holidayCircularList);
//    }
//
//    private void validateHolidayNameInExcel(List<HolidayCircular> holidayCircularsDB, String holidayName, String fileName, int rowIndex) {
//        Set<String> holidayNamesInDB =
//                holidayCircularsDB.stream()
//                        .filter(Objects::nonNull)
//                        .map(h -> h.getHolidayName().trim())
//                        .collect(Collectors.toSet());
//
//        if (holidayNamesInDB.contains(holidayName)) {
//            throwExcelException(fileName, rowIndex, COL_HOLIDAY_NAME, holidayName, "Holiday is already present in holiday Circular Data");
//        }
//    }
//
//    private void validateHolidayDateInExcel(List<HolidayCircular> holidayCircularsDB, LocalDate holidayDate, String fileName, int rowIndex) {
//        Set<LocalDate> holidayDatesInDB =
//                holidayCircularsDB.stream()
//                        .filter(Objects::nonNull)
//                        .map(HolidayCircular::getDate)
//                        .collect(Collectors.toSet());
//
//        if (holidayDatesInDB.contains(holidayDate)) {
//            throwExcelException(fileName, rowIndex, COL_HOLIDAY_DATE, holidayDate.toString(), "Date combination is already present in Holiday Circular Data");
//        }
//    }


}

package com.schooltech;

import com.schooltech.multitenant.TenantContext;
import com.schooltech.security.jwt.service.AuthService;
import com.schooltech.sms.configuration.ClientProperties;
import com.schooltech.sms.constant.AppConstant;
import com.schooltech.sms.entity.client.staff.Staff;
import com.schooltech.sms.entity.client.user.User;
import com.schooltech.sms.service.staff.StaffService;
import com.schooltech.sms.utility.DateUtility;
import com.schooltech.sms.utility.UsernameUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
public class SchoolTechApplication implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(SchoolTechApplication.class);
    @Autowired
    private AuthService authService;
    @Autowired
    private UsernameUtility usernameUtility;
    //    @Autowired
//    private AdminService adminService;
    @Autowired
    private StaffService staffService;
    @Autowired
    private ClientProperties clientProperties;
    @Autowired
    private SchoolTechTestData schoolTechTestData;

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata")); // set IST default time zone
        SpringApplication.run(SchoolTechApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        clientProperties.getDb_details().forEach((key, value) -> {
            //System.out.println(key + ":" + value);
            createSuperAdminAccount(value);
        });
    }

    public void createSuperAdminAccount(String clientTenantId) {
        TenantContext.setCurrentTenant(clientTenantId);
        String username = "superadmin";

        if (authService.getUserByUsername(username) == null) {
            User defaultUser = new User();
            defaultUser.setUsername(username);
            defaultUser.setEmail("connecttojasmine1@gmail.com");
            defaultUser.setPhoneNo("9058899133");
            defaultUser.setTenantId(clientTenantId);
            defaultUser.setRole(AppConstant.SUPER_ADMIN_ROLE);
            defaultUser.setIsActive(true);
            defaultUser.setPassword(AppConstant.DEFAULT_PASSWORD); // Securely hash password //for admin good@123 will be bad@123
            defaultUser.setTimestamp(DateUtility.getCurrentTimeStamp()); // Current timestamp
            authService.createUser(defaultUser);
            log.info("Super Admin User created for tenant: {}", clientTenantId);

            Staff staff = new Staff();
            staff.setUsername(username);
            staff.setRole(AppConstant.SUPER_ADMIN_ROLE);
            staff.setFullName("Jasmine Fiore".toLowerCase());
            staff.setEmployeeId("jf");
            staff.setDesignation("OTHER".toLowerCase());
            Map<String, String> adminDocuments = new HashMap<>();
            staff.setDocuments(adminDocuments);
            List<Staff> staffList = new ArrayList<>();
            staffList.add(staff);
            staffService.saveStaffs(staffList);

        } else {
            log.info("SCHOOLTECH... Default User \"superadmin\" already exist for tenant {}", clientTenantId);
        }

        if (clientTenantId.equalsIgnoreCase("stclient_default"))
            schoolTechTestData.prepareTestData();

        log.info("Clearing TenantContext for thread.");
        TenantContext.clear(); // Ensure cleanup after the request
    }
}



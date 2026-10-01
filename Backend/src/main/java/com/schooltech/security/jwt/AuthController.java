package com.schooltech.security.jwt;

import com.schooltech.multitenant.TenantContext;
import com.schooltech.security.jwt.dto.UpdatePasswordDTO;
import com.schooltech.security.jwt.entity.JWTRequest;
import com.schooltech.security.jwt.entity.JWTResponse;
import com.schooltech.security.jwt.service.AuthService;
import com.schooltech.security.jwt.service.TenantService;
import com.schooltech.security.jwt.util.JWTHelper;
import com.schooltech.sms.entity.client.user.User;
import com.schooltech.sms.entity.client.user.UserTrash;
import com.schooltech.sms.exception.UserNotFoundException;
import com.schooltech.sms.service.communication.OtpService;
import com.schooltech.sms.utility.AppUtility;
import com.schooltech.sms.utility.DateUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private AuthenticationManager manager;
    @Autowired
    private JWTHelper helper;
    @Autowired
    private OtpService otpService;
    @Autowired
    private AuthService authService;

    @Autowired
    private TenantService tenantService;

    @PostMapping("/login")
    public ResponseEntity<JWTResponse> login(@RequestBody JWTRequest request) {
        try {
            this.doAuthenticate(request.getUsername(), request.getPassword());
            //loadUserByUsername is overridden in CustomUserDetailsService which implements spring UserDetailsService
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            //generating token based on the username/password received
            String token = this.helper.generateToken(userDetails);
            //userDetails is nothing but User only
            User user = (User) userDetails;

            // Save last login timestamp
            user.setLastLogin(DateUtility.getCurrentTimeStamp()); // sets it on the user object in memory only
            authService.updateLastLogin(user.getUsername(), user.getLastLogin());

            String tenantId = TenantContext.getCurrentTenant();
//            SchoolTenant tenant = tenantService.getSchoolTenantByTenantId(tenantId);
//            tenant.getTenantInfo().put("tenantName", tenant.getTenantName());
//            tenant.getTenantInfo().put("tenantEmail", tenant.getEmail());
//            tenant.getTenantInfo().put("tenantPhoneNo", tenant.getPhoneNo());
//            tenant.getTenantInfo().put("tenantId", tenant.getTenantId());
//            tenant.getTenantInfo().put("tenantCode", tenant.getTenantCode());
//            tenant.getTenantInfo().put("tenantAltPhoneNo", tenant.getAltPhoneNo());

            //getting User details from DB
            //User user = authService.getUserByUserName(userDetails.getUsername());

            JWTResponse response = JWTResponse.builder()
                    .jwtToken(token)
                    .username(userDetails.getUsername())
                    .role(user.getRole())
                    .isActive(user.getIsActive())
                    //.fullName(user.getFullName())
                    .email(user.getEmail())
                    .phoneNo(user.getPhoneNo())
                    .tenantId(user.getTenantId())
                    .lastLogin(user.getLastLogin() != null
                            ? user.getLastLogin().toLocalDateTime()
                            .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                            : null)
                    //.tenantCode(tenant.getTenantCode())
                    //.tenantInfo(tenant.getTenantInfo())
                    .build();

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            throw new UserNotFoundException(e.getMessage());
        }
    }

    private void doAuthenticate(String username, String password) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, password);
        try {
            manager.authenticate(authentication);
        } catch (Exception e) {
            throw new BadCredentialsException(" Invalid Username or Password  !!");
        }

    }

    @ExceptionHandler(BadCredentialsException.class)
    public String exceptionHandler() {
        throw new UserNotFoundException("Entered Credentials Invalid !!");
        //return "Entered Credentials Invalid !!";
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return authService.getUsers();
    }

    @GetMapping("/users/{userId}")
    public User getUsers(@PathVariable String userId) {
        return authService.getUsersById(userId);
    }

    @GetMapping("/getDeActivatedUser/{username}")
    public ResponseEntity<UserTrash> getDeActivatedUser(@PathVariable String username) {
        return ResponseEntity.ok(authService.getDeActivatedUser(username));
    }

    @PostMapping("/create-user")
    public User createUser(@RequestBody User user) {
        return authService.createUser(user);
    }

    /*
        this method is used to update user credential with generated otp
    */
    @PutMapping("/update-user-with-email-otp")
    public User updateUserPassword(@RequestBody User user) {//ForgotUserRequest forgotUserRequest){
        if (otpService.validateEmailOtp(user.getEmail(), user.getOtp())) { //validating the generated OTP for User Credential Update
            otpService.clearOtp(user.getEmail());
            return authService.updateUser(user);
        }
        throw new UserNotFoundException("Wrong OTP!!!!!");
    }

    @PutMapping("/update-user-with-sms-otp")
    public User updateUserPasswordWithSmSOtp(@RequestBody User user) {//ForgotUserRequest forgotUserRequest){
        if (otpService.validateSmsOtp(user.getPhoneNo(), user.getOtp())) { //validating the generated OTP for User Credential Update
            String phoneNoWithoutCC = AppUtility.removeCountryCode(user.getPhoneNo());
            user.setPhoneNo(phoneNoWithoutCC);
            return authService.updateUser(user);
        }
        throw new UserNotFoundException("Wrong OTP!!!!!");
    }

    //Below api used for local testng of forgot password
    @PutMapping("/update-user-with-sms-otp-disabled")
    public User updateUserPasswordWithSmSOtpDisabled(@RequestBody User user) {//ForgotUserRequest forgotUserRequest){
        if (user.getOtp() != null && user.getOtp().equalsIgnoreCase("1234")) {
            String phoneNoWithoutCC = AppUtility.removeCountryCode(user.getPhoneNo());
            user.setPhoneNo(phoneNoWithoutCC);
            return authService.updateUser(user);
        }
        throw new UserNotFoundException("Wrong OTP!!!!!");
    }

    /*
    this method is used to update user credential with authentication token
   */
    @PutMapping("/update-user-with-auth")
    public User updateUser(@RequestBody User user) {//ForgotUserRequest forgotUserRequest){
        return authService.updateUser(user);
    }


    @PutMapping("/update-password-with-token")
    public ResponseEntity<String> updatePassword(@RequestBody UpdatePasswordDTO updatePasswordDTO) {
        try {
            this.doAuthenticate(updatePasswordDTO.getUser().getUsername(), updatePasswordDTO.getOldPassword());
            authService.updateUser(updatePasswordDTO.getUser());
            return new ResponseEntity<>("Password is Updated", HttpStatus.OK);
        } catch (Exception e) {
            throw new UserNotFoundException(e.getMessage());
        }
    }

//    @DeleteMapping("/deActivateUser/{username}")
//    @Transactional
//    public ResponseEntity<String> deActivateUserByUsername(@PathVariable String username) {
//        authService.deActivateUserByUsername(username);
//        return ResponseEntity.ok("User with username " + username + " has been deactivated successfully.");
//    }

}
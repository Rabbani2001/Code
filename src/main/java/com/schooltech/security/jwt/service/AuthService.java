package com.schooltech.security.jwt.service;


import com.schooltech.security.jwt.dto.UserResponseDTO;
import com.schooltech.sms.dao.client.staff.StaffRepository;
import com.schooltech.sms.dao.client.student.ParentRepository;
import com.schooltech.sms.dao.client.student.StudentRepository;
import com.schooltech.sms.dao.client.teacher.TeacherRepository;
import com.schooltech.sms.dao.client.user.UserRepository;
import com.schooltech.sms.dao.client.userTrash.UserTrashRepository;
import com.schooltech.sms.entity.client.user.User;
import com.schooltech.sms.entity.client.user.UserTrash;
import com.schooltech.sms.exception.UserNotFoundException;
import com.schooltech.sms.utility.DateUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private ParentRepository parentRepository;
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserTrashRepository userTrashRepository;

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUsersById(String id) {
        return userRepository.findByUsernameOrEmailOrPhoneNo(id).orElseThrow(() -> new UserNotFoundException("User Not Found"));
    }

    private List<UserResponseDTO> getChildrenOfParent(String parentUsername) {
        return studentRepository.findByParentUsername(parentUsername)
                .stream()
                .map(student -> UserResponseDTO.builder()
                        .username(student.getUsername())
                        .fullName(student.getFullName())
                        .className(student.getClassName())
                        .rollNo(student.getRollNo() != null ? student.getRollNo().toString() : null)
                        .build())
                .toList();
    }

    public UserResponseDTO getUserResponseByPhoneNo(String phoneNo) {
        User user = userRepository.findByPhoneNo(phoneNo)
                .orElseThrow(() -> new UserNotFoundException(
                        "No user found with phone number: " + phoneNo));

        String role = user.getRole();
        String username = user.getUsername();

        if (role == null) {
            throw new IllegalStateException("Role not found for user: " + username);
        }

        Optional<UserResponseDTO> response = switch (role) {
            case "teacher" -> teacherRepository.findByUsername(username)
                    .map(teacher -> UserResponseDTO.builder()
                            .userType("teacher")
                            .username(username)
                            .fullName(teacher.getFullName())
                            .role(role)
                            .employeeId(teacher.getEmployeeId())
                            .classNames(teacher.getClassNames())
                            .build());

            case "parent" -> parentRepository.findByUsername(username)
                    .map(parent -> UserResponseDTO.builder()
                            .userType("parent")
                            .username(username)
                            .fullName(parent.getFullName())
                            .children(getChildrenOfParent(username))
                            .build());

            // everything else is treated as staff
            default -> staffRepository.findByUsername(username)
                    .filter(staff -> !"superadmin".equals(staff.getUsername()))
                    .map(staff -> UserResponseDTO.builder()
                            .userType("staff")
                            .username(username)
                            .fullName(staff.getFullName())
                            .role(role)
                            .employeeId(staff.getEmployeeId())
                            .classNames(staff.getClassNames())
                            .build());
        };

        return response.orElseThrow(() -> new UserNotFoundException(
                "No " + role + " profile found for phone number: " + phoneNo));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public UserTrash getDeActivatedUser(String username) {
        return userTrashRepository.getDeActivatedUser(username);
    }

    public User getUserByPhoneNo(String phoneNo) {
        return userRepository.findByPhoneNo(phoneNo).orElseThrow(() -> new UserNotFoundException("User Not Found"));
    }

    public User getUserByUserName(String username) {
        return userRepository.findByUsernameOrEmailOrPhoneNo(username).get();
    }

    public UserResponseDTO getUserByAadharNo(String aadharNo) {
        return teacherRepository.findFirstByAadharNo(aadharNo)
                .map(teacher -> UserResponseDTO.builder()
                        .userType("teacher")
                        .username(teacher.getUsername())
                        .fullName(teacher.getFullName())
                        .role("teacher")
                        .employeeId(teacher.getEmployeeId())
                        .classNames(teacher.getClassNames())
                        .build())

                // .or(() -> ...) runs the next lookup only if the previous result was empty
                .or(() -> staffRepository.findFirstByAadharNo(aadharNo)
                        .filter(staff -> !"superadmin".equalsIgnoreCase(staff.getUsername()))
                        .map(staff -> UserResponseDTO.builder()
                                .userType("staff")
                                .username(staff.getUsername())
                                .fullName(staff.getFullName())
                                .role(staff.getRole())
                                .employeeId(staff.getEmployeeId())
                                .classNames(staff.getClassNames())
                                .build()))

                .or(() -> parentRepository.findFirstByAadharNo(aadharNo)
                        .map(parent -> UserResponseDTO.builder()
                                .userType("parent")
                                .username(parent.getUsername())
                                .fullName(parent.getFullName())
                                .build()))

                .or(() -> studentRepository.findByAadharNo(aadharNo)
                        .map(student -> UserResponseDTO.builder()
                                .userType("student")
                                .username(student.getUsername())
                                .fullName(student.getFullName())
                                .className(student.getClassName())
                                .rollNo(student.getRollNo() != null ? student.getRollNo().toString() : null)
                                .build()))

                .orElseThrow(() -> new UserNotFoundException(
                        "No user found with Aadhaar number: " + aadharNo));
    }

    public User createUser(User user) {
        try {
            //Here is password is encoded into BCryptPasswordEncoder
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setTimestamp(DateUtility.getCurrentTimeStamp());
            return userRepository.save(user);
        } catch (Exception e) {
            throw new UserNotFoundException(e.getMessage());
        }
    }

    public void saveAllUser(List<User> userList) {
        try {
            userList.forEach(user -> {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                user.setTimestamp(DateUtility.getCurrentTimeStamp());
            });
            userRepository.saveAll(userList);
        } catch (Exception e) {
            throw new RuntimeException("SchoolTech....Error in saving users in DB");
        }
    }

    public User updateUser(User user) {
        try {
            User userDB = null;
            Optional<User> optionalUser = null;
            //Username can be fetched using username, phoneNo, email as these are unique for each user
            if (user.getUsername() != null) {
                optionalUser = userRepository.findByUsernameOrEmailOrPhoneNo(user.getUsername());
            } else if (user.getPhoneNo() != null) {
                optionalUser = userRepository.findByUsernameOrEmailOrPhoneNo(user.getPhoneNo());
            } else if (user.getEmail() != null) {
                optionalUser = userRepository.findByUsernameOrEmailOrPhoneNo(user.getEmail());
            }

            if (optionalUser != null && optionalUser.isPresent()) userDB = optionalUser.get();
            if (userDB == null) throw new UserNotFoundException("Unable to Update as User not Found");
            updateNonNullFields(user, userDB);
            userDB.setTimestamp(DateUtility.getCurrentTimeStamp());
            return userRepository.save(userDB);
        } catch (Exception e) {
            if (e instanceof DataIntegrityViolationException) {
                throw new UserNotFoundException("Duplicate Error: please check Email id/ Phone No");
            } else {
                throw new UserNotFoundException("User Not found");
            }
        }
    }

    private void updateNonNullFields(User user, User userDB) {
        if (user.getEmail() != null) userDB.setEmail(user.getEmail());
        if (user.getPhoneNo() != null) userDB.setPhoneNo(user.getPhoneNo());
        if (user.getAltPhoneNo() != null) userDB.setAltPhoneNo(user.getAltPhoneNo());
        if (user.getIsActive() != null) userDB.setIsActive(user.getIsActive());
        if (user.getLastLogin() != null) userDB.setLastLogin(user.getLastLogin());
        if (user.getRole() != null) userDB.setRole(user.getRole());
        if (user.getUpdatedBy() != null) userDB.setUpdatedBy(user.getUpdatedBy());
        if (user.getPassword() != null) userDB.setPassword(passwordEncoder.encode(user.getPassword()));
    }

    public void updateLastLogin(String username, Timestamp lastLogin) {
        try {
            User userDB = userRepository.findByUsername(username);
            if (userDB == null) throw new UserNotFoundException("User not found");
            userDB.setLastLogin(lastLogin);
            userRepository.save(userDB);
        } catch (Exception e) {
            // Don't block login if this fails — just log it
            throw new UserNotFoundException("Failed to update last login for user: " + username, e);
        }
    }

    public void deActivateUserByUsername(String username, String updatedBy) {
        if (!userRepository.existsByUsername(username)) {
            throw new RuntimeException("User with username " + username + " does not exist.");
        }

        // Fetch the user to be moved to the trash
        User user = userRepository.findByUsername(username);

        // Map User to UserTrash
        UserTrash userTrash = new UserTrash();
        userTrash.setUsername(user.getUsername());
        userTrash.setEmail(user.getEmail());
        userTrash.setPhoneNo(user.getPhoneNo());
        userTrash.setAltPhoneNo(user.getAltPhoneNo());
        userTrash.setTenantId(user.getTenantId());
        userTrash.setRole(user.getRole());
        userTrash.setUpdatedBy(updatedBy);
        userTrash.setPassword(user.getPassword());
        userTrash.setTimestamp(user.getTimestamp());

        // Save the user in the trash table
        userTrashRepository.save(userTrash);

        // Delete the user from the user table
        userRepository.deleteByUsername(username);
    }

    public void activateUserByUsername(String username, String updatedBy) {
        // Fetch the user from the trash table
        UserTrash userTrash = userTrashRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User with username " + username + " does not exist in trash."));

        // Map UserTrash to User
        User user = new User();
        user.setUsername(userTrash.getUsername());
        user.setEmail(userTrash.getEmail());
        user.setPhoneNo(userTrash.getPhoneNo());
        user.setAltPhoneNo(userTrash.getAltPhoneNo());
        user.setTenantId(userTrash.getTenantId());
        user.setRole(userTrash.getRole());
        user.setUpdatedBy(updatedBy);
        user.setIsActive(true); // Set the user as active
        user.setPassword(userTrash.getPassword());
        user.setTimestamp(userTrash.getTimestamp());

        // Save the user back to the user table
        userRepository.save(user);

        // Delete the user from the trash table
        userTrashRepository.delete(userTrash);
    }


}

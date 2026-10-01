package com.schooltech.security.jwt.dto;

import com.schooltech.sms.entity.client.user.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePasswordDTO {
    private User user;
    private String oldPassword;
    private String newPassword;

}

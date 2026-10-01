package com.schooltech.sms.controller.staff.dto;

import com.schooltech.sms.entity.client.staff.Staff;
import com.schooltech.sms.entity.client.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class StaffUserDTO {
    private Staff staff;
    private User user;
}

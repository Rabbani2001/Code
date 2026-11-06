package com.schooltech.sms.controller.student.dto;

import com.schooltech.sms.entity.client.student.Parent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParentPanelDTO {
    //student fields
    private Parent parent;
    //user fields
    private String phoneNo;
    //Kids field
    private List<KidInfo> kidInfoList;
}

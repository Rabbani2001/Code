package com.schooltech.sms.controller.visitor.dto;

import com.schooltech.sms.entity.client.visitor.Visitor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class VisitorDTO {
    private Visitor visitor;
    private String tenantId;
}

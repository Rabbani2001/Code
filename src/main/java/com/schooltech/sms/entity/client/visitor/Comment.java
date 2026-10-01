package com.schooltech.sms.entity.client.visitor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    private String username;
    private String commentorName;
    private String date;
    private String time;
    private String commentMessage;


}

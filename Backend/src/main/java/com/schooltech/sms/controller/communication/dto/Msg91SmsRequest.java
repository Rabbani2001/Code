package com.schooltech.sms.controller.communication.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Msg91SmsRequest {

    @JsonProperty("template_id")
    private String templateId;

    @JsonProperty("short_url")
    private String shortUrl;

    @JsonProperty("realTimeResponse")
    private Integer realTimeResponse;

    private List<Map<String, String>> recipients;

}
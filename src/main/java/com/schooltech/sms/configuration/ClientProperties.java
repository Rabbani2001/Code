package com.schooltech.sms.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "stclient")
public class ClientProperties {
    private Map<String, String> db_details;
}

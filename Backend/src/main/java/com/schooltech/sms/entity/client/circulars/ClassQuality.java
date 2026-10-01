package com.schooltech.sms.entity.client.circulars;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassQuality {

    @NotBlank(message = "qualityName cannot be null or empty")
    private String qualityName;
    @NotBlank(message = "qualityObtained cannot be null or empty")
    private String qualityObtained;


}

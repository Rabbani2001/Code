package com.schooltech.sms.controller.fileupload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemoveDocumentDTO {
    private String tenantId;
    private String fileOldUrl;
    private String fileNameToRemove;
    private String role;
}

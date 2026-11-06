package com.schooltech.sms.controller.fileupload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadDocumentDTO {
    private String tenantId;
    private String fileOldUrl;
    private String fileNameToUpload;
    private String role;
    private String session;
}

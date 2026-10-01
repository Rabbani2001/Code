package com.schooltech.sms.service.file;

import com.schooltech.sms.utility.ShortUUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Service
public class FileUploadS3Service {

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.region}")
    private String region;

    @Autowired
    private S3Client s3Client;   //convert it to constructor or setter based DI

    public String uploadFile(MultipartFile file, String path) throws IOException {
        String key = path +
                ShortUUID.generate() + "_" +
                file.getOriginalFilename().toLowerCase();

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        //.acl("public-read") // optional: public URL
                        .build(),
                RequestBody.fromInputStream(file.getInputStream(), file.getSize())
        );

        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + key;
    }

    public void deleteFileByUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) return;

        try {
            // Extract object key from full URL
            String prefix = "amazonaws.com/";
            int index = fileUrl.indexOf(prefix);
            if (index == -1) {
                throw new RuntimeException("Invalid S3 URL format: " + fileUrl);
            }
            // Everything after "amazonaws.com/"
            String fileKey = fileUrl.substring(index + prefix.length());

            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();

            s3Client.deleteObject(deleteRequest);
            System.out.println("✅ Deleted file from S3: " + fileKey);

        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file from S3: " + e.getMessage(), e);
        }
    }
}

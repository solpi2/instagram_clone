package com.jinn.backend.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 s3Client;

    @Value("${AWS_BUCKET_NAME}")
    private String bucketName;

    public String uploadFile(MultipartFile file, String folder) {
        if (s3Client == null) {
            log.warn("S3 client not configured. Using local storage fallback.");
            throw new RuntimeException("S3 not configured");
        }

        validateFile(file);

        String fileName = generateFileName(file, folder);

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName,
                    fileName,
                    file.getInputStream(),
                    metadata
            );

            s3Client.putObject(putObjectRequest);

            log.info("Successfully uploaded file to S3: {}", fileName);

            return fileName;
        } catch (IOException e) {
            log.error("Failed to upload file to S3", e);
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("File size exceeds 5MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }
    }

    private String generateFileName(MultipartFile file, String folder) {
        String originalFileName = file.getOriginalFilename();
        String extension = originalFileName != null && originalFileName.contains(".")
                ? originalFileName.substring(originalFileName.lastIndexOf("."))
                : "";

        return folder + "/" + UUID.randomUUID().toString() + extension;
    }
}
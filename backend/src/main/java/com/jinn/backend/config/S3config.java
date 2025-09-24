package com.jinn.backend.config;

import com.amazonaws.services.s3.AmazonS3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class S3Config {

    @Value("${AWS_ACCESS_KEY_ID}")
    private String accessKeyId;

    @Value("${AWS_SECRET_ACCESS_KEY}")
    private String secretAccessKey;

    @Value("${AWS_REGION}")
    private String region;

    @Bean
    public AmazonS3 amazonS3() {
        if (accessKeyId == null || accessKeyId.isEmpty() || secretAccessKey == null || secretAccessKey.isEmpty()) {
            log.warn("AWS credentials not configured. S3 upload will not be available.");
            return null;
        }
    }
}
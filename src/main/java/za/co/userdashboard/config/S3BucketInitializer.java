package za.co.userdashboard.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import za.co.userdashboard.service.ProfileImageService;

/**
 * Ensures the S3 bucket exists on startup when using LocalStack (endpoint override set).
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class S3BucketInitializer implements ApplicationRunner {

    private final ProfileImageService profileImageService;

    @Value("${app.s3.endpoint-override:}")
    private String endpointOverride;

    @Override
    public void run(ApplicationArguments args) {
        if (endpointOverride != null && !endpointOverride.isBlank()) {
            profileImageService.ensureBucketExists();
        }
    }
}

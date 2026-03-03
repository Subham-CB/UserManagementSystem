package za.co.userdashboard.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import za.co.userdashboard.service.ImageStreamResult;
import za.co.userdashboard.service.ProfileImageService;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProfileImageServiceImpl implements ProfileImageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );
    private static final String PREFIX = "profiles/";

    private final AmazonS3 amazonS3;

    @Value("${app.s3.bucket-name:userdashboard-profiles}")
    private String bucketName;

    @Override
    public void ensureBucketExists() {
        if (!amazonS3.doesBucketExistV2(bucketName)) {
            amazonS3.createBucket(bucketName);
            log.info("Created S3 bucket: {}", bucketName);
        } else {
            log.debug("S3 bucket already exists: {}", bucketName);
        }
    }

    @Override
    public String uploadProfileImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Invalid image type. Allowed: JPEG, PNG, GIF, WebP");
        }
        String originalFilename = file.getOriginalFilename();
        String ext = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf('.'))
                : ".jpg";
        String key = PREFIX + UUID.randomUUID() + ext;

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);
            metadata.setContentLength(file.getSize());
            amazonS3.putObject(bucketName, key, file.getInputStream(), metadata);
            log.info("Uploaded profile image: {}", key);
            return key;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload profile image", e);
        }
    }

    @Override
    public ImageStreamResult getProfileImage(String key) {
        if (key == null || !key.startsWith(PREFIX)) {
            return null;
        }
        try {
            S3Object s3Object = amazonS3.getObject(bucketName, key);
            String contentType = s3Object.getObjectMetadata().getContentType();
            if (contentType == null || contentType.isBlank()) {
                contentType = "image/jpeg";
            }
            Long contentLength = s3Object.getObjectMetadata().getContentLength();
            InputStream stream = s3Object.getObjectContent();
            return new ImageStreamResult(contentType, stream, contentLength > 0 ? contentLength : null);
        } catch (AmazonS3Exception e) {
            if (e.getStatusCode() == 404) {
                return null;
            }
            throw e;
        }
    }
}

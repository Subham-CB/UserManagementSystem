package za.co.userdashboard.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import za.co.userdashboard.service.S3PhotoService;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3PhotoServiceImpl implements S3PhotoService {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name:user-uploads}")
    private String bucketName;

    @Value("${aws.endpoint-override:http://localhost:4566}")
    private String endpoint;

    @Value("${aws.region:us-east-1}")
    private String region;

    @Override
    public void createBucketIfNotExists() {

        try {
            HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();
            s3Client.headBucket(headBucketRequest);
            log.info("Bucket '{}' already exists", bucketName);
        } catch (NoSuchBucketException e) {
            CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .build();
            s3Client.createBucket(createBucketRequest);
            log.info("Bucket '{}' created successfully", bucketName);
        } catch (Exception e) {
            log.error("Error checking/creating bucket: {}", e.getMessage());
        }

    }

    @Override
    public String uploadProfilePhoto(MultipartFile file, String username) {

        try{
            if (file.isEmpty()){
                throw new IllegalArgumentException("File is empty");
            }
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")){
                throw new IllegalArgumentException("Only image files are allowed");
            }
            String fileExtension = getFileExtension(file.getOriginalFilename());
            String uniqueFileName = "profile_" + UUID.randomUUID() + "." + fileExtension;
            String s3Key = "users/" + username + "/" + uniqueFileName;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(putObjectRequest,RequestBody.fromInputStream(
                    file.getInputStream(),file.getSize()));

            String fileUrl = generateS3Url(s3Key);
            log.info("Profile photo uplaoded sucessfully for user: {}",username);

            return fileUrl;

        } catch (IOException e) {
            log.error("IO error uploading file: {}",e.getMessage());
            throw new RuntimeException("Failed to upload photo: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error uploading profile photo: {}", e.getMessage());
            throw new RuntimeException("Failed to upload photo: " + e.getMessage());
        }

    }

    @Override
    public String generateS3Url(String s3Key) {

        if (endpoint.contains("localhost") || endpoint.contains("localstack")) {
            return endpoint + "/" + bucketName + "/" + s3Key;
        }
        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + s3Key;
    }

    @Override
    public String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "jpg";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    @Override
    public void deleteProfilePhoto(String photoUrl) {

        try {
            String s3Key = extractS3Key(photoUrl);

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            log.info("Profile photo deleted: {}", s3Key);
        } catch (Exception e) {
            log.error("Error deleting profile photo: {}", e.getMessage());
        }

    }

    @Override
    public String extractS3Key(String photoUrl) {
        {
            if (photoUrl == null) {
                return "";
            }
            int bucketIndex = photoUrl.indexOf(bucketName);
            if (bucketIndex != -1) {
                return photoUrl.substring(bucketIndex + bucketName.length() + 1);
            }
            return photoUrl;
        }
    }
}

package za.co.userdashboard.service;

import org.springframework.web.multipart.MultipartFile;

public interface S3PhotoService {

    void createBucketIfNotExists();

    String uploadProfilePhoto(MultipartFile file, String username);

    String generateS3Url(String s3Key);

    String getFileExtension(String filename);

    void deleteProfilePhoto(String photoUrl);

    String extractS3Key(String photoUrl);

}

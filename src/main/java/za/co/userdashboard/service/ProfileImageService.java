package za.co.userdashboard.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Uploads profile images to S3 (or LocalStack) and returns the object key.
 * Caller is responsible for building the public URL from the key if needed.
 */
public interface ProfileImageService {

    /**
     * Ensure the configured bucket exists. Called on startup when using LocalStack.
     */
    void ensureBucketExists();

    /**
     * Upload a profile image and return the S3 object key (e.g. profiles/uuid-filename.ext).
     *
     * @param file image file (allowed types: jpeg, png, gif, webp)
     * @return object key, or null if file is null or empty
     */
    String uploadProfileImage(MultipartFile file);

    /**
     * Stream a profile image from S3 by key. Used for serving images via app proxy.
     *
     * @param key S3 object key (must start with profiles/)
     * @return response content type and input stream, or null if not found
     */
    ImageStreamResult getProfileImage(String key);
}

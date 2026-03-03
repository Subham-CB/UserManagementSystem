package za.co.userdashboard.service;

import java.io.InputStream;

/**
 * Result of fetching a profile image from S3 for streaming.
 */
public record ImageStreamResult(String contentType, InputStream inputStream, Long contentLength) {
    public ImageStreamResult(String contentType, InputStream inputStream) {
        this(contentType, inputStream, null);
    }
}
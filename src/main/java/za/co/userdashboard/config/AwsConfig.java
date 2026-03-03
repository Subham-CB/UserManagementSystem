package za.co.userdashboard.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;
import java.net.URL;

@Slf4j
@Configuration
public class AwsConfig {

    @Bean
    public S3Client s3Client(){
        String endpoint = System.getenv("AWS_ENDPOINT_OVERRIDE");
        String region = System.getenv("AWS_REGION");
        String accessKey = System.getenv("AWS_ACCESS_KEY_ID");
        String secretKey = System.getenv("AWS_SECRET_ACCESS_KEY");

        String finalEndpoint = endpoint != null ? endpoint : "http://localhost:4566";
        String finalRegion = region != null ? region : "us-east-1";
        String finalAccessKey = accessKey != null ? accessKey : "test";
        String finalSecretKey = secretKey != null ? secretKey : "test";

        S3Client s3Client = S3Client.builder()
                .region(Region.of(finalRegion))
                .endpointOverride(URI.create(finalEndpoint))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(finalAccessKey,finalSecretKey)))
                .build();

        log.info("S3Client configured with endpoiny: {}",finalEndpoint);

        return s3Client;
    }

}

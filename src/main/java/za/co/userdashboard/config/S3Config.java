package za.co.userdashboard.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

    @Value("${app.s3.region:us-east-1}")
    private String region;

    @Value("${app.s3.endpoint-override:}")
    private String endpointOverride;

    @Value("${aws.access-key-id:test}")
    private String accessKey;

    @Value("${aws.secret-access-key:test}")
    private String secretKey;

    @Bean
    public AmazonS3 amazonS3() {

        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials));

        if (endpointOverride != null && !endpointOverride.isBlank()) {
            builder.withEndpointConfiguration(
                    new AwsClientBuilder.EndpointConfiguration(endpointOverride, region)
            );
            builder.withPathStyleAccessEnabled(true);
        } else {
            builder.withRegion(region);
        }

        return builder.build();
    }
}

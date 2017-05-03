package org.superbiz.moviefun.movies;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.superbiz.moviefun.BlobStore;

@Configuration
public class ApplicationConfiguration {

    @Bean
    @ConfigurationProperties("s3")
    public S3Configuration s3Configuration() {
        return new S3Configuration();
    }

    @Bean
    public BlobStore blobStore(S3Configuration s3Configuration) {
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setSignerOverride("AWSS3V4SignerType");

        S3ClientOptions clientOptions = S3ClientOptions.builder()
                .disableChunkedEncoding()
                .build();

        AWSCredentials credentials = new BasicAWSCredentials(s3Configuration.getAccessKey(), s3Configuration.getSecretKey());
        AmazonS3Client s3Client = new AmazonS3Client(credentials, clientConfiguration);

        s3Client.setEndpoint(s3Configuration.getEndpointUrl());
        s3Client.setS3ClientOptions(clientOptions);

        return new S3Store(s3Client, s3Configuration.getBucketName());
    }
}

package org.superbiz.moviefun.movies;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.apache.tika.io.IOUtils;
import org.superbiz.moviefun.Blob;
import org.superbiz.moviefun.BlobStore;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

public class S3Store implements BlobStore {

    private final AmazonS3Client s3Client;
    private final String s3BucketName;

    public S3Store(AmazonS3Client s3Client, String s3BucketName) {
        this.s3Client = s3Client;
        this.s3BucketName = s3BucketName;
    }

    @Override
    public void put(Blob blob) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();

        metadata.setContentType(blob.contentType);
        byte[] bytes = IOUtils.toByteArray(blob.inputStream);

        metadata.setContentLength(bytes.length);

        s3Client.putObject(s3BucketName, blob.name, new ByteArrayInputStream(bytes), metadata);
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        try {
            S3Object object = s3Client.getObject(s3BucketName, name);
            return Optional.of(new Blob(object.getKey(), object.getObjectContent(), object.getObjectMetadata().getContentType()));
        } catch (SdkClientException e) {
            return Optional.empty();
        }
    }
}

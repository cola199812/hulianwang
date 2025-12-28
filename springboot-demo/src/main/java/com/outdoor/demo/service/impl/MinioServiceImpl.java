package com.outdoor.demo.service.impl;

import com.outdoor.demo.service.MinioService;
import io.minio.*;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Service
public class MinioServiceImpl implements MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;
    
    @Value("${minio.endpoint}")
    private String endpoint;

    public MinioServiceImpl(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    private void ensureBucket() {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                // Set policy to public read (optional, for demo convenience)
                // In production, better use presigned GET urls or Nginx proxy
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String uploadFile(MultipartFile file, String objectName) {
        try {
            ensureBucket();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
            return getObjectUrl(objectName);
        } catch (Exception e) {
            throw new RuntimeException("MinIO upload failed", e);
        }
    }

    @Override
    public String uploadFile(InputStream stream, String objectName, String contentType) {
        try {
            ensureBucket();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(stream, -1, 10485760)
                            .contentType(contentType)
                            .build());
            return getObjectUrl(objectName);
        } catch (Exception e) {
            throw new RuntimeException("MinIO upload failed", e);
        }
    }

    @Override
    public String getPresignedUrl(String objectName) {
        try {
            ensureBucket();
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.PUT)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(10, TimeUnit.MINUTES)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException("MinIO presigned url failed", e);
        }
    }

    @Override
    public String getObjectUrl(String objectName) {
        // Simple concat for demo. 
        // If MinIO is on localhost:9000, this returns http://localhost:9000/bucket/object
        return endpoint + "/" + bucketName + "/" + objectName;
    }
}

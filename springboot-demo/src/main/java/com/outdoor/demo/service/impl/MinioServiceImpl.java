package com.outdoor.demo.service.impl;

import com.outdoor.demo.service.MinioService;
import io.minio.*;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Service
public class MinioServiceImpl implements MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;
    
    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.external-endpoint:}")
    private String externalEndpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    private MinioClient externalClient;

    public MinioServiceImpl(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @PostConstruct
    public void init() {
        // Initialize external client if endpoint is different
        if (externalEndpoint != null && !externalEndpoint.isEmpty() && !externalEndpoint.equals(endpoint)) {
            try {
                this.externalClient = MinioClient.builder()
                        .endpoint(externalEndpoint)
                        .credentials(accessKey, secretKey)
                        .build();
            } catch (Exception e) {
                System.err.println("Failed to init external MinioClient: " + e.getMessage());
                this.externalClient = this.minioClient;
            }
        } else {
            this.externalClient = this.minioClient;
        }

        ensureBucket();
        setBucketPolicy();
    }

    private void ensureBucket() {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setBucketPolicy() {
        try {
            String policy = "{\n" +
                    "  \"Version\": \"2012-10-17\",\n" +
                    "  \"Statement\": [\n" +
                    "    {\n" +
                    "      \"Effect\": \"Allow\",\n" +
                    "      \"Principal\": {\"AWS\": [\"*\"]},\n" +
                    "      \"Action\": [\"s3:GetObject\"],\n" +
                    "      \"Resource\": [\"arn:aws:s3:::" + bucketName + "/*\"]\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";
            minioClient.setBucketPolicy(
                    SetBucketPolicyArgs.builder().bucket(bucketName).config(policy).build());
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
            return externalClient.getPresignedObjectUrl(
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
        // Use external endpoint for frontend access
        String baseUrl = (externalEndpoint != null && !externalEndpoint.isEmpty()) ? externalEndpoint : endpoint;
        return baseUrl + "/" + bucketName + "/" + objectName;
    }

    @Override
    public String getInternalUrl(String url) {
        if (url == null) return null;
        if (externalEndpoint != null && !externalEndpoint.isEmpty() && url.startsWith(externalEndpoint)) {
            return url.replace(externalEndpoint, endpoint);
        }
        // Also handle localhost explicitly just in case externalEndpoint is not set but url is localhost
        if (url.startsWith("http://localhost:9000")) {
            return url.replace("http://localhost:9000", endpoint);
        }
        return url;
    }
}

package com.outdoor.demo.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;

public interface MinioService {
    String uploadFile(MultipartFile file, String objectName);
    String uploadFile(InputStream stream, String objectName, String contentType);
    String getPresignedUrl(String objectName);
    String getObjectUrl(String objectName);
    String getInternalUrl(String url);
}

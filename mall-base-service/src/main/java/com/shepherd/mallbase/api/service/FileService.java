package com.shepherd.mallbase.api.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2020/12/29 19:09
 */
public interface FileService {
    String uploadFileToOSS(MultipartFile file);

    String downloadOSSFile(String fileName);

    void deleteOSSFile(String fileName);
}

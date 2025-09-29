package com.jupiter.chatweb.service.impl;

import com.jupiter.chatweb.util.AjaxResult;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileUploadService {
    @Value("${file.upload-dir}")
    private String uploadDir;

    public AjaxResult<String> uploadFile(MultipartFile file, HttpServletRequest request, String type) {
        try {
            // 验证文件类型
            if (!validateFileType(file.getContentType())) {
                return AjaxResult.error("只支持JPG/PNG格式");
            }

            // 验证文件大小
            if (file.getSize() > 2 * 1024 * 1024) {
                return AjaxResult.error("文件大小不能超过2MB");
            }

            // 生成存储路径
            String filePath = buildFilePath(file, type);

            // 保存文件
            Path targetPath = Paths.get(uploadDir)
                    .resolve(type).toAbsolutePath().normalize();
            Files.createDirectories(targetPath);
            Files.copy(file.getInputStream(), targetPath.resolve(filePath),
                    StandardCopyOption.REPLACE_EXISTING);

            // 生成访问URL
            String fileUrl = buildFileUrl(request, type, filePath);

            return AjaxResult.success(fileUrl);
        } catch (IOException e) {
            return AjaxResult.error("文件上传失败");
        }
    }

    private boolean validateFileType(String contentType) {
        return "image/jpeg".equals(contentType) || "image/png".equals(contentType);
    }

    private String buildFilePath(MultipartFile file, String type) {
        return String.format("%s_%s.%s",
                UUID.randomUUID().toString(),
                System.currentTimeMillis(),
                FilenameUtils.getExtension(file.getOriginalFilename()));
    }

    private String buildFileUrl(HttpServletRequest request, String type, String filePath) {
        return String.format("%s://%s:%s/uploads/%s/%s",
                request.getScheme(),
                request.getServerName(),
                request.getServerPort(),
                type,
                filePath);
    }
}
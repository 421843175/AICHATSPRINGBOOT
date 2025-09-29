package com.jupiter.chatweb.controller;

import com.jupiter.chatweb.service.impl.FileUploadService;
import com.jupiter.chatweb.util.AjaxResult;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
@CrossOrigin("*")
public class UploadController {

    @Autowired
    private FileUploadService fileUploadService;

    @PostMapping("/avatar")
    public AjaxResult<String> uploadAvatar(@RequestParam("file") MultipartFile file,
                                           HttpServletRequest request) {
        return fileUploadService.uploadFile(file, request, "avatar");
    }

    @PostMapping("/goods")
    public AjaxResult<String> uploadGoods(@RequestParam("file") MultipartFile file,
                                          HttpServletRequest request) {
        return fileUploadService.uploadFile(file, request, "goods");
    }
}
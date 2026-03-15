package com.weasudo.controller;

import com.weasudo.exception.BusinessException;
import com.weasudo.common.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static com.weasudo.common.ResponseCodeEnum.*;

@Slf4j
@RestController
@RequestMapping("/upload")
public class UploadController {

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/avatars/";

    @PostMapping("/avatar")
    public Response<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            log.warn("上传头像失败: 文件为空");
            throw new BusinessException(FILE_NOT_FOUND);
        }

        try {
            // 使用绝对路径

            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String originalFilename = file.getOriginalFilename();
            String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = System.currentTimeMillis() + "_" + UUID.randomUUID() + ext;

            File dest = new File(dir, fileName);
            file.transferTo(dest);

            String avatarUrl = "http://localhost:8080/uploads/avatars/" + fileName;
            log.info("上传头像成功: fileName={}",fileName);
            return Response.success(avatarUrl);
        } catch (IOException e) {
            log.warn("上传头像失败: {}", e.getMessage());
            throw new BusinessException(UPLOAD_FILE_FAILED);
        }

    }
}

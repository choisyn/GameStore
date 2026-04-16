package com.gamestore.service;

import com.gamestore.exception.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class PostImageStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final int MAX_FILE_COUNT = 9;

    private final Path uploadRoot;

    public PostImageStorageService(@Value("${file.upload.path:uploads/}") String uploadPath) {
        this.uploadRoot = Paths.get(uploadPath).toAbsolutePath().normalize();
    }

    public List<String> storeForumPostImages(MultipartFile[] files) {
        if (files == null || files.length == 0) {
            throw new CustomException("请至少选择一张图片");
        }
        if (files.length > MAX_FILE_COUNT) {
            throw new CustomException("单次最多上传 9 张图片");
        }

        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            imageUrls.add(storeSingleImage(file));
        }
        return imageUrls;
    }

    private String storeSingleImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CustomException("上传图片不能为空");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new CustomException("单张图片大小不能超过 5MB");
        }

        String extension = getExtension(file.getOriginalFilename());
        String contentType = file.getContentType();
        if (!ALLOWED_EXTENSIONS.contains(extension)
                || contentType == null
                || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new CustomException("仅支持 jpg、jpeg、png、gif、webp 格式图片");
        }

        String dateFolder = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        Path targetDirectory = uploadRoot.resolve(Paths.get("forum", dateFolder)).normalize();

        try {
            Files.createDirectories(targetDirectory);

            String fileName = UUID.randomUUID() + "." + extension;
            Path targetFile = targetDirectory.resolve(fileName).normalize();
            if (!targetFile.startsWith(targetDirectory)) {
                throw new CustomException("上传路径无效");
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetFile, StandardCopyOption.REPLACE_EXISTING);
            }

            return "/uploads/forum/" + dateFolder + "/" + fileName;
        } catch (IOException e) {
            throw new CustomException("图片保存失败", e);
        }
    }

    private String getExtension(String originalFilename) {
        String extension = StringUtils.getFilenameExtension(originalFilename);
        if (!StringUtils.hasText(extension)) {
            throw new CustomException("图片文件缺少扩展名");
        }
        return extension.toLowerCase(Locale.ROOT);
    }
}

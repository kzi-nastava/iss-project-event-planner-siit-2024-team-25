package com.team25.event.planner.common.util;

import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class FileUtils {
    public static String getExtensionOrDefault(MultipartFile file, String defaultExtension) {
        final String originalFilename = file.getOriginalFilename() == null
                ? "default." + defaultExtension
                : file.getOriginalFilename();
        String extension = StringUtils.getFilenameExtension(originalFilename);
        if (extension == null) {
            extension = defaultExtension;
        }
        return extension;
    }

    public static boolean isImage(@NonNull MultipartFile file) {
        // validate mime type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return false;
        }

        // validate file contents (slower but secure)
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            return image != null;
        } catch (IOException e) {
            return false;
        }
    }
}

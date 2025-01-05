package com.team25.event.planner.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

public class FileUtils {
    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

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

    public static void deleteFiles(Path location, Collection<String> filenames) {
        for (String filename : filenames) {
            try {
                Files.deleteIfExists(location.resolve(filename));
            } catch (IOException cleanupException) {
                logger.error("Failed to delete file during cleanup: {}", filename);
            }
        }
    }
}

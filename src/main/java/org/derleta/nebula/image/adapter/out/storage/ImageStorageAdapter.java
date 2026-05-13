package org.derleta.nebula.image.adapter.out.storage;

import org.derleta.nebula.image.application.port.out.ImageStoragePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Outbound storage adapter that persists avatar images to the local file system.
 * Implements {@link ImageStoragePort}.
 */
@Component
public class ImageStorageAdapter implements ImageStoragePort {

    @Value("${image.avatar.path}")
    private String avatarPath;

    @Override
    public boolean store(final long userId, final MultipartFile image) {
        if (!createDirectoryIfNotExists(avatarPath)) return false;
        try {
            saveImageAsJpg(image, new File(avatarPath), userId);
            return true;
        } catch (IOException e) {
            System.err.println("Error processing or saving image: " + e.getMessage());
            return false;
        }
    }

    protected boolean createDirectoryIfNotExists(final String path) {
        Path directory = Paths.get(path);
        if (Files.exists(directory)) {
            return Files.isDirectory(directory) && Files.isWritable(directory);
        }
        try {
            Files.createDirectories(directory);
            return true;
        } catch (IOException e) {
            System.err.println("Failed to create directory: " + path + " — " + e.getMessage());
            return false;
        }
    }

    protected void saveImageAsJpg(final MultipartFile image, final File directory, final long userId) throws IOException {
        byte[] file = ImageUtil.cropAndConvertToJpg(image);
        String fileName = String.format("%s/%s.jpg", directory.getAbsolutePath(), userId);
        ImageUtil.saveAsJpg(file, fileName);
    }
}


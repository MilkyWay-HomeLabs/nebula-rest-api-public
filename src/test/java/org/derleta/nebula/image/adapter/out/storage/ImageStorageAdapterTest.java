package org.derleta.nebula.image.adapter.out.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ImageStorageAdapterTest {

    @TempDir
    Path tempDir;

    ImageStorageAdapter adapter;

    @BeforeEach
    void setUp() throws Exception {
        adapter = new ImageStorageAdapter();
        var field = ImageStorageAdapter.class.getDeclaredField("avatarPath");
        field.setAccessible(true);
        field.set(adapter, tempDir.toString());
    }

    @Test
    void store_valid_image_returns_true() throws IOException {
        InputStream is = getClass().getResourceAsStream("/test/test-image.png");
        MockMultipartFile file = new MockMultipartFile("file", "test-image.png", "image/png", is);

        boolean result = adapter.store(42L, file);

        assertTrue(result);
        assertTrue(tempDir.resolve("42.jpg").toFile().exists());
    }

    @Test
    void store_invalid_path_returns_false() throws Exception {
        ImageStorageAdapter adapterWithBadPath = new ImageStorageAdapter();
        var field = ImageStorageAdapter.class.getDeclaredField("avatarPath");
        field.setAccessible(true);

        // A regular file used as avatar directory → not a directory → should return false
        Path notADirectory = tempDir.resolve("avatar-path-file.txt");
        Files.writeString(notADirectory, "x");
        field.set(adapterWithBadPath, notADirectory.toString());

        InputStream is = getClass().getResourceAsStream("/test/test-image.png");
        MockMultipartFile file = new MockMultipartFile("file", "test-image.png", "image/png", is);

        boolean result = adapterWithBadPath.store(42L, file);

        assertFalse(result);
    }

    @Test
    void createDirectoryIfNotExists_creates_new_directory() {
        String newPath = tempDir.resolve("newdir").toString();
        assertTrue(adapter.createDirectoryIfNotExists(newPath));
    }
}


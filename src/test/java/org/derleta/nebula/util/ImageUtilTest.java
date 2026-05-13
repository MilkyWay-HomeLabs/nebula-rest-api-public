package org.derleta.nebula.util;

import org.derleta.nebula.image.adapter.out.storage.ImageUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;


class ImageUtilTest {

    @Test
    void cropAndConvertToJpg_validImage_shouldReturnJpgBytes() throws IOException {
        // Arrange
        BufferedImage inputImage = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
        ImageIO.write(inputImage, "png", imageStream);
        MultipartFile mockImage = new MockMultipartFile("testImage", "testImage.png", "image/png", imageStream.toByteArray());

        // Act
        byte[] result = ImageUtil.cropAndConvertToJpg(mockImage);
        BufferedImage resultImage = ImageIO.read(new ByteArrayInputStream(result));

        // Assert
        Assertions.assertNotNull(resultImage);
        Assertions.assertEquals(512, resultImage.getWidth());
        Assertions.assertEquals(512, resultImage.getHeight());
    }

    @Test
    void cropAndConvertToJpg_imageAlreadySquare_shouldMaintainDimensions() throws IOException {
        // Arrange
        BufferedImage squareImage = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
        ImageIO.write(squareImage, "jpeg", imageStream);
        MultipartFile mockImage = new MockMultipartFile("squareImage", "squareImage.jpeg", "image/jpeg", imageStream.toByteArray());

        // Act
        byte[] result = ImageUtil.cropAndConvertToJpg(mockImage);
        BufferedImage resultImage = ImageIO.read(new ByteArrayInputStream(result));

        // Assert
        Assertions.assertNotNull(resultImage);
        Assertions.assertEquals(500, resultImage.getWidth());
        Assertions.assertEquals(500, resultImage.getHeight());
    }

    @Test
    void cropAndConvertToJpg_largeImage_shouldScaleDown() throws IOException {
        // Arrange
        BufferedImage largeImage = new BufferedImage(2000, 1500, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
        ImageIO.write(largeImage, "jpeg", imageStream);
        MultipartFile mockImage = new MockMultipartFile("largeImage", "largeImage.jpeg", "image/jpeg", imageStream.toByteArray());

        // Act
        byte[] result = ImageUtil.cropAndConvertToJpg(mockImage);
        BufferedImage resultImage = ImageIO.read(new ByteArrayInputStream(result));

        // Assert
        Assertions.assertNotNull(resultImage);
        Assertions.assertTrue(resultImage.getWidth() <= 1024);
        Assertions.assertTrue(resultImage.getHeight() <= 1024);
    }

    @Test
    void cropAndConvertToJpg_invalidImage_shouldThrowIOException() throws IOException {
        // Arrange
        MultipartFile mockImage = Mockito.mock(MultipartFile.class);
        Mockito.when(mockImage.getBytes()).thenReturn(null);

        // Act & Assert
        Assertions.assertThrows(IOException.class, () -> ImageUtil.cropAndConvertToJpg(mockImage));
    }

    @Test
    void cropAndConvertToJpg_invalidFormatImage_shouldThrowIOException() {
        // Arrange
        MultipartFile mockFile = new MockMultipartFile("invalidImage", "invalidImage.txt", "text/plain", "Invalid image content".getBytes());

        // Act & Assert
        Assertions.assertThrows(IOException.class, () -> ImageUtil.cropAndConvertToJpg(mockFile));
    }

    @Test
    void isValidImageFile_validImage_shouldReturnTrue() {
        // Arrange
        MultipartFile mockImage = new MockMultipartFile("validImage", "validImage.jpg", "image/jpeg", new byte[]{});

        // Act
        boolean result = ImageUtil.isValidImageFile(mockImage);

        // Assert
        Assertions.assertTrue(result, "Expected valid image file to return true");
    }

    @Test
    void isValidImageFile_invalidExtension_shouldReturnFalse() {
        // Arrange
        MultipartFile mockImage = new MockMultipartFile("invalidImage", "invalidImage.txt", "text/plain", new byte[]{});

        // Act
        boolean result = ImageUtil.isValidImageFile(mockImage);

        // Assert
        Assertions.assertFalse(result, "Expected invalid image file to return false");
    }

    @Test
    void isValidImageFile_noExtension_shouldReturnFalse() {
        // Arrange
        MultipartFile mockImage = new MockMultipartFile("noExtensionImage", "noExtensionImage", "image/jpeg", new byte[]{});

        // Act
        boolean result = ImageUtil.isValidImageFile(mockImage);

        // Assert
        Assertions.assertFalse(result, "Expected image file with no extension to return false");
    }

    @Test
    void isValidImageFile_nullFilename_shouldReturnFalse() {
        // Arrange
        MultipartFile mockImage = Mockito.mock(MultipartFile.class);
        Mockito.when(mockImage.getOriginalFilename()).thenReturn(null);

        // Act
        boolean result = ImageUtil.isValidImageFile(mockImage);

        // Assert
        Assertions.assertFalse(result, "Expected image file with null filename to return false");
    }

    @Test
    void getFileExtension_validFile_shouldReturnExtension() {
        // Arrange
        MultipartFile mockImage = new MockMultipartFile("validFile", "fileName.png", "image/png", new byte[]{});

        // Act
        String result = ImageUtil.getFileExtension(mockImage);

        // Assert
        Assertions.assertEquals("png", result, "Expected file extension to be 'png'");
    }

    @Test
    void getFileExtension_fileWithoutExtension_shouldReturnEmptyString() {
        // Arrange
        MultipartFile mockImage = new MockMultipartFile("noExtensionFile", "fileName", "text/plain", new byte[]{});

        // Act
        String result = ImageUtil.getFileExtension(mockImage);

        // Assert
        Assertions.assertEquals("", result, "Expected file extension to be an empty string");
    }

    @Test
    void getFileExtension_nullFilename_shouldReturnEmptyString() {
        // Arrange
        MultipartFile mockImage = Mockito.mock(MultipartFile.class);
        Mockito.when(mockImage.getOriginalFilename()).thenReturn(null);

        // Act
        String result = ImageUtil.getFileExtension(mockImage);

        // Assert
        Assertions.assertEquals("", result, "Expected file extension to be an empty string when filename is null");
    }

    @Test
    void getFileExtension_fileWithMultipleDots_shouldReturnLastExtension() {
        // Arrange
        MultipartFile mockImage = new MockMultipartFile("multiDotFile", "file.name.with.dots.jpeg", "image/jpeg", new byte[]{});

        // Act
        String result = ImageUtil.getFileExtension(mockImage);

        // Assert
        Assertions.assertEquals("jpeg", result, "Expected file extension to be 'jpeg'");
    }

    @Test
    void getFileExtension_emptyFilename_shouldReturnEmptyString() {
        // Arrange
        MultipartFile mockImage = Mockito.mock(MultipartFile.class);
        Mockito.when(mockImage.getOriginalFilename()).thenReturn("");

        // Act
        String result = ImageUtil.getFileExtension(mockImage);

        // Assert
        Assertions.assertEquals("", result, "Expected file extension to be an empty string when filename is empty");
    }

    @Test
    void saveAsJpg_validImage_shouldSaveToFile() throws IOException {
        // Arrange
        byte[] validImageData = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF}; // mock JPG header bytes
        String tempOutputPath = "testImage.jpg";

        // Act
        ImageUtil.saveAsJpg(validImageData, tempOutputPath);
        File savedFile = new File(tempOutputPath);

        // Assert
        Assertions.assertTrue(savedFile.exists(), "Saved file should exist");
        Assertions.assertEquals("jpg", tempOutputPath.substring(tempOutputPath.lastIndexOf(".") + 1), "File extension should be 'jpg'");

        // Clean up
        var result = savedFile.delete();
    }

    @Test
    void saveAsJpg_invalidOutputPath_shouldThrowIOException() {
        // Arrange
        byte[] validImageData = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF}; // mock JPG header bytes
        String invalidOutputPath = "/invalidPath/testImage.jpg";

        // Act & Assert
        Assertions.assertThrows(IOException.class, () -> ImageUtil.saveAsJpg(validImageData, invalidOutputPath),
                "Expected an IOException when writing to an invalid path");
    }

    @Test
    void saveAsJpg_shouldCreateReadableAndValidJpgFile() throws IOException {
        // Arrange
        byte[] validImageData = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF}; // mock JPG header bytes
        String tempOutputPath = "testImage.jpg";

        // Act
        ImageUtil.saveAsJpg(validImageData, tempOutputPath);
        File savedFile = new File(tempOutputPath);

        // Assert
        Assertions.assertTrue(savedFile.canRead(), "The saved file should be readable");

        try (FileInputStream fis = new FileInputStream(savedFile)) {
            byte[] fileHeader = new byte[3];
            int bytesRead = fis.read(fileHeader);

            Assertions.assertEquals(3, bytesRead, "Expected to read 3 bytes from the file");
            Assertions.assertArrayEquals(new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF}, fileHeader, "File header should match JPG format");
        } finally {
            var result = savedFile.delete();
        }
    }

}

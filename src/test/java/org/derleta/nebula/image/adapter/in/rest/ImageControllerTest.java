package org.derleta.nebula.image.adapter.in.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.derleta.nebula.shared.domain.exception.TokenExpiredException;
import org.derleta.nebula.image.application.port.in.UploadImageUseCase;
import org.derleta.nebula.shared.security.TokenProvider;
import org.derleta.nebula.image.adapter.out.storage.ImageUtil;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageControllerTest {

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private UploadImageUseCase uploadImageUseCase;

    @InjectMocks
    private ImageController imageController;

    private String validAccessToken;
    private String invalidAccessToken;
    private long userId;
    private MockMultipartFile validImageFile;
    private MockMultipartFile invalidImageFile;
    private MockMultipartFile emptyImageFile;

    @BeforeEach
    void setUp() {
        validAccessToken = "valid.access.token";
        invalidAccessToken = "invalid.access.token";
        userId = 123L;

        validImageFile = new MockMultipartFile(
                "file", "test-image.jpg", "image/jpeg", "test image content".getBytes());

        invalidImageFile = new MockMultipartFile(
                "file", "test-document.txt", "text/plain", "test document content".getBytes());

        emptyImageFile = new MockMultipartFile(
                "file", "empty.jpg", "image/jpeg", new byte[0]);
    }

    @Test
    void upload_withInvalidToken_returnsUnauthorized() {
        when(tokenProvider.isValid(invalidAccessToken)).thenReturn(false);

        ResponseEntity<String> response = imageController.upload(invalidAccessToken, validImageFile);

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(tokenProvider).isValid(invalidAccessToken);
        verify(tokenProvider, never()).getUserId(any());
        verify(uploadImageUseCase, never()).uploadImage(anyLong(), any(MultipartFile.class));
    }

    @Test
    void upload_withValidTokenAndValidImage_returnsCreated() {
        try (var mockedStatic = mockStatic(ImageUtil.class)) {
            when(tokenProvider.isValid(validAccessToken)).thenReturn(true);
            when(tokenProvider.getUserId(validAccessToken)).thenReturn(userId);
            mockedStatic.when(() -> ImageUtil.isValidImageFile(validImageFile)).thenReturn(true);
            when(uploadImageUseCase.uploadImage(userId, validImageFile)).thenReturn(true);

            ResponseEntity<String> response = imageController.upload(validAccessToken, validImageFile);

            assertNotNull(response);
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertEquals("Image has been saved", response.getBody());
            verify(uploadImageUseCase).uploadImage(userId, validImageFile);
        }
    }

    @Test
    void upload_withValidTokenAndValidImageButUpdateFails_returnsForbidden() {
        try (var mockedStatic = mockStatic(ImageUtil.class)) {
            when(tokenProvider.isValid(validAccessToken)).thenReturn(true);
            when(tokenProvider.getUserId(validAccessToken)).thenReturn(userId);
            mockedStatic.when(() -> ImageUtil.isValidImageFile(validImageFile)).thenReturn(true);
            when(uploadImageUseCase.uploadImage(userId, validImageFile)).thenReturn(false);

            ResponseEntity<String> response = imageController.upload(validAccessToken, validImageFile);

            assertNotNull(response);
            assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
            assertEquals("Image has not been saved", response.getBody());
            verify(uploadImageUseCase).uploadImage(userId, validImageFile);
        }
    }

    @Test
    void upload_withValidTokenAndEmptyImage_returnsBadRequest() {
        when(tokenProvider.isValid(validAccessToken)).thenReturn(true);
        when(tokenProvider.getUserId(validAccessToken)).thenReturn(userId);

        ResponseEntity<String> response = imageController.upload(validAccessToken, emptyImageFile);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Image is empty", response.getBody());
        verify(uploadImageUseCase, never()).uploadImage(anyLong(), any(MultipartFile.class));
    }

    @Test
    void upload_withValidTokenAndInvalidImageType_returnsBadRequest() {
        try (var mockedStatic = mockStatic(ImageUtil.class)) {
            when(tokenProvider.isValid(validAccessToken)).thenReturn(true);
            when(tokenProvider.getUserId(validAccessToken)).thenReturn(userId);
            mockedStatic.when(() -> ImageUtil.isValidImageFile(invalidImageFile)).thenReturn(false);

            ResponseEntity<String> response = imageController.upload(validAccessToken, invalidImageFile);

            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Unsupported image file, there are supported: jpg, jpeg, png", response.getBody());
            verify(uploadImageUseCase, never()).uploadImage(anyLong(), any(MultipartFile.class));
        }
    }

    @Test
    void upload_expiredToken_throwsTokenExpiredException() {
        when(tokenProvider.isValid("expired-token"))
                .thenThrow(new TokenExpiredException("TOKEN_EXPIRED"));

        assertThrows(TokenExpiredException.class,
                () -> imageController.upload("expired-token", validImageFile));

        verify(tokenProvider).isValid("expired-token");
        verify(tokenProvider, never()).getUserId(any());
    }
}


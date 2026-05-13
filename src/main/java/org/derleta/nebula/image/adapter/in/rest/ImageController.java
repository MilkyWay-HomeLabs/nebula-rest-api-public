package org.derleta.nebula.image.adapter.in.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.derleta.nebula.image.application.port.in.UploadImageUseCase;
import org.derleta.nebula.shared.security.TokenProvider;
import org.derleta.nebula.image.adapter.out.storage.ImageUtil;

/** Inbound REST adapter for avatar image upload. */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public final class ImageController {

    public static final String DEFAULT_PATH = "image";

    private final TokenProvider tokenProvider;
    private final UploadImageUseCase uploadImageUseCase;

    /**
     * Uploads a multipart image file for the authenticated user.
     * Returns 401 UNAUTHORIZED if the token is invalid.
     */
    @PostMapping(value = DEFAULT_PATH, produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> upload(@CookieValue("accessToken") String accessToken,
                                         @RequestPart("file") MultipartFile image) {
        if (tokenProvider.isValid(accessToken)) {
            long userId = tokenProvider.getUserId(accessToken);
            return getImageResponse(userId, image);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Validates and delegates image persistence.
     * Returns 400 BAD_REQUEST for empty or unsupported files,
     * 201 CREATED on success, 403 FORBIDDEN on storage failure.
     */
    private ResponseEntity<String> getImageResponse(long userId, MultipartFile image) {
        if (image.isEmpty()) {
            return ResponseEntity.badRequest().body("Image is empty");
        }
        if (!ImageUtil.isValidImageFile(image)) {
            return ResponseEntity.badRequest()
                    .body("Unsupported image file, there are supported: jpg, jpeg, png");
        }
        if (uploadImageUseCase.uploadImage(userId, image)) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Image has been saved");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Image has not been saved");
    }
}


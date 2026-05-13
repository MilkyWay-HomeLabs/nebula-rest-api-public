package org.derleta.nebula.image.application.port.in;

import org.springframework.web.multipart.MultipartFile;

/** Input port: upload (or replace) a user's avatar image. */
public interface UploadImageUseCase {

    /**
     * Uploads and persists an image for the given user.
     *
     * @param userId the owner of the avatar
     * @param image  the multipart file to store
     * @return {@code true} if the image was stored successfully, {@code false} otherwise
     */
    boolean uploadImage(long userId, MultipartFile image);
}


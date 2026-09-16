package org.derleta.nebula.image.application.port.out;

import org.springframework.web.multipart.MultipartFile;

/** Output port: stores an avatar image in the underlying file system or object storage. */
public interface ImageStoragePort {

    /**
     * Persists the provided image for the given user.
     *
     * @param userId the owner of the avatar
     * @param image  the multipart file to persist
     * @return {@code true} if the image was stored successfully, {@code false} otherwise
     */
    boolean store(long userId, MultipartFile image);
}


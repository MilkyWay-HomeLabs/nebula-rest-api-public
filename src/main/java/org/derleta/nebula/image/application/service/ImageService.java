package org.derleta.nebula.image.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.derleta.nebula.image.application.port.in.UploadImageUseCase;
import org.derleta.nebula.image.application.port.out.ImageStoragePort;
import org.derleta.nebula.shared.application.port.out.MetricsPort;

import java.util.function.Supplier;

/** Application service that orchestrates avatar image uploads. */
@Service
@RequiredArgsConstructor
public class ImageService implements UploadImageUseCase {

    private static final String UPLOAD_OPERATION = "upload";
    private static final String IMAGE_OPERATIONS_METRIC = "nebula.image.operations";
    private static final String IMAGE_OPERATION_DURATION_METRIC = "nebula.image.operation.duration";
    private static final String IMAGE_UPLOAD_BYTES_METRIC = "nebula.image.upload.bytes";

    private final ImageStoragePort imageStoragePort;
    private final MetricsPort metricsPort;

    @Override
    public boolean uploadImage(final long userId, final MultipartFile image) {
        return recordUploadOperation(() -> {
            if (image != null) {
                metricsPort.recordValue(IMAGE_UPLOAD_BYTES_METRIC, image.getSize(), "operation", UPLOAD_OPERATION);
            }
            return imageStoragePort.store(userId, image);
        });
    }

    private boolean recordUploadOperation(Supplier<Boolean> action) {
        metricsPort.incrementCounter(IMAGE_OPERATIONS_METRIC,
                "operation", UPLOAD_OPERATION,
                "stage", "attempt");
        long start = System.nanoTime();
        try {
            boolean result = Boolean.TRUE.equals(action.get());
            metricsPort.incrementCounter(IMAGE_OPERATIONS_METRIC,
                    "operation", UPLOAD_OPERATION,
                    "stage", result ? "success" : "failure");
            return result;
        } catch (RuntimeException exception) {
            metricsPort.incrementCounter(IMAGE_OPERATIONS_METRIC,
                    "operation", UPLOAD_OPERATION,
                    "stage", "error");
            throw exception;
        } finally {
            metricsPort.recordDuration(IMAGE_OPERATION_DURATION_METRIC,
                    System.nanoTime() - start,
                    "operation", UPLOAD_OPERATION);
        }
    }
}


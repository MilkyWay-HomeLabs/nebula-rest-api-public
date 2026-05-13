package org.derleta.nebula.image.application.service;

import org.derleta.nebula.image.application.port.out.ImageStoragePort;
import org.derleta.nebula.shared.application.port.out.MetricsPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    private ImageStoragePort imageStoragePort;

    @Mock
    private MetricsPort metricsPort;

    @InjectMocks
    private ImageService imageService;

    @Test
    void uploadImage_success_recordsMetricsAndBytes() {
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "avatar.png",
                "image/png",
                new byte[]{1, 2, 3, 4}
        );
        when(imageStoragePort.store(7L, image)).thenReturn(true);

        boolean result = imageService.uploadImage(7L, image);

        assertTrue(result);
        verify(metricsPort).incrementCounter("nebula.image.operations", "operation", "upload", "stage", "attempt");
        verify(metricsPort).incrementCounter("nebula.image.operations", "operation", "upload", "stage", "success");
        verify(metricsPort).recordValue("nebula.image.upload.bytes", 4.0, "operation", "upload");
        verify(metricsPort).recordDuration(eq("nebula.image.operation.duration"), anyLong(), eq("operation"), eq("upload"));
    }

    @Test
    void uploadImage_failure_recordsFailureMetrics() {
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "avatar.png",
                "image/png",
                new byte[]{9, 8}
        );
        when(imageStoragePort.store(5L, image)).thenReturn(false);

        boolean result = imageService.uploadImage(5L, image);

        assertFalse(result);
        verify(metricsPort).incrementCounter("nebula.image.operations", "operation", "upload", "stage", "attempt");
        verify(metricsPort).incrementCounter("nebula.image.operations", "operation", "upload", "stage", "failure");
        verify(metricsPort).recordValue("nebula.image.upload.bytes", 2.0, "operation", "upload");
        verify(metricsPort).recordDuration(eq("nebula.image.operation.duration"), anyLong(), eq("operation"), eq("upload"));
    }

    @Test
    void uploadImage_error_recordsErrorMetrics() {
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "avatar.png",
                "image/png",
                new byte[]{7, 7, 7}
        );
        when(imageStoragePort.store(9L, image)).thenThrow(new IllegalStateException("storage unavailable"));

        assertThrows(IllegalStateException.class, () -> imageService.uploadImage(9L, image));

        verify(metricsPort).incrementCounter("nebula.image.operations", "operation", "upload", "stage", "attempt");
        verify(metricsPort).incrementCounter("nebula.image.operations", "operation", "upload", "stage", "error");
        verify(metricsPort).recordValue("nebula.image.upload.bytes", 3.0, "operation", "upload");
        verify(metricsPort).recordDuration(eq("nebula.image.operation.duration"), anyLong(), eq("operation"), eq("upload"));
    }
}


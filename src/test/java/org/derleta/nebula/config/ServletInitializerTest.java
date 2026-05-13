package org.derleta.nebula.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.derleta.nebula.NebulaRestApiApplication;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class ServletInitializerTest {

    @Test
    void configure_shouldSetSourcesCorrectly() {
        // Arrange
        ServletInitializer servletInitializer = new ServletInitializer();
        SpringApplicationBuilder applicationBuilder = mock(SpringApplicationBuilder.class);
        when(applicationBuilder.sources(NebulaRestApiApplication.class)).thenReturn(applicationBuilder);

        // Act
        SpringApplicationBuilder result = servletInitializer.configure(applicationBuilder);

        // Assert
        assertNotNull(result);
        verify(applicationBuilder).sources(NebulaRestApiApplication.class);
    }

}
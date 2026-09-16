package org.derleta.nebula.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.HateoasSortHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.data.web.PagedResourcesAssemblerArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WebMvcConfigTest {

    private WebMvcConfig webMvcConfig;
    private List<HandlerMethodArgumentResolver> argumentResolvers;

    @BeforeEach
    void setUp() {
        webMvcConfig = new WebMvcConfig();
        argumentResolvers = new ArrayList<>();
    }

    @Test
    void addArgumentResolvers_shouldAddAllResolvers() {
        // Act
        webMvcConfig.addArgumentResolvers(argumentResolvers);

        // Assert
        assertEquals(3, argumentResolvers.size());
        assertInstanceOf(HateoasPageableHandlerMethodArgumentResolver.class, argumentResolvers.get(0));
        assertTrue(argumentResolvers.get(1) instanceof HateoasSortHandlerMethodArgumentResolver);
        assertTrue(argumentResolvers.get(2) instanceof PagedResourcesAssemblerArgumentResolver);
    }

    @Test
    void sortResolver_shouldReturnHateoasSortHandlerMethodArgumentResolver() {
        // Act
        HateoasSortHandlerMethodArgumentResolver resolver = webMvcConfig.sortResolver();

        // Assert
        assertNotNull(resolver);
        assertTrue(resolver instanceof HateoasSortHandlerMethodArgumentResolver);
    }

    @Test
    void pageableResolver_shouldReturnHateoasPageableHandlerMethodArgumentResolver() {
        // Act
        HateoasPageableHandlerMethodArgumentResolver resolver = webMvcConfig.pageableResolver();

        // Assert
        assertNotNull(resolver);
        assertTrue(resolver instanceof HateoasPageableHandlerMethodArgumentResolver);
    }

    @Test
    void pagedResourcesAssembler_shouldReturnPagedResourcesAssembler() {
        // Act
        PagedResourcesAssembler<?> assembler = webMvcConfig.pagedResourcesAssembler();

        // Assert
        assertNotNull(assembler);
        assertTrue(assembler instanceof PagedResourcesAssembler);
    }

    @Test
    void pagedResourcesAssemblerArgumentResolver_shouldReturnPagedResourcesAssemblerArgumentResolver() {
        // Act
        PagedResourcesAssemblerArgumentResolver resolver = webMvcConfig.pagedResourcesAssemblerArgumentResolver();

        // Assert
        assertNotNull(resolver);
        assertTrue(resolver instanceof PagedResourcesAssemblerArgumentResolver);
    }
}

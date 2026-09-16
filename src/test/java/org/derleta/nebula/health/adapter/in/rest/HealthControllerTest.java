package org.derleta.nebula.health.adapter.in.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.derleta.nebula.health.application.port.in.CheckHealthUseCase;
import org.derleta.nebula.health.application.port.in.GetAppVersionUseCase;
import org.derleta.nebula.shared.security.TokenProvider;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = HealthController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CheckHealthUseCase checkHealthUseCase;

    @MockitoBean
    private GetAppVersionUseCase getAppVersionUseCase;

    @MockitoBean
    private TokenProvider tokenProvider;

    @Test
    void hello_returnsOk() throws Exception {
        when(checkHealthUseCase.check()).thenReturn("hello");

        mockMvc.perform(get("/api/v1/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("hello"));
    }

    @Test
    void version_returnsCurrentVersion() throws Exception {
        when(getAppVersionUseCase.getVersion()).thenReturn("3.4.0-SNAPSHOT");

        mockMvc.perform(get("/api/v1/version"))
                .andExpect(status().isOk())
                .andExpect(content().string("3.4.0-SNAPSHOT"));
    }
}


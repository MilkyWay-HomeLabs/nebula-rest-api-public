package org.derleta.nebula.shared.adapter.in.rest.exception;

import org.derleta.nebula.shared.domain.exception.TokenExpiredException;
import org.derleta.nebula.health.adapter.in.rest.HealthController;
import org.derleta.nebula.health.application.port.in.CheckHealthUseCase;
import org.derleta.nebula.health.application.port.in.GetAppVersionUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.derleta.nebula.shared.security.TokenProvider;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = HealthController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class GlobalExceptionHandlerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CheckHealthUseCase checkHealthUseCase;

    @MockitoBean
    private GetAppVersionUseCase getAppVersionUseCase;

    @MockitoBean
    private TokenProvider tokenProvider;

    @Test
    void testTokenExpiredExceptionHandling() throws Exception {
        when(checkHealthUseCase.check()).thenThrow(new TokenExpiredException("ACCESS_TOKEN_EXPIRED"));

        mockMvc.perform(get("/api/v1/hello"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("ACCESS_TOKEN_EXPIRED"))
                .andExpect(jsonPath("$.error").value("TOKEN_EXPIRED"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}


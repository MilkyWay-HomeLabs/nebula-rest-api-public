package org.derleta.nebula.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class AppConfigTest {

    @Autowired
    private AppConfig appConfig;

    @Test
    public void corsConfigurationSource_shouldNotBeNull() {
        CorsConfigurationSource corsConfigurationSource = appConfig.corsConfigurationSource();
        assertThat(corsConfigurationSource).isNotNull();
    }

    @Test
    public void restTemplate_shouldNotBeNull() {
        RestTemplate restTemplate = appConfig.restTemplate();
        assertThat(restTemplate).isNotNull();
    }

    @Test
    void corsConfigurationSource_shouldContainExpectedOriginPatterns() {
        CorsConfigurationSource source = appConfig.corsConfigurationSource();

        assertThat(source).isInstanceOf(UrlBasedCorsConfigurationSource.class);
        UrlBasedCorsConfigurationSource urlSource = (UrlBasedCorsConfigurationSource) source;

        CorsConfiguration cors = urlSource.getCorsConfigurations().get("/**");
        assertThat(cors).isNotNull();
        assertThat(cors.getAllowedOriginPatterns()).contains(
                "https://*.milkyway",
                "https://*.test.milkyway",
                "https://*.dev.milkyway"
        );
        assertThat(cors.getAllowedMethods()).contains("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
        assertThat(cors.getAllowedHeaders()).contains("*");
        assertThat(cors.getAllowCredentials()).isTrue();
    }

    @Test
    void corsConfiguration_shouldAllowExpectedOrigins_andRejectInvalidOnes() {
        UrlBasedCorsConfigurationSource source =
                (UrlBasedCorsConfigurationSource) appConfig.corsConfigurationSource();
        CorsConfiguration cors = source.getCorsConfigurations().get("/**");

        assertThat(cors.checkOrigin("https://api.milkyway")).isEqualTo("https://api.milkyway");
        assertThat(cors.checkOrigin("https://app.test.milkyway")).isEqualTo("https://app.test.milkyway");
        assertThat(cors.checkOrigin("https://ui.dev.milkyway")).isEqualTo("https://ui.dev.milkyway");

        assertThat(cors.checkOrigin("http://api.milkyway")).isNull();      // bad protocol
        assertThat(cors.checkOrigin("https://evil.com")).isNull();         // outside allowed patterns
    }

}

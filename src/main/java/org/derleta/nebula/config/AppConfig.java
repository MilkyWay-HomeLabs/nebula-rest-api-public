package org.derleta.nebula.config;

import jakarta.servlet.http.HttpServletResponse;
import org.derleta.nebula.config.security.JwtAuthenticationFilter;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.MissingCsrfTokenException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class AppConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final List<String> corsAllowedOriginPatterns;

    public AppConfig(@Value("${cors.allowed-origin-patterns}") List<String> corsAllowedOriginPatterns,
                     JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.corsAllowedOriginPatterns = corsAllowedOriginPatterns;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/hello", "/api/v1/version").permitAll()

                        .requestMatchers("/api/v1/account/register").permitAll()
                        .requestMatchers("/api/v1/account/confirm").permitAll()
                        .requestMatchers("/api/v1/account/unlock/**").permitAll()
                        .requestMatchers("/api/v1/account/reset-password/**").permitAll()
                        .requestMatchers("/api/v1/account/token").permitAll()
                        .requestMatchers("/api/v1/token/refresh/access").permitAll()

                        .requestMatchers("/api/v1/games", "/api/v1/games/*", "/api/v1/games/enabled").permitAll()
                        .requestMatchers("/api/v1/genders", "/api/v1/genders/*").permitAll()
                        .requestMatchers("/api/v1/nationalities", "/api/v1/nationalities/*").permitAll()
                        .requestMatchers("/api/v1/themes", "/api/v1/themes/*").permitAll()

                        .requestMatchers("/nebula/**").permitAll()

                        .requestMatchers("/api/actuator/health/**", "/api/actuator/info").permitAll()

                        .requestMatchers("/api/actuator/prometheus").authenticated()
                        .requestMatchers("/api/actuator/**").authenticated()

                        .requestMatchers("/api/v1/account/change-password").authenticated()
                        .requestMatchers("/api/v1/token/**").authenticated()
                        .requestMatchers("/api/v1/users/**").authenticated()
                        .requestMatchers("/api/v1/image").authenticated()

                        .requestMatchers(HttpMethod.POST, "/api/v1/games").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/games/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/games/**").authenticated()

                        .anyRequest().denyAll()
                )
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling
                                .authenticationEntryPoint((request, response, exception) -> {
                                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                    response.setContentType("application/json");
                                    response.getWriter().write("""
                                            {"message":"Authentication is required.","error":"UNAUTHORIZED"}
                                            """);
                                })
                                .accessDeniedHandler((request, response, exception) -> {
                                    if (exception instanceof MissingCsrfTokenException) {
                                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                        response.setContentType("application/json");
                                        response.getWriter().write("""
                                                {"message":"CSRF token is missing or invalid.","error":"FORBIDDEN"}
                                                """);
                                    } else {
                                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                        response.setContentType("application/json");
                                        response.getWriter().write("""
                                                {"message":"Access denied.","error":"FORBIDDEN"}
                                                """);
                                    }
                                }))
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = getCorsConfiguration();
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    private @NonNull CorsConfiguration getCorsConfiguration() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsAllowedOriginPatterns.stream()
                .map(String::trim)
                .filter(pattern -> !pattern.isBlank())
                .forEach(corsConfiguration::addAllowedOriginPattern);
        return corsConfiguration;
    }

}

package org.derleta.nebula.health.adapter.in.rest;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.derleta.nebula.health.application.port.in.CheckHealthUseCase;
import org.derleta.nebula.health.application.port.in.GetAppVersionUseCase;

/** REST adapter — exposes health and version endpoints. */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class HealthController {

    private final CheckHealthUseCase checkHealthUseCase;
    private final GetAppVersionUseCase getAppVersionUseCase;

    @Timed(
            value = "http.api.v1.health.hello",
            description = "Time spent handling GET /api/v1/hello",
            percentiles = {0.5, 0.95, 0.99}
    )
    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok(checkHealthUseCase.check());
    }

    @Timed(
            value = "http.api.v1.health.version",
            description = "Time spent handling GET /api/v1/version",
            percentiles = {0.5, 0.95, 0.99}
    )
    @GetMapping("/version")
    public ResponseEntity<String> version() {
        return ResponseEntity.ok(getAppVersionUseCase.getVersion());
    }
}


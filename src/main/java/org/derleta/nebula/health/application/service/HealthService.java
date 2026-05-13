package org.derleta.nebula.health.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.derleta.nebula.health.application.port.in.CheckHealthUseCase;
import org.derleta.nebula.health.application.port.in.GetAppVersionUseCase;

/** Application service implementing health use cases. */
@Service
public class HealthService implements CheckHealthUseCase, GetAppVersionUseCase {

    private final String appVersion;

    public HealthService(@Value("${app.version:unknown}") String appVersion) {
        this.appVersion = appVersion;
    }

    @Override
    public String check() {
        return "hello";
    }

    @Override
    public String getVersion() {
        return appVersion;
    }
}

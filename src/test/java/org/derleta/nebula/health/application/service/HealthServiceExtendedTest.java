package org.derleta.nebula.health.application.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HealthServiceExtendedTest {

    @Test
    void getVersion_returnsConfiguredVersion() {
        HealthService service = new HealthService("4.4.0-SNAPSHOT");
        assertEquals("4.4.0-SNAPSHOT", service.getVersion());
    }

    @Test
    void getVersion_defaultValue_returnsUnknown() {
        HealthService service = new HealthService("unknown");
        assertEquals("unknown", service.getVersion());
    }

    @Test
    void check_returnsHello() {
        HealthService service = new HealthService("1.0");
        assertEquals("hello", service.check());
    }
}


package org.derleta.nebula.shared.application.port.out;

/**
 * Outbound port for recording application and business metrics without coupling
 * the application layer to a concrete monitoring library.
 */
public interface MetricsPort {

    void incrementCounter(String name, String... tags);

    void recordDuration(String name, long durationNanos, String... tags);

    void recordValue(String name, double value, String... tags);
}


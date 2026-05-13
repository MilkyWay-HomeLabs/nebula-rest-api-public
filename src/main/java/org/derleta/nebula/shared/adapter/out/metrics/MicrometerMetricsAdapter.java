package org.derleta.nebula.shared.adapter.out.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.derleta.nebula.shared.application.port.out.MetricsPort;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Micrometer-backed implementation of {@link MetricsPort}.
 */
@Component
@RequiredArgsConstructor
public class MicrometerMetricsAdapter implements MetricsPort {

    private final MeterRegistry meterRegistry;

    @Override
    public void incrementCounter(String name, String... tags) {
        Counter.builder(name)
                .tags(normalizeTags(tags))
                .register(meterRegistry)
                .increment();
    }

    @Override
    public void recordDuration(String name, long durationNanos, String... tags) {
        Timer.builder(name)
                .publishPercentiles(0.5, 0.95, 0.99)
                .tags(normalizeTags(tags))
                .register(meterRegistry)
                .record(durationNanos, TimeUnit.NANOSECONDS);
    }

    @Override
    public void recordValue(String name, double value, String... tags) {
        DistributionSummary.builder(name)
                .publishPercentiles(0.5, 0.95, 0.99)
                .tags(normalizeTags(tags))
                .register(meterRegistry)
                .record(value);
    }

    private String[] normalizeTags(String... tags) {
        if (tags == null || tags.length == 0) {
            return new String[0];
        }
        if (tags.length % 2 != 0) {
            throw new IllegalArgumentException("Metric tags must be provided as key/value pairs.");
        }
        return tags;
    }
}


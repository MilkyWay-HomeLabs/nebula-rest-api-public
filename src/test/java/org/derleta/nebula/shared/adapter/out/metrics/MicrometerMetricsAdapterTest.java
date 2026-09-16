package org.derleta.nebula.shared.adapter.out.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MicrometerMetricsAdapterTest {

	@Test
	void recordsCountersTimersAndValues() {
		SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
		MicrometerMetricsAdapter adapter = new MicrometerMetricsAdapter(meterRegistry);

		adapter.incrementCounter("nebula.test.counter", "operation", "register", "stage", "success");
		adapter.recordDuration("nebula.test.timer", 5_000_000L, "operation", "register");
		adapter.recordValue("nebula.test.bytes", 128.0, "operation", "upload");

		Counter counter = meterRegistry.get("nebula.test.counter")
				.tags("operation", "register", "stage", "success")
				.counter();
		Timer timer = meterRegistry.get("nebula.test.timer")
				.tags("operation", "register")
				.timer();
		DistributionSummary summary = meterRegistry.get("nebula.test.bytes")
				.tags("operation", "upload")
				.summary();

		assertEquals(1.0, counter.count());
		assertEquals(1L, timer.count());
		assertTrue(timer.totalTime(TimeUnit.NANOSECONDS) >= 5_000_000L);
		assertEquals(1L, summary.count());
		assertEquals(128.0, summary.totalAmount());
	}

	@Test
	void rejectsOddNumberOfMetricTags() {
		SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
		MicrometerMetricsAdapter adapter = new MicrometerMetricsAdapter(meterRegistry);

		assertThrows(IllegalArgumentException.class,
				() -> adapter.incrementCounter("nebula.test.counter", "operation", "register", "stage"));
	}
}


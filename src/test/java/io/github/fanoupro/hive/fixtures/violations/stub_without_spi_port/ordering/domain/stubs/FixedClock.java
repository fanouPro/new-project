package io.github.fanoupro.hive.fixtures.violations.stub_without_spi_port.ordering.domain.stubs;

import io.github.fanoupro.hive.fixtures.ddd.Stub;

import java.time.Instant;

@Stub
public class FixedClock {
    public Instant now() {
        return Instant.EPOCH;
    }
}

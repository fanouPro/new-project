package io.github.fanoupro.hive.fixtures.alternative_conventions.booking.domain.spi;

import java.util.List;
import java.util.UUID;

public interface GetTrainsToBook {
    List<String> from(UUID searchId);
}

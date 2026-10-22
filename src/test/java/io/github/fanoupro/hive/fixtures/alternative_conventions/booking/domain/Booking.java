package io.github.fanoupro.hive.fixtures.alternative_conventions.booking.domain;

import java.util.List;
import java.util.UUID;

public record Booking(UUID id, List<String> trains) {
}

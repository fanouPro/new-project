package io.github.fanoupro.hive.fixtures.alternative_conventions.booking.domain.api;

import io.github.fanoupro.hive.fixtures.alternative_conventions.booking.domain.Booking;

import java.util.UUID;

public interface PrepareBooking {
    Booking prepare(UUID searchId);
}

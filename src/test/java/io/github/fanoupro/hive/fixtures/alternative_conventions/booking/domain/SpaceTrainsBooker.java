package io.github.fanoupro.hive.fixtures.alternative_conventions.booking.domain;

import io.github.fanoupro.hive.fixtures.alternative_conventions.booking.domain.api.PrepareBooking;
import io.github.fanoupro.hive.fixtures.alternative_conventions.booking.domain.spi.GetTrainsToBook;

import java.util.UUID;

public class SpaceTrainsBooker implements PrepareBooking {

    private final GetTrainsToBook trainsToBook;

    public SpaceTrainsBooker(GetTrainsToBook trainsToBook) {
        this.trainsToBook = trainsToBook;
    }

    @Override
    public Booking prepare(UUID searchId) {
        return new Booking(UUID.randomUUID(), trainsToBook.from(searchId));
    }
}

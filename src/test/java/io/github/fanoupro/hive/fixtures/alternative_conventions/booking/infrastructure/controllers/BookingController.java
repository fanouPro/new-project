package io.github.fanoupro.hive.fixtures.alternative_conventions.booking.infrastructure.controllers;

import io.github.fanoupro.hive.fixtures.alternative_conventions.booking.domain.Booking;
import io.github.fanoupro.hive.fixtures.alternative_conventions.booking.domain.api.PrepareBooking;

import java.util.UUID;

public class BookingController {

    private final PrepareBooking prepareBooking;

    public BookingController(PrepareBooking prepareBooking) {
        this.prepareBooking = prepareBooking;
    }

    public Booking book(UUID searchId) {
        return prepareBooking.prepare(searchId);
    }
}

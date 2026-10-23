package io.github.fanoupro.ruche.exemples.conventions_alternatives.reservation.domaine.api;

import io.github.fanoupro.ruche.exemples.conventions_alternatives.reservation.domaine.Reservation;

import java.util.UUID;

public interface PreparerReservation {
    Reservation preparer(UUID idRecherche);
}

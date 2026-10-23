package io.github.fanoupro.ruche.exemples.conventions_alternatives.reservation.infrastructure.controleurs;

import io.github.fanoupro.ruche.exemples.conventions_alternatives.reservation.domaine.Reservation;
import io.github.fanoupro.ruche.exemples.conventions_alternatives.reservation.domaine.api.PreparerReservation;

import java.util.UUID;

public class ControleurReservation {

    private final PreparerReservation preparerReservation;

    public ControleurReservation(PreparerReservation preparerReservation) {
        this.preparerReservation = preparerReservation;
    }

    public Reservation reserver(UUID idRecherche) {
        return preparerReservation.preparer(idRecherche);
    }
}

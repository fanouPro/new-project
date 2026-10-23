package io.github.fanoupro.ruche.exemples.conventions_alternatives.reservation.domaine;

import io.github.fanoupro.ruche.exemples.conventions_alternatives.reservation.domaine.api.PreparerReservation;
import io.github.fanoupro.ruche.exemples.conventions_alternatives.reservation.domaine.spi.ObtenirTrainsAReserver;

import java.util.UUID;

public class GuichetDeTrainsSpatiaux implements PreparerReservation {

    private final ObtenirTrainsAReserver trainsAReserver;

    public GuichetDeTrainsSpatiaux(ObtenirTrainsAReserver trainsAReserver) {
        this.trainsAReserver = trainsAReserver;
    }

    @Override
    public Reservation preparer(UUID idRecherche) {
        return new Reservation(UUID.randomUUID(), trainsAReserver.depuis(idRecherche));
    }
}

package io.github.fanoupro.ruche.exemples.conventions_alternatives.reservation.domaine.spi;

import java.util.List;
import java.util.UUID;

public interface ObtenirTrainsAReserver {
    List<String> depuis(UUID idRecherche);
}

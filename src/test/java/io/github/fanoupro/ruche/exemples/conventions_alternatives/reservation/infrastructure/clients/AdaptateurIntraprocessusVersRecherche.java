package io.github.fanoupro.ruche.exemples.conventions_alternatives.reservation.infrastructure.clients;

import io.github.fanoupro.ruche.exemples.conventions_alternatives.recherche.domaine.Recherche;
import io.github.fanoupro.ruche.exemples.conventions_alternatives.recherche.domaine.api.ObtenirRecherche;
import io.github.fanoupro.ruche.exemples.conventions_alternatives.reservation.domaine.spi.ObtenirTrainsAReserver;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class AdaptateurIntraprocessusVersRecherche implements ObtenirTrainsAReserver {

    private final ObtenirRecherche obtenirRecherche;

    public AdaptateurIntraprocessusVersRecherche(ObtenirRecherche obtenirRecherche) {
        this.obtenirRecherche = obtenirRecherche;
    }

    @Override
    public List<String> depuis(UUID idRecherche) {
        Recherche recherche = obtenirRecherche.identifieePar(idRecherche);
        return List.copyOf(recherche.trainsSelectionnes());
    }
}

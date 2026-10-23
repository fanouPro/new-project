package io.github.fanoupro.ruche.exemples.conventions_alternatives.recherche.domaine;

import io.github.fanoupro.ruche.exemples.conventions_alternatives.recherche.domaine.api.ObtenirRecherche;
import io.github.fanoupro.ruche.exemples.conventions_alternatives.recherche.domaine.spi.Recherches;

import java.util.UUID;

public class MoteurDeRecherche implements ObtenirRecherche {

    private final Recherches recherches;

    public MoteurDeRecherche(Recherches recherches) {
        this.recherches = recherches;
    }

    @Override
    public Recherche identifieePar(UUID idRecherche) {
        return recherches.trouver(idRecherche).orElseThrow();
    }
}

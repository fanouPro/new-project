package io.github.fanoupro.ruche.exemples.conventions_alternatives.recherche.infrastructure.controleurs;

import io.github.fanoupro.ruche.exemples.conventions_alternatives.recherche.domaine.api.ObtenirRecherche;

import java.util.List;
import java.util.UUID;

public class ControleurRecherche {

    private final ObtenirRecherche obtenirRecherche;

    public ControleurRecherche(ObtenirRecherche obtenirRecherche) {
        this.obtenirRecherche = obtenirRecherche;
    }

    public List<String> trainsSelectionnes(UUID idRecherche) {
        return obtenirRecherche.identifieePar(idRecherche).trainsSelectionnes();
    }
}

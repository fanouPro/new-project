package io.github.fanoupro.ruche.exemples.conventions_alternatives.recherche.domaine.api;

import io.github.fanoupro.ruche.exemples.conventions_alternatives.recherche.domaine.Recherche;

import java.util.UUID;

public interface ObtenirRecherche {
    Recherche identifieePar(UUID idRecherche);
}

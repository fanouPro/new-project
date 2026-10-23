package io.github.fanoupro.ruche.exemples.conventions_alternatives.recherche.domaine.spi;

import io.github.fanoupro.ruche.exemples.conventions_alternatives.recherche.domaine.Recherche;

import java.util.Optional;
import java.util.UUID;

public interface Recherches {
    Optional<Recherche> trouver(UUID idRecherche);
}

package io.github.fanoupro.ruche.exemples.conventions_alternatives.recherche.infrastructure.persistance;

import io.github.fanoupro.ruche.exemples.conventions_alternatives.recherche.domaine.Recherche;
import io.github.fanoupro.ruche.exemples.conventions_alternatives.recherche.domaine.spi.Recherches;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class RecherchesEnMemoire implements Recherches {

    private final Map<UUID, Recherche> recherches = new HashMap<>();

    @Override
    public Optional<Recherche> trouver(UUID idRecherche) {
        return Optional.ofNullable(recherches.get(idRecherche));
    }
}

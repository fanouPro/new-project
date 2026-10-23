package io.github.fanoupro.ruche.exemples.conventions_alternatives.recherche.domaine;

import java.util.List;
import java.util.UUID;

public record Recherche(UUID id, List<String> trainsSelectionnes) {
}

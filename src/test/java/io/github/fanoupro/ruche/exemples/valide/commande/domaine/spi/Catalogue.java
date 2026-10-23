package io.github.fanoupro.ruche.exemples.valide.commande.domaine.spi;

import io.github.fanoupro.ruche.exemples.valide.monnaie.Montant;

import java.util.Optional;

public interface Catalogue {
    Optional<Montant> prixDe(String reference);
}

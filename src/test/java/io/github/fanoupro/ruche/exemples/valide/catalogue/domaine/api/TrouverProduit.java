package io.github.fanoupro.ruche.exemples.valide.catalogue.domaine.api;

import io.github.fanoupro.ruche.exemples.valide.catalogue.domaine.Produit;

import java.util.Optional;

@FunctionalInterface
public interface TrouverProduit {
    Optional<Produit> parReference(String reference);
}

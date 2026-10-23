package io.github.fanoupro.ruche.exemples.valide.catalogue.domaine.spi;

import io.github.fanoupro.ruche.exemples.valide.catalogue.domaine.Produit;

import java.util.Optional;

public interface Produits {
    Optional<Produit> avecReference(String reference);
}

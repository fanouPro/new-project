package io.github.fanoupro.ruche.exemples.disposition_anglaise.catalogue.domain.api;

import io.github.fanoupro.ruche.exemples.disposition_anglaise.catalogue.domain.Produit;

import java.util.Optional;

public interface TrouverProduit {
    Optional<Produit> parReference(String reference);
}

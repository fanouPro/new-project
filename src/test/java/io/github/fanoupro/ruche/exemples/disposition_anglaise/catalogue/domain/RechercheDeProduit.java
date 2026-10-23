package io.github.fanoupro.ruche.exemples.disposition_anglaise.catalogue.domain;

import io.github.fanoupro.ruche.exemples.disposition_anglaise.catalogue.domain.api.TrouverProduit;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RechercheDeProduit implements TrouverProduit {

    private final Map<String, Produit> produits = new HashMap<>();

    @Override
    public Optional<Produit> parReference(String reference) {
        return Optional.ofNullable(produits.get(reference));
    }
}

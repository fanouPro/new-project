package io.github.fanoupro.ruche.exemples.disposition_anglaise.commande.domain;

import io.github.fanoupro.ruche.exemples.disposition_anglaise.commande.domain.api.PasserCommande;
import io.github.fanoupro.ruche.exemples.disposition_anglaise.commande.domain.spi.Catalogue;

import java.util.UUID;

public class PriseDeCommande implements PasserCommande {

    private final Catalogue catalogue;

    public PriseDeCommande(Catalogue catalogue) {
        this.catalogue = catalogue;
    }

    @Override
    public Commande passer(String reference) {
        if (!catalogue.propose(reference)) {
            throw new IllegalArgumentException("Produit inconnu " + reference);
        }
        return new Commande(UUID.randomUUID(), reference);
    }
}

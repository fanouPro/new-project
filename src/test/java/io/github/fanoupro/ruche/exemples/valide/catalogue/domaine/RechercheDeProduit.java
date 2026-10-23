package io.github.fanoupro.ruche.exemples.valide.catalogue.domaine;

import io.github.fanoupro.ruche.exemples.ddd.ServiceDuDomaine;
import io.github.fanoupro.ruche.exemples.valide.catalogue.domaine.api.TrouverProduit;
import io.github.fanoupro.ruche.exemples.valide.catalogue.domaine.spi.Produits;

import java.util.Optional;

@ServiceDuDomaine
public class RechercheDeProduit implements TrouverProduit {

    private final Produits produits;

    public RechercheDeProduit(Produits produits) {
        this.produits = produits;
    }

    @Override
    public Optional<Produit> parReference(String reference) {
        return produits.avecReference(reference);
    }
}

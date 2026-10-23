package io.github.fanoupro.ruche.exemples.valide.commande.infrastructure.intraprocessus;

import io.github.fanoupro.ruche.exemples.valide.catalogue.domaine.Produit;
import io.github.fanoupro.ruche.exemples.valide.catalogue.domaine.api.TrouverProduit;
import io.github.fanoupro.ruche.exemples.valide.commande.domaine.spi.Catalogue;
import io.github.fanoupro.ruche.exemples.valide.monnaie.Montant;

import java.util.Optional;

public class AdaptateurCatalogue implements Catalogue {

    private final TrouverProduit trouverProduit;

    public AdaptateurCatalogue(TrouverProduit trouverProduit) {
        this.trouverProduit = trouverProduit;
    }

    @Override
    public Optional<Montant> prixDe(String reference) {
        return trouverProduit.parReference(reference).map(Produit::prix);
    }
}

package io.github.fanoupro.ruche.exemples.valide.commande.domaine;

import io.github.fanoupro.ruche.exemples.ddd.ServiceDuDomaine;
import io.github.fanoupro.ruche.exemples.valide.commande.domaine.api.PasserCommande;
import io.github.fanoupro.ruche.exemples.valide.commande.domaine.spi.Catalogue;
import io.github.fanoupro.ruche.exemples.valide.commande.domaine.spi.Commandes;
import io.github.fanoupro.ruche.exemples.valide.commande.domaine.spi.Paiements;

import java.util.UUID;

@ServiceDuDomaine
public class PriseDeCommande implements PasserCommande {

    private final Commandes commandes;
    private final Catalogue catalogue;
    private final Paiements paiements;

    public PriseDeCommande(Commandes commandes, Catalogue catalogue, Paiements paiements) {
        this.commandes = commandes;
        this.catalogue = catalogue;
        this.paiements = paiements;
    }

    @Override
    public Commande passer(String reference) {
        var prix = catalogue.prixDe(reference).orElseThrow(() -> new ProduitInconnu(reference));
        var commande = commandes.enregistrer(new Commande(UUID.randomUUID(), reference, prix));
        paiements.debiter(commande.id(), commande.total());
        return commande;
    }
}

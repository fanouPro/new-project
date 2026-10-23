package io.github.fanoupro.ruche.exemples.violations.adaptateur_intraprocessus_sans_port_spi.commande.infrastructure.intraprocessus;

import io.github.fanoupro.ruche.exemples.violations.adaptateur_intraprocessus_sans_port_spi.catalogue.domaine.api.TrouverProduit;

public class PasserelleCatalogue {

    private TrouverProduit trouverProduit;

    public void verifier(String reference) {
        trouverProduit.trouver(reference);
    }
}

package io.github.fanoupro.ruche.exemples.violations.adaptateur_intraprocessus_utilise_la_spi_d_un_autre_module.commande.infrastructure.intraprocessus;

import io.github.fanoupro.ruche.exemples.violations.adaptateur_intraprocessus_utilise_la_spi_d_un_autre_module.catalogue.domaine.spi.Produits;
import io.github.fanoupro.ruche.exemples.violations.adaptateur_intraprocessus_utilise_la_spi_d_un_autre_module.commande.domaine.spi.Catalogue;

public class AdaptateurCatalogue implements Catalogue {

    private Produits produits;

    @Override
    public void verifier(String reference) {
        produits.trouver(reference);
    }
}

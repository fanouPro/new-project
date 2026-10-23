package io.github.fanoupro.ruche.exemples.violations.adaptateur_intraprocessus_utilise_un_bouchon_d_un_autre_module.commande.infrastructure.intraprocessus;

import io.github.fanoupro.ruche.exemples.violations.adaptateur_intraprocessus_utilise_un_bouchon_d_un_autre_module.catalogue.domaine.bouchons.ProduitsEnMemoire;
import io.github.fanoupro.ruche.exemples.violations.adaptateur_intraprocessus_utilise_un_bouchon_d_un_autre_module.commande.domaine.spi.Catalogue;

public class AdaptateurCatalogue implements Catalogue {

    private final ProduitsEnMemoire produits = new ProduitsEnMemoire();

    @Override
    public void verifier(String reference) {
        produits.trouver(reference);
    }
}

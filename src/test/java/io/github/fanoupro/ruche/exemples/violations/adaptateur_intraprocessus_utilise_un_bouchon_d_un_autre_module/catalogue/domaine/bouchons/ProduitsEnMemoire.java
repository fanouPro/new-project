package io.github.fanoupro.ruche.exemples.violations.adaptateur_intraprocessus_utilise_un_bouchon_d_un_autre_module.catalogue.domaine.bouchons;

import io.github.fanoupro.ruche.exemples.ddd.Bouchon;
import io.github.fanoupro.ruche.exemples.violations.adaptateur_intraprocessus_utilise_un_bouchon_d_un_autre_module.catalogue.domaine.spi.Produits;

@Bouchon
public class ProduitsEnMemoire implements Produits {

    @Override
    public void trouver(String reference) {
    }
}

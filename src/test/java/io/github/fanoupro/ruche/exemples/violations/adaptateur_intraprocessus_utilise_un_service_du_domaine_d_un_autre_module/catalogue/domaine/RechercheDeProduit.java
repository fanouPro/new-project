package io.github.fanoupro.ruche.exemples.violations.adaptateur_intraprocessus_utilise_un_service_du_domaine_d_un_autre_module.catalogue.domaine;

import io.github.fanoupro.ruche.exemples.ddd.ServiceDuDomaine;
import io.github.fanoupro.ruche.exemples.violations.adaptateur_intraprocessus_utilise_un_service_du_domaine_d_un_autre_module.catalogue.domaine.api.TrouverProduit;

@ServiceDuDomaine
public class RechercheDeProduit implements TrouverProduit {

    @Override
    public void trouver(String reference) {
    }
}

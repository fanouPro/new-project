package io.github.fanoupro.ruche.exemples.violations.adaptateur_intraprocessus_utilise_un_service_du_domaine_d_un_autre_module.commande.infrastructure.intraprocessus;

import io.github.fanoupro.ruche.exemples.violations.adaptateur_intraprocessus_utilise_un_service_du_domaine_d_un_autre_module.catalogue.domaine.RechercheDeProduit;
import io.github.fanoupro.ruche.exemples.violations.adaptateur_intraprocessus_utilise_un_service_du_domaine_d_un_autre_module.commande.domaine.spi.Catalogue;

public class AdaptateurCatalogue implements Catalogue {

    private RechercheDeProduit rechercheDeProduit;

    @Override
    public void verifier(String reference) {
        rechercheDeProduit.trouver(reference);
    }
}

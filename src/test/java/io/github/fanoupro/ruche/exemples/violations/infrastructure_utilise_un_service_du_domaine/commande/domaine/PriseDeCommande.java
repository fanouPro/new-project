package io.github.fanoupro.ruche.exemples.violations.infrastructure_utilise_un_service_du_domaine.commande.domaine;

import io.github.fanoupro.ruche.exemples.ddd.ServiceDuDomaine;
import io.github.fanoupro.ruche.exemples.violations.infrastructure_utilise_un_service_du_domaine.commande.domaine.api.PasserCommande;

@ServiceDuDomaine
public class PriseDeCommande implements PasserCommande {

    @Override
    public void passer(String reference) {
    }
}

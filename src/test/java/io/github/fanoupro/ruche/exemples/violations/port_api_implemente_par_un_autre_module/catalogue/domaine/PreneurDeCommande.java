package io.github.fanoupro.ruche.exemples.violations.port_api_implemente_par_un_autre_module.catalogue.domaine;

import io.github.fanoupro.ruche.exemples.violations.port_api_implemente_par_un_autre_module.commande.domaine.api.PasserCommande;

public class PreneurDeCommande implements PasserCommande {

    @Override
    public void passer(String reference) {
    }
}

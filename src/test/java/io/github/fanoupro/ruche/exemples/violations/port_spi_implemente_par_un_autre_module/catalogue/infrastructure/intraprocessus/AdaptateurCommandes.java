package io.github.fanoupro.ruche.exemples.violations.port_spi_implemente_par_un_autre_module.catalogue.infrastructure.intraprocessus;

import io.github.fanoupro.ruche.exemples.violations.port_spi_implemente_par_un_autre_module.commande.domaine.spi.Commandes;

public class AdaptateurCommandes implements Commandes {

    @Override
    public void enregistrer(String id) {
    }
}

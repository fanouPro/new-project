package io.github.fanoupro.ruche.exemples.violations.annotation_de_service_dans_l_infrastructure.commande.infrastructure.persistance;

import io.github.fanoupro.ruche.exemples.ddd.ServiceDuDomaine;
import io.github.fanoupro.ruche.exemples.violations.annotation_de_service_dans_l_infrastructure.commande.domaine.spi.Commandes;

@ServiceDuDomaine
public class AdaptateurDepotCommandes implements Commandes {

    @Override
    public void enregistrer(String id) {
    }
}

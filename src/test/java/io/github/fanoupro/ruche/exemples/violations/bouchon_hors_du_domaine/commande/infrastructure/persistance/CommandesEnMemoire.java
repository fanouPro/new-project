package io.github.fanoupro.ruche.exemples.violations.bouchon_hors_du_domaine.commande.infrastructure.persistance;

import io.github.fanoupro.ruche.exemples.ddd.Bouchon;
import io.github.fanoupro.ruche.exemples.violations.bouchon_hors_du_domaine.commande.domaine.spi.Commandes;

@Bouchon
public class CommandesEnMemoire implements Commandes {

    @Override
    public void enregistrer(String id) {
    }
}

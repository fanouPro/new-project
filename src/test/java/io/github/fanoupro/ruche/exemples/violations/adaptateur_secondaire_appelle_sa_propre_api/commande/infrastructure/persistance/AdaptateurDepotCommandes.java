package io.github.fanoupro.ruche.exemples.violations.adaptateur_secondaire_appelle_sa_propre_api.commande.infrastructure.persistance;

import io.github.fanoupro.ruche.exemples.violations.adaptateur_secondaire_appelle_sa_propre_api.commande.domaine.api.PasserCommande;
import io.github.fanoupro.ruche.exemples.violations.adaptateur_secondaire_appelle_sa_propre_api.commande.domaine.spi.Commandes;

public class AdaptateurDepotCommandes implements Commandes {

    private PasserCommande passerCommande;

    @Override
    public void enregistrer(String id) {
        passerCommande.passer(id);
    }
}

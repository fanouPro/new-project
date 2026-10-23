package io.github.fanoupro.ruche.exemples.violations.racine_de_composition_atteint_un_domaine.commande.domaine;

import io.github.fanoupro.ruche.exemples.ddd.ServiceDuDomaine;
import io.github.fanoupro.ruche.exemples.violations.racine_de_composition_atteint_un_domaine.commande.domaine.api.PasserCommande;

@ServiceDuDomaine
public class PriseDeCommande implements PasserCommande {

    @Override
    public void passer(String reference) {
    }
}

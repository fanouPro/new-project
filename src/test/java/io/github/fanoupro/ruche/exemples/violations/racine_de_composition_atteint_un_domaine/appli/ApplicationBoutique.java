package io.github.fanoupro.ruche.exemples.violations.racine_de_composition_atteint_un_domaine.appli;

import io.github.fanoupro.ruche.exemples.violations.racine_de_composition_atteint_un_domaine.commande.domaine.PriseDeCommande;
import io.github.fanoupro.ruche.exemples.violations.racine_de_composition_atteint_un_domaine.commande.domaine.api.PasserCommande;

public class ApplicationBoutique {

    PasserCommande passerCommande() {
        return new PriseDeCommande();
    }
}

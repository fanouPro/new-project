package io.github.fanoupro.ruche.exemples.valide.commande.infrastructure.controleur;

import io.github.fanoupro.ruche.exemples.valide.commande.domaine.api.PasserCommande;
import io.github.fanoupro.ruche.exemples.valide.commande.infrastructure.controleur.ressources.RessourceCommande;
import org.springframework.stereotype.Controller;

@Controller
public class ControleurCommande {

    private final PasserCommande passerCommande;

    public ControleurCommande(PasserCommande passerCommande) {
        this.passerCommande = passerCommande;
    }

    public RessourceCommande commander(String reference) {
        return RessourceCommande.de(passerCommande.passer(reference));
    }
}

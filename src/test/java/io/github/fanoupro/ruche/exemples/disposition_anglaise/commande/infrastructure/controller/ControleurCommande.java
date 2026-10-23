package io.github.fanoupro.ruche.exemples.disposition_anglaise.commande.infrastructure.controller;

import io.github.fanoupro.ruche.exemples.disposition_anglaise.commande.domain.Commande;
import io.github.fanoupro.ruche.exemples.disposition_anglaise.commande.domain.api.PasserCommande;

public class ControleurCommande {

    private final PasserCommande passerCommande;

    public ControleurCommande(PasserCommande passerCommande) {
        this.passerCommande = passerCommande;
    }

    public Commande commander(String reference) {
        return passerCommande.passer(reference);
    }
}

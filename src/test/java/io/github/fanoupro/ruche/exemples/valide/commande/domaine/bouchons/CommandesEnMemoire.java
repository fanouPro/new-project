package io.github.fanoupro.ruche.exemples.valide.commande.domaine.bouchons;

import io.github.fanoupro.ruche.exemples.ddd.Bouchon;
import io.github.fanoupro.ruche.exemples.valide.commande.domaine.Commande;
import io.github.fanoupro.ruche.exemples.valide.commande.domaine.spi.Commandes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Bouchon
public class CommandesEnMemoire implements Commandes {

    private final Map<UUID, Commande> commandes = new HashMap<>();

    @Override
    public Commande enregistrer(Commande commande) {
        commandes.put(commande.id(), commande);
        return commande;
    }
}

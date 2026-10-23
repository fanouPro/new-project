package io.github.fanoupro.ruche.exemples.valide.commande.infrastructure.persistance;

import io.github.fanoupro.ruche.exemples.valide.commande.domaine.Commande;
import io.github.fanoupro.ruche.exemples.valide.commande.domaine.spi.Commandes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AdaptateurDepotCommandes implements Commandes {

    private final Map<UUID, EntiteCommande> table = new HashMap<>();

    @Override
    public Commande enregistrer(Commande commande) {
        table.put(commande.id(), new EntiteCommande(commande));
        return table.get(commande.id()).versDomaine();
    }
}

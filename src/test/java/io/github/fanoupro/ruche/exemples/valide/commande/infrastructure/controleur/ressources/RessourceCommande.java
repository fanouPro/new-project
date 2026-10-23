package io.github.fanoupro.ruche.exemples.valide.commande.infrastructure.controleur.ressources;

import io.github.fanoupro.ruche.exemples.valide.commande.domaine.Commande;
import io.github.fanoupro.ruche.exemples.valide.hypermedia.Lien;

import java.net.URI;
import java.util.List;
import java.util.UUID;

public record RessourceCommande(UUID id, long totalEnCentimes, List<Lien> liens) {

    public static RessourceCommande de(Commande commande) {
        return new RessourceCommande(commande.id(), commande.total().centimes(),
                List.of(new Lien("self", URI.create("/commandes/" + commande.id()))));
    }
}

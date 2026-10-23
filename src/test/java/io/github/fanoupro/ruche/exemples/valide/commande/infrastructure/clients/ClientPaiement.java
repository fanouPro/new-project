package io.github.fanoupro.ruche.exemples.valide.commande.infrastructure.clients;

import io.github.fanoupro.ruche.exemples.valide.commande.domaine.spi.Paiements;
import io.github.fanoupro.ruche.exemples.valide.monnaie.Montant;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

public class ClientPaiement implements Paiements {

    private final HttpClient http = HttpClient.newHttpClient();
    private final URI servicePaiement;

    public ClientPaiement(URI servicePaiement) {
        this.servicePaiement = servicePaiement;
    }

    @Override
    public void debiter(UUID idCommande, Montant montant) {
        HttpRequest requete = HttpRequest.newBuilder(servicePaiement.resolve("/paiements/" + idCommande))
                .POST(HttpRequest.BodyPublishers.ofString(montant.centimes() + " " + montant.devise()))
                .build();
        http.sendAsync(requete, HttpResponse.BodyHandlers.discarding());
    }
}

package io.github.fanoupro.ruche.exemples.valide.commande.infrastructure.configuration;

import io.github.fanoupro.ruche.exemples.valide.commande.domaine.PriseDeCommande;
import io.github.fanoupro.ruche.exemples.valide.commande.domaine.api.PasserCommande;
import io.github.fanoupro.ruche.exemples.valide.commande.domaine.spi.Catalogue;
import io.github.fanoupro.ruche.exemples.valide.commande.domaine.spi.Commandes;
import io.github.fanoupro.ruche.exemples.valide.commande.domaine.spi.Paiements;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigurationCommande {

    @Bean
    PasserCommande passerCommande(Commandes commandes, Catalogue catalogue, Paiements paiements) {
        return new PriseDeCommande(commandes, catalogue, paiements);
    }
}

package io.github.fanoupro.ruche.exemples.valide.catalogue.infrastructure.configuration;

import io.github.fanoupro.ruche.exemples.valide.catalogue.domaine.RechercheDeProduit;
import io.github.fanoupro.ruche.exemples.valide.catalogue.domaine.api.TrouverProduit;
import io.github.fanoupro.ruche.exemples.valide.catalogue.domaine.spi.Produits;
import io.github.fanoupro.ruche.exemples.valide.catalogue.infrastructure.persistance.AdaptateurDepotProduits;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigurationCatalogue {

    @Bean
    Produits produits() {
        return new AdaptateurDepotProduits();
    }

    @Bean
    TrouverProduit trouverProduit(Produits produits) {
        return new RechercheDeProduit(produits);
    }
}

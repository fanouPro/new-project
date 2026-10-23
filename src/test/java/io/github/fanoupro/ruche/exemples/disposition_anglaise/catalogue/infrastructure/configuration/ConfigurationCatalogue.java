package io.github.fanoupro.ruche.exemples.disposition_anglaise.catalogue.infrastructure.configuration;

import io.github.fanoupro.ruche.exemples.disposition_anglaise.catalogue.domain.RechercheDeProduit;
import io.github.fanoupro.ruche.exemples.disposition_anglaise.catalogue.domain.api.TrouverProduit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigurationCatalogue {

    @Bean
    TrouverProduit trouverProduit() {
        return new RechercheDeProduit();
    }
}

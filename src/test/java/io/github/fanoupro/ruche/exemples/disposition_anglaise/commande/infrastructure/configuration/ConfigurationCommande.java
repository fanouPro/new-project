package io.github.fanoupro.ruche.exemples.disposition_anglaise.commande.infrastructure.configuration;

import io.github.fanoupro.ruche.exemples.disposition_anglaise.commande.domain.PriseDeCommande;
import io.github.fanoupro.ruche.exemples.disposition_anglaise.commande.domain.api.PasserCommande;
import io.github.fanoupro.ruche.exemples.disposition_anglaise.commande.domain.spi.Catalogue;
import io.github.fanoupro.ruche.exemples.disposition_anglaise.commande.infrastructure.inproc.AdaptateurCatalogue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = AdaptateurCatalogue.class)
public class ConfigurationCommande {

    @Bean
    PasserCommande passerCommande(Catalogue catalogue) {
        return new PriseDeCommande(catalogue);
    }
}

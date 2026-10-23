package io.github.fanoupro.ruche.exemples.conventions_alternatives.reservation.infrastructure.configurations;

import io.github.fanoupro.ruche.exemples.conventions_alternatives.reservation.domaine.GuichetDeTrainsSpatiaux;
import io.github.fanoupro.ruche.exemples.conventions_alternatives.reservation.domaine.api.PreparerReservation;
import io.github.fanoupro.ruche.exemples.conventions_alternatives.reservation.domaine.spi.ObtenirTrainsAReserver;
import io.github.fanoupro.ruche.exemples.conventions_alternatives.reservation.infrastructure.clients.AdaptateurIntraprocessusVersRecherche;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = AdaptateurIntraprocessusVersRecherche.class)
public class ConfigurationReservation {

    @Bean
    PreparerReservation preparerReservation(ObtenirTrainsAReserver trainsAReserver) {
        return new GuichetDeTrainsSpatiaux(trainsAReserver);
    }
}

package io.github.fanoupro.ruche;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class ConventionsRucheTest {

    @Test
    void les_conventions_par_defaut_traduisent_l_implementation_de_reference() {
        assertThat(ConventionsRuche.parDefaut()).isEqualTo(new ConventionsRuche(
                "domaine", "domaine.api", "domaine.spi", "infrastructure", "infrastructure.configuration",
                List.of("infrastructure.controleur"), List.of("infrastructure.intraprocessus")));
    }

    @Test
    void les_conventions_de_l_implementation_de_reference_gardent_ses_noms_anglais() {
        assertThat(ConventionsRuche.implementationDeReference()).isEqualTo(new ConventionsRuche(
                "domain", "domain.api", "domain.spi", "infrastructure", "infrastructure.configuration",
                List.of("infrastructure.controller"), List.of("infrastructure.inproc")));
    }

    @Test
    void les_paquetages_des_adaptateurs_peuvent_etre_renommes() {
        ConventionsRuche conventions = ConventionsRuche.parDefaut()
                .avecConfiguration("infrastructure.configurations")
                .avecAdaptateursPrimaires("infrastructure.controleurs", "infrastructure.consommateurs")
                .avecAdaptateursIntraprocessus("infrastructure.clients");

        assertThat(conventions.configuration()).isEqualTo("infrastructure.configurations");
        assertThat(conventions.adaptateursPrimaires())
                .containsExactly("infrastructure.controleurs", "infrastructure.consommateurs");
        assertThat(conventions.adaptateursIntraprocessus()).containsExactly("infrastructure.clients");
    }

    @Test
    void les_ports_appartiennent_au_domaine() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new ConventionsRuche("domaine", "api", "domaine.spi", "infrastructure",
                        "infrastructure.configuration", List.of(), List.of()))
                .withMessage("Le paquetage des ports API 'api' doit être un sous-paquetage de 'domaine'");
    }

    @Test
    void les_ports_api_et_spi_sont_separes() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new ConventionsRuche("domaine", "domaine.ports", "domaine.ports.spi",
                        "infrastructure", "infrastructure.configuration", List.of(), List.of()))
                .withMessage("Les ports API et SPI doivent être dans des paquetages distincts");
    }

    @Test
    void les_adaptateurs_appartiennent_a_l_infrastructure() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> ConventionsRuche.parDefaut().avecAdaptateursIntraprocessus("domaine.intraprocessus"))
                .withMessage("Le paquetage d'adaptateurs intraprocessus 'domaine.intraprocessus' doit être un "
                        + "sous-paquetage de 'infrastructure'");
    }

    @Test
    void le_domaine_et_l_infrastructure_sont_separes() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new ConventionsRuche("coeur", "coeur.api", "coeur.spi", "coeur.adaptateurs",
                        "coeur.adaptateurs.configuration", List.of(), List.of()))
                .withMessage("Le domaine et l'infrastructure doivent être des paquetages distincts");
    }
}

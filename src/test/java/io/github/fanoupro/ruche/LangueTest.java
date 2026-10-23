package io.github.fanoupro.ruche;

import com.tngtech.archunit.ArchConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class LangueTest {

    @AfterEach
    void oublier_la_configuration() {
        ArchConfiguration.get().reset();
    }

    @Test
    void une_langue_se_designe_par_son_code() {
        assertThat(Langue.deCode("fr")).isEqualTo(Langue.FRANCAIS);
        assertThat(Langue.deCode(" EN ")).isEqualTo(Langue.ANGLAIS);
        assertThat(Langue.ANGLAIS.code()).isEqualTo("en");
    }

    @Test
    void une_langue_inconnue_est_refusee() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Langue.deCode("de"))
                .withMessage("La langue 'de' n'est pas prise en charge : utilisez fr ou en");
    }

    @Test
    void sans_configuration_la_verification_est_en_francais() {
        assertThat(Langue.configuree()).isEqualTo(Langue.FRANCAIS);
    }

    @Test
    void la_configuration_archunit_choisit_la_langue() {
        ArchConfiguration.get().setProperty(Langue.PROPRIETE, "en");

        assertThat(Langue.configuree()).isEqualTo(Langue.ANGLAIS);
    }

    @Test
    void une_langue_mal_configuree_est_signalee_avec_sa_propriete() {
        ArchConfiguration.get().setProperty(Langue.PROPRIETE, "anglais");

        assertThatIllegalArgumentException()
                .isThrownBy(Langue::configuree)
                .withMessage("Configuration ArchUnit ruche.langue : La langue 'anglais' n'est pas prise en charge : "
                        + "utilisez fr ou en");
    }
}

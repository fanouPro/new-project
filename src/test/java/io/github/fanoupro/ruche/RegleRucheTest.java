package io.github.fanoupro.ruche;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class RegleRucheTest {

    @Test
    void l_identifiant_d_une_regle_est_son_nom_en_francais_et_son_equivalent_en_anglais() {
        assertThat(RegleRuche.LES_DOMAINES_SONT_ISOLES.identifiant(Langue.FRANCAIS))
                .isEqualTo("LES_DOMAINES_SONT_ISOLES");
        assertThat(RegleRuche.LES_DOMAINES_SONT_ISOLES.identifiant(Langue.ANGLAIS))
                .isEqualTo("DOMAINS_ARE_ISOLATED");
    }

    @ParameterizedTest
    @EnumSource(Langue.class)
    void chaque_regle_a_un_identifiant_unique_dans_chaque_langue(Langue langue) {
        assertThat(Arrays.stream(RegleRuche.values()).map(regle -> regle.identifiant(langue)))
                .doesNotHaveDuplicates()
                .allMatch(identifiant -> identifiant.matches("[A-Z]+(_[A-Z]+)*"));
    }

    @ParameterizedTest
    @EnumSource(Langue.class)
    void chaque_regle_est_decrite_et_justifiee_dans_chaque_langue(Langue langue) {
        assertThat(RegleRuche.values()).allSatisfy(regle -> {
            assertThat(regle.description(langue)).isNotBlank();
            assertThat(regle.justification(langue)).isNotBlank();
        });
    }
}

package io.github.fanoupro.ruche;

import com.tngtech.archunit.core.domain.JavaClasses;
import io.github.fanoupro.ruche.Exemples.Disposition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.EnumSet;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class RucheConformeTest {

    private static final String STANDARD = Exemples.paquetageDe("valide");
    private static final String ALTERNATIVE = Exemples.paquetageDe("conventions_alternatives");

    private static final ReglesRuche REGLES_STANDARD = ReglesRuche.de(Disposition.STANDARD.ruche(STANDARD));
    private static final ReglesRuche REGLES_ALTERNATIVES = ReglesRuche.de(Disposition.ALTERNATIVE.ruche(ALTERNATIVE));

    private static final JavaClasses CLASSES_STANDARD = Disposition.STANDARD.classes(STANDARD);
    private static final JavaClasses CLASSES_ALTERNATIVES = Disposition.ALTERNATIVE.classes(ALTERNATIVE);

    static Stream<RegleRuche> reglesDeLaDispositionStandard() {
        return REGLES_STANDARD.applicables().stream();
    }

    static Stream<RegleRuche> reglesDeLaDispositionAlternative() {
        return REGLES_ALTERNATIVES.applicables().stream();
    }

    @Test
    void la_disposition_standard_est_verifiee_par_toutes_les_regles() {
        assertThat(REGLES_STANDARD.applicables()).isEqualTo(EnumSet.allOf(RegleRuche.class));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("reglesDeLaDispositionStandard")
    void une_ruche_organisee_comme_l_implementation_de_reference_respecte(RegleRuche regle) {
        REGLES_STANDARD.regle(regle).check(CLASSES_STANDARD);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("reglesDeLaDispositionAlternative")
    void une_ruche_aux_conventions_alternatives_sous_une_racine_de_composition_parente_respecte(RegleRuche regle) {
        REGLES_ALTERNATIVES.regle(regle).check(CLASSES_ALTERNATIVES);
    }
}

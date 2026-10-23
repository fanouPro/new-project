package io.github.fanoupro.ruche;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import io.github.fanoupro.ruche.Exemples.Disposition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.EnumSet;
import java.util.stream.Stream;

import static io.github.fanoupro.ruche.RegleRuche.LES_MODULES_SE_DIVISENT_EN_DOMAINE_ET_INFRASTRUCTURE;
import static org.assertj.core.api.Assertions.assertThat;

class RucheConformeTest {

    private static final String STANDARD = Exemples.paquetageDe("valide");
    private static final String ALTERNATIVE = Exemples.paquetageDe("conventions_alternatives");
    private static final String ANGLAISE = Exemples.paquetageDe("disposition_anglaise");

    private static final ReglesRuche REGLES_STANDARD = ReglesRuche.de(Disposition.STANDARD.ruche(STANDARD));
    private static final ReglesRuche REGLES_ALTERNATIVES = ReglesRuche.de(Disposition.ALTERNATIVE.ruche(ALTERNATIVE));
    private static final ReglesRuche REGLES_ANGLAISES = ReglesRuche.de(rucheAnglaise(Langue.ANGLAIS));

    private static final JavaClasses CLASSES_STANDARD = Disposition.STANDARD.classes(STANDARD);
    private static final JavaClasses CLASSES_ALTERNATIVES = Disposition.ALTERNATIVE.classes(ALTERNATIVE);
    private static final JavaClasses CLASSES_ANGLAISES = new ClassFileImporter().importPackages(ANGLAISE);

    /**
     * Une ruche aux paquetages anglais ({@code domain}, {@code infrastructure.controller}, {@code inproc}), déclarée
     * sans conventions : celles de la langue s'appliquent.
     */
    private static Ruche rucheAnglaise(Langue langue) {
        return Ruche.nouvelle()
                .langue(langue)
                .module(ANGLAISE + ".commande")
                .module(ANGLAISE + ".catalogue")
                .paquetageDeBase(ANGLAISE)
                .construire();
    }

    static Stream<RegleRuche> reglesDeLaDispositionStandard() {
        return REGLES_STANDARD.applicables().stream();
    }

    static Stream<RegleRuche> reglesDeLaDispositionAlternative() {
        return REGLES_ALTERNATIVES.applicables().stream();
    }

    static Stream<RegleRuche> reglesDeLaDispositionAnglaise() {
        return REGLES_ANGLAISES.applicables().stream();
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

    @ParameterizedTest(name = "{0}")
    @MethodSource("reglesDeLaDispositionAnglaise")
    void une_ruche_aux_paquetages_anglais_verifiee_en_anglais_respecte(RegleRuche regle) {
        REGLES_ANGLAISES.regle(regle).check(CLASSES_ANGLAISES);
    }

    @Test
    void verifiee_en_francais_une_ruche_aux_paquetages_anglais_n_a_pas_de_domaine() {
        ReglesRuche regles = ReglesRuche.de(rucheAnglaise(Langue.FRANCAIS));

        assertThat(regles.regle(LES_MODULES_SE_DIVISENT_EN_DOMAINE_ET_INFRASTRUCTURE).evaluate(CLASSES_ANGLAISES)
                .getFailureReport().getDetails())
                .anyMatch(detail -> detail.contains("<" + ANGLAISE + ".commande.domain.PriseDeCommande> n'appartient "
                        + "ni au domaine ni à l'infrastructure du module 'commande'"));
    }
}

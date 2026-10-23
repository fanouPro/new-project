package io.github.fanoupro.ruche;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;

class ViolationTest {

    private static final String VIOLATIONS = Exemples.paquetageDe("violations");

    static Stream<Violation> violations() {
        return Violation.toutes();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("violations")
    void une_violation_enfreint_exactement_les_regles_attendues(Violation violation) {
        JavaClasses classes = violation.classes();
        ReglesRuche regles = ReglesRuche.de(violation.ruche());

        Set<RegleRuche> reglesEnfreintes = regles.applicables().stream()
                .filter(regle -> regles.regle(regle).evaluate(classes).hasViolation())
                .collect(toCollection(() -> EnumSet.noneOf(RegleRuche.class)));

        assertThat(reglesEnfreintes).isEqualTo(violation.reglesEnfreintes());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("violations")
    void le_rapport_d_une_violation_nomme_le_fautif(Violation violation) {
        JavaClasses classes = violation.classes();
        ReglesRuche regles = ReglesRuche.de(violation.ruche());

        for (RegleRuche regle : violation.reglesEnfreintes()) {
            assertThat(regles.regle(regle).evaluate(classes).getFailureReport().getDetails())
                    .as("rapport de %s", regle)
                    .anyMatch(detail -> detail.contains(violation.fautifQualifie()));
        }
    }

    @Test
    void chaque_regle_est_prouvee_par_au_moins_une_violation() {
        Set<RegleRuche> reglesProuvees = Violation.toutes()
                .flatMap(violation -> violation.reglesEnfreintes().stream())
                .collect(toSet());

        assertThat(reglesProuvees).containsExactlyInAnyOrder(RegleRuche.values());
    }

    @Test
    void chaque_exemple_de_violation_est_verifie() {
        Set<String> exemples = new ClassFileImporter().importPackages(VIOLATIONS).stream()
                .map(JavaClass::getPackageName)
                .map(paquetage -> paquetage.substring(VIOLATIONS.length() + 1).split("\\.")[0])
                .collect(toSet());

        assertThat(exemples).containsExactlyInAnyOrderElementsOf(Violation.toutes().map(Violation::toString).toList());
    }
}

package io.github.fanoupro.hive;

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

    private static final String VIOLATIONS = Fixtures.packageOf("violations");

    static Stream<Violation> violations() {
        return Violation.all();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("violations")
    void a_violation_breaks_exactly_the_rules_it_violates(Violation violation) {
        JavaClasses classes = violation.classes();
        HiveRules rules = HiveRules.of(violation.hive());

        Set<HiveRule> brokenRules = rules.applicable().stream()
                .filter(rule -> rules.rule(rule).evaluate(classes).hasViolation())
                .collect(toCollection(() -> EnumSet.noneOf(HiveRule.class)));

        assertThat(brokenRules).isEqualTo(violation.brokenRules());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("violations")
    void a_violation_report_names_the_offender(Violation violation) {
        JavaClasses classes = violation.classes();
        HiveRules rules = HiveRules.of(violation.hive());

        for (HiveRule rule : violation.brokenRules()) {
            assertThat(rules.rule(rule).evaluate(classes).getFailureReport().getDetails())
                    .as("report of %s", rule)
                    .anyMatch(detail -> detail.contains(violation.qualifiedOffender()));
        }
    }

    @Test
    void every_rule_is_proven_by_at_least_one_violation() {
        Set<HiveRule> provenRules = Violation.all()
                .flatMap(violation -> violation.brokenRules().stream())
                .collect(toSet());

        assertThat(provenRules).containsExactlyInAnyOrder(HiveRule.values());
    }

    @Test
    void every_violation_fixture_is_checked() {
        Set<String> fixtures = new ClassFileImporter().importPackages(VIOLATIONS).stream()
                .map(JavaClass::getPackageName)
                .map(packageName -> packageName.substring(VIOLATIONS.length() + 1).split("\\.")[0])
                .collect(toSet());

        assertThat(fixtures).containsExactlyInAnyOrderElementsOf(Violation.all().map(Violation::toString).toList());
    }
}

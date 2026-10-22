package io.github.fanoupro.hive;

import com.tngtech.archunit.core.domain.JavaClasses;
import io.github.fanoupro.hive.Fixtures.Layout;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.EnumSet;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class CompliantHiveTest {

    private static final String REFERENCE = Fixtures.packageOf("valid");
    private static final String ALTERNATIVE = Fixtures.packageOf("alternative_conventions");

    private static final HiveRules REFERENCE_RULES = HiveRules.of(Layout.STANDARD.hive(REFERENCE));
    private static final HiveRules ALTERNATIVE_RULES = HiveRules.of(Layout.ALTERNATIVE.hive(ALTERNATIVE));

    private static final JavaClasses REFERENCE_CLASSES = Layout.STANDARD.classes(REFERENCE);
    private static final JavaClasses ALTERNATIVE_CLASSES = Layout.ALTERNATIVE.classes(ALTERNATIVE);

    static Stream<HiveRule> rulesOfTheReferenceLayout() {
        return REFERENCE_RULES.applicable().stream();
    }

    static Stream<HiveRule> rulesOfTheAlternativeLayout() {
        return ALTERNATIVE_RULES.applicable().stream();
    }

    @Test
    void the_reference_layout_is_checked_against_every_rule() {
        assertThat(REFERENCE_RULES.applicable()).isEqualTo(EnumSet.allOf(HiveRule.class));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("rulesOfTheReferenceLayout")
    void a_hive_laid_out_like_the_reference_implementation_complies_with(HiveRule rule) {
        REFERENCE_RULES.rule(rule).check(REFERENCE_CLASSES);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("rulesOfTheAlternativeLayout")
    void a_hive_with_alternative_conventions_and_a_parent_composition_root_complies_with(HiveRule rule) {
        ALTERNATIVE_RULES.rule(rule).check(ALTERNATIVE_CLASSES);
    }
}

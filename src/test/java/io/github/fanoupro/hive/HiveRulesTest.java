package io.github.fanoupro.hive;

import com.tngtech.archunit.core.domain.JavaClasses;
import io.github.fanoupro.hive.Fixtures.Layout;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static io.github.fanoupro.hive.HiveRule.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class HiveRulesTest {

    @Test
    void a_hive_only_gets_the_rules_its_declarations_make_checkable() {
        Hive hive = Hive.builder().module("com.acme.ordering").build();

        assertThat(HiveRules.of(hive).applicable()).isEqualTo(EnumSet.complementOf(EnumSet.of(
                API_PORTS_ARE_FUNCTIONAL,
                SHARED_KERNELS_ARE_PURE,
                ANNOTATIONS_ARE_PURE,
                TECHNICAL_MODULES_ARE_DOMAIN_AGNOSTIC,
                DOMAINS_DO_NOT_DEPEND_ON_TECHNICAL_MODULES,
                NOTHING_DEPENDS_ON_THE_COMPOSITION_ROOT,
                COMPOSITION_ROOT_DOES_NOT_REACH_INTO_DOMAINS,
                EVERY_CLASS_BELONGS_TO_A_DECLARED_ZONE,
                DOMAIN_SERVICES_ARE_ANNOTATED,
                DOMAIN_SERVICE_ANNOTATION_IS_RESERVED_TO_DOMAIN_SERVICES,
                STUBS_ARE_SPI_IMPLEMENTATIONS_IN_THE_DOMAIN)));
    }

    @Test
    void asking_for_a_rule_the_hive_cannot_check_fails() {
        HiveRules rules = HiveRules.of(Hive.builder().module("com.acme.ordering").build());

        assertThatIllegalArgumentException()
                .isThrownBy(() -> rules.rule(STUBS_ARE_SPI_IMPLEMENTATIONS_IN_THE_DOMAIN))
                .withMessageContaining("STUBS_ARE_SPI_IMPLEMENTATIONS_IN_THE_DOMAIN does not apply");
    }

    @Test
    void all_rules_pass_together_on_a_compliant_hive() {
        String reference = Fixtures.packageOf("valid");

        HiveRules.of(Layout.STANDARD.hive(reference)).all().check(Layout.STANDARD.classes(reference));
    }

    @Test
    void a_rule_explains_itself_with_its_identifier_and_rationale() {
        HiveRules rules = HiveRules.of(Hive.builder().module("com.acme.ordering").build());

        assertThat(rules.rule(DOMAINS_ARE_ISOLATED).getDescription())
                .isEqualTo("[DOMAINS_ARE_ISOLATED] the domain of a module never depends on another module, "
                        + "because each module must stay extractable as a standalone service");
    }

    @Test
    void all_rules_together_tag_every_violation_with_the_rule_it_breaks() {
        Violation violation = violation("api_port_implemented_by_another_module");
        String productOrderer = "Class <" + violation.basePackage() + ".catalog.domain.ProductOrderer>";

        assertThat(HiveRules.of(violation.hive()).all().evaluate(violation.classes()).getFailureReport().toString())
                .contains("Rule 'classes follow the Hive pattern (28 rules)' was violated (2 times)")
                .contains("[API_PORTS_ARE_IMPLEMENTED_BY_THE_DOMAIN] " + productOrderer + " in module 'catalog' domain "
                        + "implements <" + violation.basePackage() + ".ordering.domain.api.PlaceOrder> "
                        + "of module 'ordering' API")
                .contains("[DOMAINS_ARE_ISOLATED] module 'catalog' domain -> module 'ordering' API: " + productOrderer
                        + " implements interface <" + violation.basePackage() + ".ordering.domain.api.PlaceOrder>");
    }

    @Test
    void a_forbidden_dependency_is_reported_with_the_zones_it_crosses() {
        Violation violation = violation("domain_depends_on_its_infrastructure");

        assertThat(HiveRules.of(violation.hive()).rule(DOMAIN_DOES_NOT_DEPEND_ON_ITS_INFRASTRUCTURE)
                .evaluate(violation.classes()).getFailureReport().getDetails())
                .singleElement().asString()
                .startsWith("[DOMAIN_DOES_NOT_DEPEND_ON_ITS_INFRASTRUCTURE] "
                        + "module 'ordering' domain -> module 'ordering' infrastructure: Field <"
                        + violation.basePackage() + ".ordering.domain.Order.entity> has type <"
                        + violation.basePackage() + ".ordering.infrastructure.persistence.OrderEntity>");
    }

    @Test
    void a_shared_table_is_reported_with_every_module_mapping_it() {
        Violation violation = violation("two_modules_map_the_same_table");

        assertThat(HiveRules.of(violation.hive()).rule(DATASTORES_ARE_SEGREGATED)
                .evaluate(violation.classes()).getFailureReport().getDetails())
                .containsExactly("[DATASTORES_ARE_SEGREGATED] Table 'product' is mapped by module 'catalog' ["
                        + violation.basePackage() + ".catalog.infrastructure.persistence.ProductEntity] and module 'ordering' ["
                        + violation.basePackage() + ".ordering.infrastructure.persistence.ProductLineEntity]");
    }

    @Test
    void domains_may_depend_on_declared_pure_libraries() {
        Violation violation = violation("domain_uses_a_framework");
        JavaClasses classes = violation.classes();
        Hive strict = Hive.builder().module(violation.basePackage() + ".ordering").build();
        Hive lenient = Hive.builder().module(violation.basePackage() + ".ordering")
                .pureLibraries("jakarta.persistence..")
                .build();

        assertThat(HiveRules.of(strict).rule(DOMAIN_IS_FRAMEWORK_FREE).evaluate(classes).hasViolation()).isTrue();
        assertThat(HiveRules.of(lenient).rule(DOMAIN_IS_FRAMEWORK_FREE).evaluate(classes).hasViolation()).isFalse();
    }

    private static Violation violation(String fixture) {
        return Violation.all()
                .filter(violation -> violation.toString().equals(fixture))
                .findFirst()
                .orElseThrow();
    }
}

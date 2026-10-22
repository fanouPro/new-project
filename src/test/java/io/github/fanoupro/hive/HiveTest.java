package io.github.fanoupro.hive;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

class HiveTest {

    @Test
    void a_module_is_named_after_its_package_by_default() {
        Hive hive = Hive.builder().module("com.acme.shop.ordering").build();

        assertThat(hive.modules()).containsExactly(new HiveModule("ordering", "com.acme.shop.ordering"));
    }

    @Test
    void pure_code_may_always_depend_on_the_jdk() {
        Hive hive = Hive.builder().module("com.acme.shop.ordering").pureLibraries("org.jspecify..").build();

        assertThat(hive.pureLibraries()).containsExactly("java..", "org.jspecify..");
    }

    @Test
    void a_hive_needs_a_module() {
        assertThatIllegalStateException()
                .isThrownBy(() -> Hive.builder().sharedKernel("com.acme.money").build())
                .withMessage("A Hive needs at least one module");
    }

    @Test
    void module_names_are_unique() {
        assertThatIllegalStateException()
                .isThrownBy(() -> Hive.builder()
                        .module("com.acme.shop.ordering")
                        .module("com.acme.legacy.ordering")
                        .build())
                .withMessage("Two Hive modules are named 'ordering'");
    }

    @Test
    void a_package_is_declared_once() {
        assertThatIllegalStateException()
                .isThrownBy(() -> Hive.builder()
                        .module("com.acme.shop.ordering")
                        .technicalModule("com.acme.shop.ordering")
                        .build())
                .withMessage("The package 'com.acme.shop.ordering' is declared twice");
    }

    @Test
    void a_zone_cannot_be_nested_in_another_one() {
        assertThatIllegalStateException()
                .isThrownBy(() -> Hive.builder()
                        .module("com.acme.shop.ordering")
                        .sharedKernel("com.acme.shop.ordering.money")
                        .build())
                .withMessage("The package 'com.acme.shop.ordering.money' is nested in the declared package "
                        + "'com.acme.shop.ordering'");
    }

    @Test
    void a_composition_root_cannot_be_nested_in_another_zone() {
        assertThatIllegalStateException()
                .isThrownBy(() -> Hive.builder()
                        .module("com.acme.shop.ordering")
                        .compositionRoot("com.acme.shop.ordering.application")
                        .build())
                .withMessage("The composition root 'com.acme.shop.ordering.application' is nested in the declared "
                        + "package 'com.acme.shop.ordering'");
    }

    @Test
    void zones_can_be_nested_in_a_composition_root() {
        Hive hive = Hive.builder()
                .module("com.acme.shop.ordering")
                .compositionRoot("com.acme.shop")
                .build();

        assertThat(hive.compositionRoots()).containsExactly("com.acme.shop");
    }

    @Test
    void packages_must_be_valid_java_packages() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Hive.builder().module("com.acme..ordering"))
                .withMessage("Invalid module package: 'com.acme..ordering'");
    }
}

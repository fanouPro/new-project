package io.github.fanoupro.hive;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class HiveConventionsTest {

    @Test
    void defaults_follow_the_reference_implementation() {
        assertThat(HiveConventions.defaults()).isEqualTo(new HiveConventions(
                "domain", "domain.api", "domain.spi", "infrastructure", "infrastructure.configuration",
                List.of("infrastructure.controller"), List.of("infrastructure.inproc")));
    }

    @Test
    void adapter_packages_can_be_renamed() {
        HiveConventions conventions = HiveConventions.defaults()
                .withConfiguration("infrastructure.configurations")
                .withDrivingAdapters("infrastructure.controllers", "infrastructure.listeners")
                .withInProcessAdapters("infrastructure.clients");

        assertThat(conventions.configuration()).isEqualTo("infrastructure.configurations");
        assertThat(conventions.drivingAdapters()).containsExactly("infrastructure.controllers", "infrastructure.listeners");
        assertThat(conventions.inProcessAdapters()).containsExactly("infrastructure.clients");
    }

    @Test
    void ports_belong_to_the_domain() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new HiveConventions("domain", "api", "domain.spi", "infrastructure",
                        "infrastructure.configuration", List.of(), List.of()))
                .withMessage("The API package 'api' must be a sub-package of 'domain'");
    }

    @Test
    void api_and_spi_ports_are_separate() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new HiveConventions("domain", "domain.ports", "domain.ports.spi", "infrastructure",
                        "infrastructure.configuration", List.of(), List.of()))
                .withMessage("API and SPI ports must be separate packages");
    }

    @Test
    void adapters_belong_to_the_infrastructure() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> HiveConventions.defaults().withInProcessAdapters("domain.inproc"))
                .withMessage("The in-process adapters package 'domain.inproc' must be a sub-package of 'infrastructure'");
    }

    @Test
    void the_domain_and_the_infrastructure_are_separate() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new HiveConventions("core", "core.api", "core.spi", "core.adapters",
                        "core.adapters.configuration", List.of(), List.of()))
                .withMessage("The domain and the infrastructure must be separate packages");
    }
}

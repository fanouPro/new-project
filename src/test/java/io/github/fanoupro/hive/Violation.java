package io.github.fanoupro.hive;

import com.tngtech.archunit.core.domain.JavaClasses;
import io.github.fanoupro.hive.Fixtures.Layout;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Stream;

import static io.github.fanoupro.hive.HiveRule.*;

/**
 * A fixture breaking the Hive pattern, the rules it must break, and the class or element the reports must name.
 */
record Violation(String fixture, Layout layout, Set<HiveRule> brokenRules, String offender) {

    static Stream<Violation> all() {
        return Stream.of(
                standard("domain_depends_on_its_infrastructure", "ordering.domain.Order",
                        DOMAIN_DOES_NOT_DEPEND_ON_ITS_INFRASTRUCTURE),
                standard("domain_uses_a_framework", "ordering.domain.Order",
                        DOMAIN_IS_FRAMEWORK_FREE),
                standard("api_port_is_a_class", "ordering.domain.api.PlaceOrder",
                        API_PORTS_ARE_INTERFACES),
                standard("spi_port_is_a_class", "ordering.domain.spi.Orders",
                        SPI_PORTS_ARE_INTERFACES),
                standard("api_port_exposes_several_features", "ordering.domain.api.ManageOrders",
                        API_PORTS_ARE_FUNCTIONAL),
                standard("api_port_implemented_in_infrastructure", "ordering.infrastructure.controller.OrderController",
                        API_PORTS_ARE_IMPLEMENTED_BY_THE_DOMAIN),
                standard("api_port_implemented_by_another_module", "catalog.domain.ProductOrderer",
                        API_PORTS_ARE_IMPLEMENTED_BY_THE_DOMAIN, DOMAINS_ARE_ISOLATED),
                standard("spi_port_implemented_in_domain", "ordering.domain.InMemoryOrders",
                        SPI_PORTS_ARE_IMPLEMENTED_BY_ADAPTERS),
                standard("spi_port_implemented_by_another_module", "catalog.infrastructure.inproc.OrdersAdapter",
                        SPI_PORTS_ARE_IMPLEMENTED_BY_ADAPTERS, OTHER_MODULES_ARE_REACHED_THROUGH_THEIR_API),
                standard("driving_adapter_uses_an_spi_port", "ordering.infrastructure.controller.OrderController",
                        DRIVING_ADAPTERS_ONLY_USE_API_PORTS),
                standard("driving_adapter_uses_a_driven_adapter", "ordering.infrastructure.controller.OrderController",
                        DRIVING_ADAPTERS_ONLY_USE_API_PORTS),
                standard("infrastructure_uses_a_domain_service", "ordering.infrastructure.controller.OrderController",
                        INFRASTRUCTURE_USES_DOMAIN_SERVICES_THROUGH_API_PORTS),
                standard("driven_adapter_calls_its_own_api",
                        "ordering.infrastructure.persistence.OrderRepositoryAdapter",
                        DRIVEN_ADAPTERS_DO_NOT_CALL_BACK_INTO_THEIR_DOMAIN),

                standard("domain_depends_on_another_module_domain", "ordering.domain.Order",
                        DOMAINS_ARE_ISOLATED),
                standard("domain_depends_on_another_module_infrastructure", "ordering.domain.Order",
                        DOMAINS_ARE_ISOLATED),
                standard("infrastructure_depends_on_another_module_infrastructure",
                        "ordering.infrastructure.persistence.OrderRepository",
                        INFRASTRUCTURES_ARE_ISOLATED),
                standard("in_process_adapter_uses_another_module_spi", "ordering.infrastructure.inproc.CatalogAdapter",
                        OTHER_MODULES_ARE_REACHED_THROUGH_THEIR_API),
                standard("in_process_adapter_uses_another_module_domain_service",
                        "ordering.infrastructure.inproc.CatalogAdapter",
                        OTHER_MODULES_ARE_REACHED_THROUGH_THEIR_API),
                standard("in_process_adapter_uses_another_module_stub", "ordering.infrastructure.inproc.CatalogAdapter",
                        OTHER_MODULES_ARE_REACHED_THROUGH_THEIR_API),
                standard("driving_adapter_calls_another_module", "ordering.infrastructure.controller.OrderController",
                        ONLY_IN_PROCESS_ADAPTERS_REACH_OTHER_MODULES),
                standard("in_process_adapter_without_spi_port", "ordering.infrastructure.inproc.CatalogGateway",
                        IN_PROCESS_ADAPTERS_IMPLEMENT_AN_SPI_PORT),
                standard("two_modules_map_the_same_table", "ordering.infrastructure.persistence.ProductLineEntity",
                        DATASTORES_ARE_SEGREGATED),
                standard("two_modules_map_the_same_implicit_table",
                        "catalog.infrastructure.persistence.TimeStampEntity",
                        DATASTORES_ARE_SEGREGATED),

                standard("shared_kernel_uses_a_framework", "money.Money",
                        SHARED_KERNELS_ARE_PURE),
                standard("shared_kernel_depends_on_a_module", "money.Money",
                        SHARED_KERNELS_ARE_PURE),
                standard("annotations_depend_on_a_framework", "ddd.DomainService",
                        ANNOTATIONS_ARE_PURE),
                standard("technical_module_depends_on_a_module", "hypermedia.OrderLinks",
                        TECHNICAL_MODULES_ARE_DOMAIN_AGNOSTIC),
                standard("domain_depends_on_a_technical_module", "ordering.domain.Order",
                        DOMAINS_DO_NOT_DEPEND_ON_TECHNICAL_MODULES),
                standard("module_depends_on_the_composition_root",
                        "ordering.infrastructure.configuration.OrderingConfiguration",
                        NOTHING_DEPENDS_ON_THE_COMPOSITION_ROOT),
                standard("composition_root_reaches_into_a_domain", "app.ShopApplication",
                        COMPOSITION_ROOT_DOES_NOT_REACH_INTO_DOMAINS),

                standard("module_without_configuration", "ordering.infrastructure.configuration",
                        EVERY_MODULE_HAS_ITS_OWN_CONFIGURATION),
                standard("class_outside_domain_and_infrastructure", "ordering.application.OrderingFacade",
                        MODULES_ARE_SPLIT_INTO_DOMAIN_AND_INFRASTRUCTURE),
                standard("undeclared_module", "shipping.domain.Shipment",
                        EVERY_CLASS_BELONGS_TO_A_DECLARED_ZONE),
                alternative("undeclared_module_under_a_composition_root", "payment.Payment",
                        EVERY_CLASS_BELONGS_TO_A_DECLARED_ZONE),

                standard("domain_service_not_annotated", "ordering.domain.OrderPlacer",
                        DOMAIN_SERVICES_ARE_ANNOTATED),
                standard("domain_service_annotation_in_infrastructure",
                        "ordering.infrastructure.persistence.OrderRepositoryAdapter",
                        DOMAIN_SERVICE_ANNOTATION_IS_RESERVED_TO_DOMAIN_SERVICES),
                standard("domain_service_annotation_without_api_port", "ordering.domain.PriceCalculator",
                        DOMAIN_SERVICE_ANNOTATION_IS_RESERVED_TO_DOMAIN_SERVICES),
                standard("stub_outside_the_domain", "ordering.infrastructure.persistence.InMemoryOrders",
                        STUBS_ARE_SPI_IMPLEMENTATIONS_IN_THE_DOMAIN),
                standard("stub_without_spi_port", "ordering.domain.stubs.FixedClock",
                        STUBS_ARE_SPI_IMPLEMENTATIONS_IN_THE_DOMAIN));
    }

    private static Violation standard(String fixture, String offender, HiveRule rule, HiveRule... otherRules) {
        return new Violation("violations." + fixture, Layout.STANDARD, EnumSet.of(rule, otherRules), offender);
    }

    private static Violation alternative(String fixture, String offender, HiveRule rule, HiveRule... otherRules) {
        return new Violation("violations." + fixture, Layout.ALTERNATIVE, EnumSet.of(rule, otherRules), offender);
    }

    String basePackage() {
        return Fixtures.packageOf(fixture);
    }

    Hive hive() {
        return layout.hive(basePackage());
    }

    JavaClasses classes() {
        return layout.classes(basePackage());
    }

    String qualifiedOffender() {
        return basePackage() + "." + offender;
    }

    @Override
    public String toString() {
        return fixture.substring("violations.".length());
    }
}

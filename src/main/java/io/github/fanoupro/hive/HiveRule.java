package io.github.fanoupro.hive;

/**
 * Catalog of the architecture rules of the Hive pattern.
 */
public enum HiveRule {

    DOMAIN_DOES_NOT_DEPEND_ON_ITS_INFRASTRUCTURE(Category.HEXAGON,
            "the domain of a module does not depend on its infrastructure",
            "the hexagon must stay independent of technical concerns"),
    DOMAIN_IS_FRAMEWORK_FREE(Category.HEXAGON,
            "the domain of a module only depends on the JDK and on declared pure libraries outside the Hive",
            "frameworks belong to adapters, not to business logic"),
    API_PORTS_ARE_INTERFACES(Category.HEXAGON,
            "API ports are interfaces",
            "an API port is the contract of a feature, implemented by a domain service"),
    SPI_PORTS_ARE_INTERFACES(Category.HEXAGON,
            "SPI ports are interfaces",
            "an SPI port is what the domain expects from the outside world, implemented by an adapter"),
    API_PORTS_ARE_FUNCTIONAL(Category.HEXAGON,
            "API ports declare exactly one abstract method",
            "each API port exposes a single feature"),
    API_PORTS_ARE_IMPLEMENTED_BY_THE_DOMAIN(Category.HEXAGON,
            "API ports are only implemented by the domain of their own module",
            "features are business logic, implemented by domain services"),
    SPI_PORTS_ARE_IMPLEMENTED_BY_ADAPTERS(Category.HEXAGON,
            "SPI ports are only implemented by the infrastructure of their own module, or by stubs of its domain",
            "the domain must not know how its SPI ports are fulfilled"),
    DRIVING_ADAPTERS_ONLY_USE_API_PORTS(Category.HEXAGON,
            "driving adapters reach their module through API ports, never through SPI ports or driven adapters",
            "the hexagon is entered through its API ports only"),
    INFRASTRUCTURE_USES_DOMAIN_SERVICES_THROUGH_API_PORTS(Category.HEXAGON,
            "outside of its configuration, the infrastructure of a module never depends on its domain services",
            "adapters depend on API ports, not on their implementations"),
    DRIVEN_ADAPTERS_DO_NOT_CALL_BACK_INTO_THEIR_DOMAIN(Category.HEXAGON,
            "adapters implementing an SPI port never call API ports of their own module",
            "a driven adapter calling its own hexagon creates a hidden loop"),

    DOMAINS_ARE_ISOLATED(Category.HIVE,
            "the domain of a module never depends on another module",
            "each module must stay extractable as a standalone service"),
    INFRASTRUCTURES_ARE_ISOLATED(Category.HIVE,
            "the infrastructure of a module never depends on the infrastructure of another module",
            "each module owns its adapters and its datastore"),
    OTHER_MODULES_ARE_REACHED_THROUGH_THEIR_API(Category.HIVE,
            "a module reaches another module through its API ports and the model they expose, "
                    + "never through its SPI ports, domain services or stubs",
            "modules communicate as they would as microservices: through the API of the target"),
    ONLY_IN_PROCESS_ADAPTERS_REACH_OTHER_MODULES(Category.HIVE,
            "only in-process adapters depend on another module",
            "inter-module calls are SPI adapters that become remote clients when a module is extracted"),
    IN_PROCESS_ADAPTERS_IMPLEMENT_AN_SPI_PORT(Category.HIVE,
            "in-process adapters calling an API port of another module implement an SPI port of their own module",
            "the caller only knows its own SPI port, expressed with its own model"),
    DATASTORES_ARE_SEGREGATED(Category.HIVE,
            "no table is mapped by two modules",
            "each module owns a datastore used exclusively by itself"),

    SHARED_KERNELS_ARE_PURE(Category.SHARED,
            "shared kernels only depend on the JDK, declared pure libraries, other shared kernels and annotations",
            "a shared kernel is a library of value objects used by every module"),
    ANNOTATIONS_ARE_PURE(Category.SHARED,
            "annotations only depend on the JDK, declared pure libraries and other annotations",
            "annotations exist to keep the dependency injection framework out of domains"),
    TECHNICAL_MODULES_ARE_DOMAIN_AGNOSTIC(Category.SHARED,
            "technical modules never depend on a Hive module",
            "technical modules are shared by the adapters of every module"),
    DOMAINS_DO_NOT_DEPEND_ON_TECHNICAL_MODULES(Category.SHARED,
            "domains never depend on technical modules",
            "technical modules are infrastructure concerns"),
    NOTHING_DEPENDS_ON_THE_COMPOSITION_ROOT(Category.SHARED,
            "nothing depends on a composition root",
            "the composition root only assembles modules into a deployable"),
    COMPOSITION_ROOT_DOES_NOT_REACH_INTO_DOMAINS(Category.SHARED,
            "composition roots never depend on domains",
            "each module wires its own domain from its infrastructure configuration"),

    EVERY_MODULE_HAS_ITS_OWN_CONFIGURATION(Category.STRUCTURE,
            "every module has a configuration in its infrastructure",
            "a module wires itself, so that it can be deployed in any composition root"),
    MODULES_ARE_SPLIT_INTO_DOMAIN_AND_INFRASTRUCTURE(Category.STRUCTURE,
            "every class of a module belongs to its domain or to its infrastructure",
            "a module is a vertical slice made of a hexagon and its adapters"),
    EVERY_CLASS_BELONGS_TO_A_DECLARED_ZONE(Category.STRUCTURE,
            "every class of the base package belongs to a declared module, shared kernel, annotations package, "
                    + "technical module or composition root",
            "an undeclared package escapes every rule of the Hive"),

    DOMAIN_SERVICES_ARE_ANNOTATED(Category.ANNOTATIONS,
            "domain services implementing an API port are annotated with the domain service annotation",
            "the composition root discovers domain services through their annotation"),
    DOMAIN_SERVICE_ANNOTATION_IS_RESERVED_TO_DOMAIN_SERVICES(Category.ANNOTATIONS,
            "classes annotated with the domain service annotation belong to a domain and implement one of its API ports",
            "a domain service implements the features of its own domain"),
    STUBS_ARE_SPI_IMPLEMENTATIONS_IN_THE_DOMAIN(Category.ANNOTATIONS,
            "classes annotated with the stub annotation belong to a domain and implement one of its SPI ports",
            "a stub stands in for an adapter of its own domain");

    private final Category category;
    private final String description;
    private final String rationale;

    HiveRule(Category category, String description, String rationale) {
        this.category = category;
        this.description = description;
        this.rationale = rationale;
    }

    public Category category() {
        return category;
    }

    public String description() {
        return description;
    }

    public String rationale() {
        return rationale;
    }

    public enum Category {
        HEXAGON,
        HIVE,
        SHARED,
        STRUCTURE,
        ANNOTATIONS
    }
}

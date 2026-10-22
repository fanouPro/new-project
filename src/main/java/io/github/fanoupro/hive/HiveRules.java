package io.github.fanoupro.hive;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.Dependency;
import com.tngtech.archunit.core.domain.JavaAnnotation;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.CompositeArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import io.github.fanoupro.hive.HiveTopology.ZoneKind;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static java.util.stream.Collectors.joining;

/**
 * The ArchUnit rules of the Hive pattern, for a given {@link Hive}.
 * <pre>{@code
 * @AnalyzeClasses(packages = "com.acme.shop", importOptions = ImportOption.DoNotIncludeTests.class)
 * class HiveArchitectureTest {
 *
 *     @ArchTest
 *     static final ArchRule hive = HiveRules.of(HIVE).all();
 * }
 * }</pre>
 * Rules depending on an optional declaration (shared kernels, annotations, technical modules, composition roots,
 * base package, domain service and stub annotations, functional API ports) only apply when it is declared.
 */
public final class HiveRules {

    private static final List<String> JPA_ENTITIES = List.of("jakarta.persistence.Entity", "javax.persistence.Entity");
    private static final List<String> JPA_TABLES = List.of("jakarta.persistence.Table", "javax.persistence.Table");
    private static final String SPRING_DATA_TABLE = "org.springframework.data.relational.core.mapping.Table";

    private final Hive hive;
    private final HiveTopology topology;
    private final Map<HiveRule, ArchRule> rules = new EnumMap<>(HiveRule.class);

    private HiveRules(Hive hive) {
        this.hive = hive;
        this.topology = new HiveTopology(hive);
        for (HiveRule rule : HiveRule.values()) {
            if (appliesTo(rule)) {
                rules.put(rule, create(rule)
                        .as("[" + rule.name() + "] " + rule.description())
                        .because(rule.rationale())
                        .allowEmptyShould(true));
            }
        }
    }

    public static HiveRules of(Hive hive) {
        return new HiveRules(hive);
    }

    /**
     * The rules applying to this Hive, given what it declares.
     */
    public Set<HiveRule> applicable() {
        return Collections.unmodifiableSet(EnumSet.copyOf(rules.keySet()));
    }

    public ArchRule rule(HiveRule rule) {
        ArchRule archRule = rules.get(rule);
        if (archRule == null) {
            throw new IllegalArgumentException(rule + " does not apply: declare what it checks in the Hive first");
        }
        return archRule;
    }

    /**
     * All the {@linkplain #applicable() applicable} rules, as a single rule reporting every violation.
     */
    public ArchRule all() {
        return CompositeArchRule.of(rules.values()).as("classes follow the Hive pattern (" + rules.size() + " rules)");
    }

    private boolean appliesTo(HiveRule rule) {
        return switch (rule) {
            case API_PORTS_ARE_FUNCTIONAL -> hive.requiresFunctionalApiPorts();
            case SHARED_KERNELS_ARE_PURE -> !hive.sharedKernels().isEmpty();
            case ANNOTATIONS_ARE_PURE -> !hive.annotations().isEmpty();
            case TECHNICAL_MODULES_ARE_DOMAIN_AGNOSTIC, DOMAINS_DO_NOT_DEPEND_ON_TECHNICAL_MODULES ->
                    !hive.technicalModules().isEmpty();
            case NOTHING_DEPENDS_ON_THE_COMPOSITION_ROOT, COMPOSITION_ROOT_DOES_NOT_REACH_INTO_DOMAINS ->
                    !hive.compositionRoots().isEmpty();
            case EVERY_CLASS_BELONGS_TO_A_DECLARED_ZONE -> hive.basePackage().isPresent();
            case DOMAIN_SERVICES_ARE_ANNOTATED, DOMAIN_SERVICE_ANNOTATION_IS_RESERVED_TO_DOMAIN_SERVICES ->
                    hive.domainServiceAnnotation().isPresent();
            case STUBS_ARE_SPI_IMPLEMENTATIONS_IN_THE_DOMAIN -> hive.stubAnnotation().isPresent();
            default -> true;
        };
    }

    private ArchRule create(HiveRule rule) {
        HiveTopology t = topology;
        String tag = "[" + rule.name() + "] ";
        return switch (rule) {
            case DOMAIN_DOES_NOT_DEPEND_ON_ITS_INFRASTRUCTURE -> noDependencies(tag, domains(),
                    (origin, target) -> t.sameModule(origin, target) && t.isInInfrastructure(target));
            case DOMAIN_IS_FRAMEWORK_FREE -> noDependencies(tag, domains(),
                    (origin, target) -> t.isExternal(target));
            case API_PORTS_ARE_INTERFACES -> classes().that(described("API ports", t::isInApi))
                    .and().areTopLevelClasses()
                    .should(satisfy(tag, "be interfaces", JavaClass::isInterface, type -> "is not an interface"));
            case SPI_PORTS_ARE_INTERFACES -> classes().that(described("SPI ports", t::isInSpi))
                    .and().areTopLevelClasses()
                    .should(satisfy(tag, "be interfaces", JavaClass::isInterface, type -> "is not an interface"));
            case API_PORTS_ARE_FUNCTIONAL -> classes().that(described("API ports", this::isApiPortInterface))
                    .should(satisfy(tag, "declare exactly one abstract method",
                            port -> abstractMethodsOf(port) == 1,
                            port -> "declares " + abstractMethodsOf(port) + " abstract methods"));
            case API_PORTS_ARE_IMPLEMENTED_BY_THE_DOMAIN -> classes()
                    .that(described("classes implementing an API port",
                            type -> type.getAllRawInterfaces().stream().anyMatch(t::isInApi)))
                    .should(implementPortsOnlyFrom(tag, t::isInApi,
                            (type, port) -> t.sameModule(type, port) && t.isInDomain(type)));
            case SPI_PORTS_ARE_IMPLEMENTED_BY_ADAPTERS -> classes()
                    .that(described("classes implementing an SPI port",
                            type -> type.getAllRawInterfaces().stream().anyMatch(t::isInSpi)))
                    .should(implementPortsOnlyFrom(tag, t::isInSpi,
                            (type, port) -> t.sameModule(type, port) && (t.isInInfrastructure(type)
                                    || (t.isInDomain(type) && t.isStub(type))
                                    || (type.isInterface() && t.isInSpi(type)))));
            case DRIVING_ADAPTERS_ONLY_USE_API_PORTS -> noDependencies(tag,
                    described("driving adapters", t::isInDrivingAdapter),
                    (origin, target) -> t.sameModule(origin, target) && (t.isInSpi(target)
                            || (t.isInInfrastructure(target) && !t.isInDrivingAdapter(target)
                            && !t.isInConfiguration(target))));
            case INFRASTRUCTURE_USES_DOMAIN_SERVICES_THROUGH_API_PORTS -> noDependencies(tag,
                    described("infrastructure outside of configuration",
                            type -> t.isInInfrastructure(type) && !t.isInConfiguration(type)),
                    (origin, target) -> t.sameModule(origin, target) && t.isInDomain(target)
                            && t.isDomainService(target));
            case DRIVEN_ADAPTERS_DO_NOT_CALL_BACK_INTO_THEIR_DOMAIN -> noDependencies(tag,
                    described("SPI adapters", type -> t.isInInfrastructure(type) && t.implementsOwnSpiPort(type)),
                    (origin, target) -> t.sameModule(origin, target) && t.isInApi(target));
            case DOMAINS_ARE_ISOLATED -> noDependencies(tag, domains(), t::isInOtherModule);
            case INFRASTRUCTURES_ARE_ISOLATED -> noDependencies(tag, infrastructures(),
                    (origin, target) -> t.isInOtherModule(origin, target) && t.isInInfrastructure(target));
            case OTHER_MODULES_ARE_REACHED_THROUGH_THEIR_API -> noDependencies(tag, infrastructures(),
                    (origin, target) -> t.isInOtherModule(origin, target) && t.isInDomain(target)
                            && (t.isInSpi(target) || t.isDomainService(target) || t.isStub(target)));
            case ONLY_IN_PROCESS_ADAPTERS_REACH_OTHER_MODULES -> noDependencies(tag,
                    described("infrastructure outside of in-process adapters",
                            type -> t.isInInfrastructure(type) && !t.isInInProcessAdapter(type)),
                    (origin, target) -> t.isInOtherModule(origin, target) && t.isInDomain(target));
            case IN_PROCESS_ADAPTERS_IMPLEMENT_AN_SPI_PORT -> classes()
                    .that(described("in-process adapters calling an API port of another module",
                            type -> t.isInInProcessAdapter(type) && callsApiOfAnotherModule(type)))
                    .should(satisfy(tag, "implement an SPI port of their own module", t::implementsOwnSpiPort,
                            type -> "calls an API port of another module but implements no SPI port of its own"));
            case DATASTORES_ARE_SEGREGATED -> classes().that(described("classes of modules", t::isInModule))
                    .should(new MapTablesNoOtherModuleMaps(tag));
            case SHARED_KERNELS_ARE_PURE -> noDependencies(tag, in(ZoneKind.SHARED_KERNEL),
                    (origin, target) -> !t.isPure(target) && !t.isIn(target, ZoneKind.SHARED_KERNEL)
                            && !t.isIn(target, ZoneKind.ANNOTATIONS) && !t.isIn(target, ZoneKind.COMPOSITION_ROOT));
            case ANNOTATIONS_ARE_PURE -> noDependencies(tag, in(ZoneKind.ANNOTATIONS),
                    (origin, target) -> !t.isPure(target) && !t.isIn(target, ZoneKind.ANNOTATIONS)
                            && !t.isIn(target, ZoneKind.COMPOSITION_ROOT));
            case TECHNICAL_MODULES_ARE_DOMAIN_AGNOSTIC -> noDependencies(tag, in(ZoneKind.TECHNICAL_MODULE),
                    (origin, target) -> t.isInModule(target));
            case DOMAINS_DO_NOT_DEPEND_ON_TECHNICAL_MODULES -> noDependencies(tag, domains(),
                    (origin, target) -> t.isIn(target, ZoneKind.TECHNICAL_MODULE));
            case NOTHING_DEPENDS_ON_THE_COMPOSITION_ROOT -> noDependencies(tag,
                    described("classes outside of composition roots",
                            type -> t.isInDeclaredZone(type) && !t.isIn(type, ZoneKind.COMPOSITION_ROOT)),
                    (origin, target) -> t.isIn(target, ZoneKind.COMPOSITION_ROOT));
            case COMPOSITION_ROOT_DOES_NOT_REACH_INTO_DOMAINS -> noDependencies(tag, in(ZoneKind.COMPOSITION_ROOT),
                    (origin, target) -> t.isInModule(target) && t.isInDomain(target));
            case EVERY_MODULE_HAS_ITS_OWN_CONFIGURATION -> classes()
                    .that(described("classes of modules", t::isInModule))
                    .should(new HaveAConfigurationInEachModule(tag));
            case MODULES_ARE_SPLIT_INTO_DOMAIN_AND_INFRASTRUCTURE -> classes()
                    .that(described("classes of modules", type -> t.isInModule(type) && !isPackageInfo(type)))
                    .should(satisfy(tag, "belong to the domain or to the infrastructure of their module",
                            type -> t.isInDomain(type) || t.isInInfrastructure(type),
                            type -> "is in " + t.describe(type)));
            case EVERY_CLASS_BELONGS_TO_A_DECLARED_ZONE -> classes()
                    .that(described("classes of the base package",
                            type -> t.isInBasePackage(type) && !isPackageInfo(type)))
                    .should(satisfy(tag, "belong to a declared zone", t::isInDeclaredZone,
                            type -> "belongs to no declared zone"));
            case DOMAIN_SERVICES_ARE_ANNOTATED -> classes()
                    .that(described("domain services", type -> t.isInDomain(type) && isConcrete(type)
                            && t.implementsOwnApiPort(type)))
                    .should(satisfy(tag, "be annotated with the domain service annotation",
                            type -> type.isAnnotatedWith(hive.domainServiceAnnotation().orElseThrow()),
                            type -> "is not annotated with <" + hive.domainServiceAnnotation().orElseThrow() + ">"));
            case DOMAIN_SERVICE_ANNOTATION_IS_RESERVED_TO_DOMAIN_SERVICES -> classes()
                    .that().areAnnotatedWith(hive.domainServiceAnnotation().orElseThrow())
                    .should(satisfy(tag, "belong to a domain and implement one of its API ports",
                            type -> t.isInDomain(type) && t.implementsOwnApiPort(type),
                            type -> t.isInDomain(type)
                                    ? "implements no API port of " + t.describe(type)
                                    : "is in " + t.describe(type) + " instead of a domain"));
            case STUBS_ARE_SPI_IMPLEMENTATIONS_IN_THE_DOMAIN -> classes()
                    .that().areAnnotatedWith(hive.stubAnnotation().orElseThrow())
                    .should(satisfy(tag, "belong to a domain and implement one of its SPI ports",
                            type -> t.isInDomain(type) && t.implementsOwnSpiPort(type),
                            type -> t.isInDomain(type)
                                    ? "implements no SPI port of " + t.describe(type)
                                    : "is in " + t.describe(type) + " instead of a domain"));
        };
    }

    private DescribedPredicate<JavaClass> domains() {
        return described("domains", topology::isInDomain);
    }

    private DescribedPredicate<JavaClass> infrastructures() {
        return described("infrastructures", topology::isInInfrastructure);
    }

    private DescribedPredicate<JavaClass> in(ZoneKind kind) {
        return described(kind.name().toLowerCase(Locale.ROOT).replace('_', ' ') + " classes",
                type -> topology.isIn(type, kind));
    }

    private static DescribedPredicate<JavaClass> described(String description, Predicate<JavaClass> predicate) {
        return DescribedPredicate.describe(description, predicate);
    }

    private ArchCondition<JavaClass> noDependencies(String tag, BiPredicate<JavaClass, JavaClass> forbidden) {
        return new ArchCondition<>("not have forbidden dependencies") {
            @Override
            public void check(JavaClass origin, ConditionEvents events) {
                for (Dependency dependency : origin.getDirectDependenciesFromSelf()) {
                    JavaClass target = dependency.getTargetClass().getBaseComponentType();
                    if (forbidden.test(origin, target)) {
                        events.add(SimpleConditionEvent.violated(dependency, tag + topology.describe(origin) + " -> "
                                + topology.describe(target) + ": " + dependency.getDescription()));
                    }
                }
            }
        };
    }

    private ArchRule noDependencies(String tag, DescribedPredicate<JavaClass> origins,
                                    BiPredicate<JavaClass, JavaClass> forbidden) {
        return classes().that(origins).should(noDependencies(tag, forbidden));
    }

    private static ArchCondition<JavaClass> satisfy(String tag, String description, Predicate<JavaClass> predicate,
                                                    Function<JavaClass, String> violation) {
        return new ArchCondition<>(description) {
            @Override
            public void check(JavaClass type, ConditionEvents events) {
                if (!predicate.test(type)) {
                    events.add(SimpleConditionEvent.violated(type,
                            tag + "Class <" + type.getName() + "> " + violation.apply(type) + " in "
                                    + type.getSourceCodeLocation()));
                }
            }
        };
    }

    private ArchCondition<JavaClass> implementPortsOnlyFrom(String tag, Predicate<JavaClass> isPort,
                                                            BiPredicate<JavaClass, JavaClass> allowed) {
        return new ArchCondition<>("implement ports of their own layer only") {
            @Override
            public void check(JavaClass type, ConditionEvents events) {
                type.getAllRawInterfaces().stream()
                        .filter(isPort)
                        .filter(port -> !allowed.test(type, port))
                        .forEach(port -> events.add(SimpleConditionEvent.violated(type,
                                tag + "Class <" + type.getName() + "> in " + topology.describe(type)
                                        + " implements <" + port.getName() + "> of " + topology.describe(port)
                                        + " in " + type.getSourceCodeLocation())));
            }
        };
    }

    private boolean isApiPortInterface(JavaClass type) {
        return topology.isInApi(type) && type.isInterface() && !type.isAnnotation() && type.isTopLevelClass()
                && !isPackageInfo(type);
    }

    private static boolean isPackageInfo(JavaClass type) {
        return type.getSimpleName().equals("package-info");
    }

    private static long abstractMethodsOf(JavaClass port) {
        return port.getAllMethods().stream()
                .filter(method -> method.getOwner().isInterface())
                .filter(method -> method.getModifiers().contains(JavaModifier.ABSTRACT))
                .map(method -> method.getName() + method.getRawParameterTypes().stream().map(JavaClass::getName).toList())
                .distinct()
                .count();
    }

    private static boolean isConcrete(JavaClass type) {
        return !type.isInterface() && !type.getModifiers().contains(JavaModifier.ABSTRACT);
    }

    private boolean callsApiOfAnotherModule(JavaClass type) {
        return type.getDirectDependenciesFromSelf().stream()
                .map(dependency -> dependency.getTargetClass().getBaseComponentType())
                .anyMatch(target -> topology.isInOtherModule(type, target) && topology.isInApi(target));
    }

    private final class HaveAConfigurationInEachModule extends ArchCondition<JavaClass> {

        private final String tag;
        private final Map<String, Boolean> configuredModules = new TreeMap<>();
        private final Map<String, HiveModule> modules = new TreeMap<>();

        HaveAConfigurationInEachModule(String tag) {
            super("have a configuration in each module");
            this.tag = tag;
        }

        @Override
        public void init(Collection<JavaClass> allObjectsToTest) {
            configuredModules.clear();
            modules.clear();
        }

        @Override
        public void check(JavaClass type, ConditionEvents events) {
            topology.moduleOf(type).ifPresent(module -> {
                modules.put(module.name(), module);
                configuredModules.merge(module.name(), topology.isInConfiguration(type), Boolean::logicalOr);
            });
        }

        @Override
        public void finish(ConditionEvents events) {
            configuredModules.forEach((name, configured) -> {
                if (!configured) {
                    HiveModule module = modules.get(name);
                    events.add(SimpleConditionEvent.violated(module, tag + "Module '" + name
                            + "' has no class in its configuration package <" + module.packageName() + "."
                            + hive.conventions().configuration() + ">"));
                }
            });
        }
    }

    private final class MapTablesNoOtherModuleMaps extends ArchCondition<JavaClass> {

        private final String tag;
        private final Map<String, Map<String, List<String>>> mappersByTable = new TreeMap<>();

        MapTablesNoOtherModuleMaps(String tag) {
            super("map tables that no other module maps");
            this.tag = tag;
        }

        @Override
        public void init(Collection<JavaClass> allObjectsToTest) {
            mappersByTable.clear();
        }

        @Override
        public void check(JavaClass type, ConditionEvents events) {
            tableOf(type).ifPresent(table -> mappersByTable
                    .computeIfAbsent(table, ignored -> new TreeMap<>())
                    .computeIfAbsent(topology.moduleOf(type).orElseThrow().name(), ignored -> new ArrayList<>())
                    .add(type.getName()));
        }

        @Override
        public void finish(ConditionEvents events) {
            mappersByTable.forEach((table, mappersByModule) -> {
                if (mappersByModule.size() > 1) {
                    events.add(SimpleConditionEvent.violated(table, tag + "Table '" + table + "' is mapped by "
                            + mappersByModule.entrySet().stream()
                            .map(mappers -> "module '" + mappers.getKey() + "' " + mappers.getValue())
                            .collect(joining(" and "))));
                }
            });
        }

        private Optional<String> tableOf(JavaClass type) {
            Optional<JavaAnnotation<JavaClass>> entity = annotationOf(type, JPA_ENTITIES);
            if (entity.isPresent()) {
                Optional<JavaAnnotation<JavaClass>> table = annotationOf(type, JPA_TABLES);
                String name = attribute(table, "name")
                        .or(() -> attribute(entity, "name"))
                        .orElse(type.getSimpleName());
                return Optional.of(qualified(attribute(table, "schema"), name));
            }
            Optional<JavaAnnotation<JavaClass>> springDataTable = type.tryGetAnnotationOfType(SPRING_DATA_TABLE);
            if (springDataTable.isPresent()) {
                String name = attribute(springDataTable, "value")
                        .or(() -> attribute(springDataTable, "name"))
                        .orElse(type.getSimpleName());
                return Optional.of(qualified(attribute(springDataTable, "schema"), name));
            }
            return Optional.empty();
        }

        private static Optional<JavaAnnotation<JavaClass>> annotationOf(JavaClass type, List<String> annotations) {
            return annotations.stream()
                    .map(type::tryGetAnnotationOfType)
                    .flatMap(Optional::stream)
                    .findFirst();
        }

        private static Optional<String> attribute(Optional<JavaAnnotation<JavaClass>> annotation, String attribute) {
            return annotation.flatMap(present -> present.get(attribute))
                    .map(String::valueOf)
                    .filter(value -> !value.isBlank());
        }

        private static String qualified(Optional<String> schema, String table) {
            return schema.map(present -> normalized(present) + ".").orElse("") + normalized(table);
        }

        private static String normalized(String identifier) {
            return identifier.replaceAll("[\"`\\[\\]_]", "").toLowerCase(Locale.ROOT);
        }
    }
}

package io.github.fanoupro.hive;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static io.github.fanoupro.hive.Packages.isStrictlyWithin;
import static io.github.fanoupro.hive.Packages.lastSegment;
import static io.github.fanoupro.hive.Packages.requireValidPackage;
import static java.util.Objects.requireNonNull;

/**
 * Declaration of a codebase built with the Hive pattern: which packages are Hive modules, shared kernels,
 * annotations, technical modules and composition roots, and how modules are laid out.
 * <pre>{@code
 * Hive hive = Hive.builder()
 *         .module("com.acme.shop.ordering")
 *         .module("com.acme.shop.catalog")
 *         .sharedKernel("com.acme.shop.money")
 *         .annotations("com.acme.ddd")
 *         .technicalModule("com.acme.shop.hypermedia")
 *         .compositionRoot("com.acme.shop.application")
 *         .basePackage("com.acme.shop")
 *         .domainServiceAnnotation("com.acme.ddd.DomainService")
 *         .build();
 * }</pre>
 */
public final class Hive {

    private final List<HiveModule> modules;
    private final List<String> sharedKernels;
    private final List<String> annotations;
    private final List<String> technicalModules;
    private final List<String> compositionRoots;
    private final Optional<String> basePackage;
    private final List<String> pureLibraries;
    private final HiveConventions conventions;
    private final Optional<String> domainServiceAnnotation;
    private final Optional<String> stubAnnotation;
    private final boolean functionalApiPorts;

    private Hive(Builder builder) {
        this.modules = List.copyOf(builder.modules);
        this.sharedKernels = List.copyOf(builder.sharedKernels);
        this.annotations = List.copyOf(builder.annotations);
        this.technicalModules = List.copyOf(builder.technicalModules);
        this.compositionRoots = List.copyOf(builder.compositionRoots);
        this.basePackage = Optional.ofNullable(builder.basePackage);
        this.pureLibraries = List.copyOf(builder.pureLibraries);
        this.conventions = builder.conventions;
        this.domainServiceAnnotation = Optional.ofNullable(builder.domainServiceAnnotation);
        this.stubAnnotation = Optional.ofNullable(builder.stubAnnotation);
        this.functionalApiPorts = builder.functionalApiPorts;
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<HiveModule> modules() {
        return modules;
    }

    public List<String> sharedKernels() {
        return sharedKernels;
    }

    public List<String> annotations() {
        return annotations;
    }

    public List<String> technicalModules() {
        return technicalModules;
    }

    public List<String> compositionRoots() {
        return compositionRoots;
    }

    public Optional<String> basePackage() {
        return basePackage;
    }

    /**
     * Package identifiers, in ArchUnit syntax, that pure code (domains, shared kernels, annotations) may depend on.
     * Always contains {@code java..}.
     */
    public List<String> pureLibraries() {
        return pureLibraries;
    }

    public HiveConventions conventions() {
        return conventions;
    }

    public Optional<String> domainServiceAnnotation() {
        return domainServiceAnnotation;
    }

    public Optional<String> stubAnnotation() {
        return stubAnnotation;
    }

    public boolean requiresFunctionalApiPorts() {
        return functionalApiPorts;
    }

    public static final class Builder {

        private final List<HiveModule> modules = new ArrayList<>();
        private final List<String> sharedKernels = new ArrayList<>();
        private final List<String> annotations = new ArrayList<>();
        private final List<String> technicalModules = new ArrayList<>();
        private final List<String> compositionRoots = new ArrayList<>();
        private final List<String> pureLibraries = new ArrayList<>(List.of("java.."));
        private String basePackage;
        private HiveConventions conventions = HiveConventions.defaults();
        private String domainServiceAnnotation;
        private String stubAnnotation;
        private boolean functionalApiPorts;

        private Builder() {
        }

        /**
         * Declares a Hive module named after the last segment of its package.
         */
        public Builder module(String packageName) {
            requireValidPackage(packageName, "module package");
            return module(lastSegment(packageName), packageName);
        }

        public Builder module(String name, String packageName) {
            modules.add(new HiveModule(name, packageName));
            return this;
        }

        /**
         * Declares shared kernels: generic sub-domains made of value objects only, with neither ports nor adapters,
         * that every module may use (e.g. money, measurement units).
         */
        public Builder sharedKernel(String... packageNames) {
            return addPackages(sharedKernels, "shared kernel package", packageNames);
        }

        /**
         * Declares the packages of the annotations used to wire domains without making them depend on the
         * dependency injection framework (e.g. {@code @DomainService}, {@code @Stub}).
         */
        public Builder annotations(String... packageNames) {
            return addPackages(annotations, "annotations package", packageNames);
        }

        /**
         * Declares technical modules: infrastructure code shared by the adapters of several modules
         * (e.g. hypermedia, serialization). Domains never use them.
         */
        public Builder technicalModule(String... packageNames) {
            return addPackages(technicalModules, "technical module package", packageNames);
        }

        /**
         * Declares composition roots: the packages assembling modules into a deployable application. A composition
         * root that is also the parent package of other zones only covers its own package, not its sub-packages.
         */
        public Builder compositionRoot(String... packageNames) {
            return addPackages(compositionRoots, "composition root package", packageNames);
        }

        /**
         * Declares the package enclosing the whole Hive: every class under it must belong to a declared zone.
         */
        public Builder basePackage(String packageName) {
            this.basePackage = requireValidPackage(packageName, "base package");
            return this;
        }

        /**
         * Allows pure code (domains, shared kernels, annotations) to depend on these libraries, besides the JDK.
         *
         * @param packageIdentifiers ArchUnit package identifiers, e.g. {@code "kotlin.."} or {@code "org.jspecify.."}
         */
        public Builder pureLibraries(String... packageIdentifiers) {
            for (String identifier : packageIdentifiers) {
                requireNonNull(identifier, "package identifier");
                pureLibraries.add(identifier);
            }
            return this;
        }

        public Builder conventions(HiveConventions conventions) {
            this.conventions = requireNonNull(conventions, "conventions");
            return this;
        }

        public Builder domainServiceAnnotation(Class<? extends Annotation> annotation) {
            return domainServiceAnnotation(annotation.getName());
        }

        /**
         * Declares the annotation marking domain services, the domain classes implementing API ports.
         */
        public Builder domainServiceAnnotation(String annotationClassName) {
            this.domainServiceAnnotation = requireNonNull(annotationClassName, "annotationClassName");
            return this;
        }

        public Builder stubAnnotation(Class<? extends Annotation> annotation) {
            return stubAnnotation(annotation.getName());
        }

        /**
         * Declares the annotation marking stubs: SPI port implementations living in the domain, such as in-memory
         * repositories.
         */
        public Builder stubAnnotation(String annotationClassName) {
            this.stubAnnotation = requireNonNull(annotationClassName, "annotationClassName");
            return this;
        }

        /**
         * Requires every API port to expose exactly one feature, as in the reference implementation.
         */
        public Builder requireFunctionalApiPorts() {
            this.functionalApiPorts = true;
            return this;
        }

        public Hive build() {
            if (modules.isEmpty()) {
                throw new IllegalStateException("A Hive needs at least one module");
            }
            requireUniqueModuleNames();
            requireDistinctZones();
            return new Hive(this);
        }

        private Builder addPackages(List<String> target, String what, String... packageNames) {
            for (String packageName : packageNames) {
                target.add(requireValidPackage(packageName, what));
            }
            return this;
        }

        private void requireUniqueModuleNames() {
            Set<String> names = new HashSet<>();
            for (HiveModule module : modules) {
                if (!names.add(module.name())) {
                    throw new IllegalStateException("Two Hive modules are named '" + module.name() + "'");
                }
            }
        }

        private void requireDistinctZones() {
            List<String> zones = Stream.of(
                            modules.stream().map(HiveModule::packageName),
                            sharedKernels.stream(),
                            annotations.stream(),
                            technicalModules.stream())
                    .flatMap(packages -> packages)
                    .toList();
            List<String> allZones = Stream.concat(zones.stream(), compositionRoots.stream()).toList();
            Set<String> seen = new HashSet<>();
            for (String zone : allZones) {
                if (!seen.add(zone)) {
                    throw new IllegalStateException("The package '" + zone + "' is declared twice");
                }
            }
            for (String zone : zones) {
                for (String other : allZones) {
                    if (isStrictlyWithin(zone, other) && !compositionRoots.contains(other)) {
                        throw new IllegalStateException(
                                "The package '" + zone + "' is nested in the declared package '" + other + "'");
                    }
                }
            }
            for (String root : compositionRoots) {
                for (String other : zones) {
                    if (isStrictlyWithin(root, other)) {
                        throw new IllegalStateException(
                                "The composition root '" + root + "' is nested in the declared package '" + other + "'");
                    }
                }
            }
        }
    }
}

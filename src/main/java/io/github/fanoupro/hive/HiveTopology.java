package io.github.fanoupro.hive;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.PackageMatcher;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static io.github.fanoupro.hive.Packages.isStrictlyWithin;
import static io.github.fanoupro.hive.Packages.isWithin;
import static io.github.fanoupro.hive.Packages.relativeTo;

/**
 * Locates classes in a {@link Hive}: which zone they belong to and, inside a module, which layer.
 */
final class HiveTopology {

    enum ZoneKind {
        MODULE("module"),
        SHARED_KERNEL("shared kernel"),
        ANNOTATIONS("annotations"),
        TECHNICAL_MODULE("technical module"),
        COMPOSITION_ROOT("composition root");

        private final String label;

        ZoneKind(String label) {
            this.label = label;
        }
    }

    record Zone(ZoneKind kind, String packageName, HiveModule module, boolean coversSubPackages) {

        boolean contains(String classPackage) {
            return coversSubPackages ? isWithin(classPackage, packageName) : classPackage.equals(packageName);
        }
    }

    private final Hive hive;
    private final HiveConventions conventions;
    private final List<Zone> zonesFromMostSpecific;
    private final List<PackageMatcher> pureLibraries;

    HiveTopology(Hive hive) {
        this.hive = hive;
        this.conventions = hive.conventions();
        this.zonesFromMostSpecific = zonesOf(hive);
        this.pureLibraries = hive.pureLibraries().stream().map(PackageMatcher::of).toList();
    }

    private static List<Zone> zonesOf(Hive hive) {
        List<Zone> zones = new ArrayList<>();
        hive.modules().forEach(module -> zones.add(new Zone(ZoneKind.MODULE, module.packageName(), module, true)));
        hive.sharedKernels().forEach(kernel -> zones.add(new Zone(ZoneKind.SHARED_KERNEL, kernel, null, true)));
        hive.annotations().forEach(annotations -> zones.add(new Zone(ZoneKind.ANNOTATIONS, annotations, null, true)));
        hive.technicalModules().forEach(module -> zones.add(new Zone(ZoneKind.TECHNICAL_MODULE, module, null, true)));
        List<String> nonRootZones = zones.stream().map(Zone::packageName).toList();
        hive.compositionRoots().forEach(root -> zones.add(new Zone(ZoneKind.COMPOSITION_ROOT, root, null,
                nonRootZones.stream().noneMatch(zone -> isStrictlyWithin(zone, root)))));
        zones.sort(Comparator.comparingInt((Zone zone) -> zone.packageName().length()).reversed());
        return List.copyOf(zones);
    }

    Optional<Zone> zoneOf(JavaClass javaClass) {
        String classPackage = javaClass.getBaseComponentType().getPackageName();
        return zonesFromMostSpecific.stream().filter(zone -> zone.contains(classPackage)).findFirst();
    }

    boolean isIn(JavaClass javaClass, ZoneKind kind) {
        return zoneOf(javaClass).filter(zone -> zone.kind() == kind).isPresent();
    }

    boolean isInDeclaredZone(JavaClass javaClass) {
        return zoneOf(javaClass).isPresent();
    }

    boolean isInBasePackage(JavaClass javaClass) {
        return hive.basePackage().filter(base -> isWithin(javaClass.getPackageName(), base)).isPresent();
    }

    Optional<HiveModule> moduleOf(JavaClass javaClass) {
        return zoneOf(javaClass).map(Zone::module);
    }

    boolean isInModule(JavaClass javaClass) {
        return moduleOf(javaClass).isPresent();
    }

    boolean sameModule(JavaClass first, JavaClass second) {
        Optional<HiveModule> module = moduleOf(first);
        return module.isPresent() && module.equals(moduleOf(second));
    }

    boolean isInOtherModule(JavaClass origin, JavaClass target) {
        return isInModule(target) && !sameModule(origin, target);
    }

    boolean isPure(JavaClass javaClass) {
        JavaClass type = javaClass.getBaseComponentType();
        return type.isPrimitive() || pureLibraries.stream().anyMatch(library -> library.matches(type.getPackageName()));
    }

    boolean isExternal(JavaClass javaClass) {
        return !isPure(javaClass) && !isInDeclaredZone(javaClass);
    }

    boolean isInDomain(JavaClass javaClass) {
        return isInLayer(javaClass, conventions.domain());
    }

    boolean isInApi(JavaClass javaClass) {
        return isInLayer(javaClass, conventions.api());
    }

    boolean isInSpi(JavaClass javaClass) {
        return isInLayer(javaClass, conventions.spi());
    }

    boolean isInInfrastructure(JavaClass javaClass) {
        return isInLayer(javaClass, conventions.infrastructure());
    }

    boolean isInConfiguration(JavaClass javaClass) {
        return isInLayer(javaClass, conventions.configuration());
    }

    boolean isInDrivingAdapter(JavaClass javaClass) {
        return conventions.drivingAdapters().stream().anyMatch(adapters -> isInLayer(javaClass, adapters));
    }

    boolean isInInProcessAdapter(JavaClass javaClass) {
        return conventions.inProcessAdapters().stream().anyMatch(adapters -> isInLayer(javaClass, adapters));
    }

    private boolean isInLayer(JavaClass javaClass, String layer) {
        JavaClass type = javaClass.getBaseComponentType();
        return moduleOf(type)
                .map(module -> isWithin(relativeTo(type.getPackageName(), module.packageName()), layer))
                .orElse(false);
    }

    boolean implementsApiPortOf(JavaClass javaClass, HiveModule module) {
        return javaClass.getAllRawInterfaces().stream()
                .anyMatch(port -> isInApi(port) && moduleOf(port).filter(module::equals).isPresent());
    }

    boolean implementsSpiPortOf(JavaClass javaClass, HiveModule module) {
        return javaClass.getAllRawInterfaces().stream()
                .anyMatch(port -> isInSpi(port) && moduleOf(port).filter(module::equals).isPresent());
    }

    boolean implementsOwnApiPort(JavaClass javaClass) {
        return moduleOf(javaClass).filter(module -> implementsApiPortOf(javaClass, module)).isPresent();
    }

    boolean implementsOwnSpiPort(JavaClass javaClass) {
        return moduleOf(javaClass).filter(module -> implementsSpiPortOf(javaClass, module)).isPresent();
    }

    boolean isDomainService(JavaClass javaClass) {
        JavaClass type = javaClass.getBaseComponentType();
        return (isInDomain(type) && !type.isInterface() && implementsOwnApiPort(type))
                || hive.domainServiceAnnotation().filter(type::isAnnotatedWith).isPresent();
    }

    boolean isStub(JavaClass javaClass) {
        return hive.stubAnnotation().filter(javaClass.getBaseComponentType()::isAnnotatedWith).isPresent();
    }

    String describe(JavaClass javaClass) {
        JavaClass type = javaClass.getBaseComponentType();
        Optional<Zone> zone = zoneOf(type);
        if (zone.isEmpty()) {
            return isPure(type) ? "pure library" : "external package '" + type.getPackageName() + "'";
        }
        if (zone.get().kind() != ZoneKind.MODULE) {
            return zone.get().kind().label + " '" + zone.get().packageName() + "'";
        }
        return "module '" + zone.get().module().name() + "' " + layerOf(type);
    }

    private String layerOf(JavaClass javaClass) {
        if (isInApi(javaClass)) {
            return "API";
        }
        if (isInSpi(javaClass)) {
            return "SPI";
        }
        if (isInDomain(javaClass)) {
            return "domain";
        }
        if (isInInfrastructure(javaClass)) {
            return "infrastructure";
        }
        return "outside of domain and infrastructure";
    }
}

package io.github.fanoupro.ruche;

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
import io.github.fanoupro.ruche.TopologieRuche.TypeDeZone;

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
 * Les règles ArchUnit du pattern Hive, pour une {@link Ruche} donnée.
 * <pre>{@code
 * @AnalyzeClasses(packages = "com.acme.boutique", importOptions = ImportOption.DoNotIncludeTests.class)
 * class ArchitectureRucheTest {
 *
 *     @ArchTest
 *     static final ArchRule ruche = ReglesRuche.de(RUCHE).toutes();
 * }
 * }</pre>
 * Les règles qui dépendent d'une déclaration facultative (noyaux partagés, annotations, modules techniques, racines de
 * composition, paquetage de base, annotations de service du domaine et de bouchon, ports API fonctionnels) ne
 * s'appliquent que si elle est faite.
 */
public final class ReglesRuche {

    private static final List<String> ENTITES_JPA = List.of("jakarta.persistence.Entity", "javax.persistence.Entity");
    private static final List<String> TABLES_JPA = List.of("jakarta.persistence.Table", "javax.persistence.Table");
    private static final String TABLE_SPRING_DATA = "org.springframework.data.relational.core.mapping.Table";

    private final Ruche ruche;
    private final TopologieRuche topologie;
    private final Map<RegleRuche, ArchRule> regles = new EnumMap<>(RegleRuche.class);

    private ReglesRuche(Ruche ruche) {
        this.ruche = ruche;
        this.topologie = new TopologieRuche(ruche);
        for (RegleRuche regle : RegleRuche.values()) {
            if (sAppliqueA(regle)) {
                regles.put(regle, creer(regle)
                        .as("[" + regle.name() + "] " + regle.description() + ", car " + regle.justification())
                        .allowEmptyShould(true));
            }
        }
    }

    public static ReglesRuche de(Ruche ruche) {
        return new ReglesRuche(ruche);
    }

    /**
     * Les règles qui s'appliquent à cette ruche, d'après ce qu'elle déclare.
     */
    public Set<RegleRuche> applicables() {
        return Collections.unmodifiableSet(EnumSet.copyOf(regles.keySet()));
    }

    public ArchRule regle(RegleRuche regle) {
        ArchRule regleArchUnit = regles.get(regle);
        if (regleArchUnit == null) {
            throw new IllegalArgumentException(
                    regle + " ne s'applique pas : déclarez d'abord dans la ruche ce qu'elle vérifie");
        }
        return regleArchUnit;
    }

    /**
     * Toutes les règles {@linkplain #applicables() applicables}, réunies en une seule règle qui signale chaque
     * violation.
     */
    public ArchRule toutes() {
        return CompositeArchRule.of(regles.values())
                .as("les classes respectent le pattern Hive (" + regles.size() + " règles)");
    }

    private boolean sAppliqueA(RegleRuche regle) {
        return switch (regle) {
            case LES_PORTS_API_SONT_FONCTIONNELS -> ruche.exigePortsApiFonctionnels();
            case LES_NOYAUX_PARTAGES_SONT_PURS -> !ruche.noyauxPartages().isEmpty();
            case LES_ANNOTATIONS_SONT_PURES -> !ruche.annotations().isEmpty();
            case LES_MODULES_TECHNIQUES_IGNORENT_LE_METIER, LES_DOMAINES_NE_DEPENDENT_PAS_DES_MODULES_TECHNIQUES ->
                    !ruche.modulesTechniques().isEmpty();
            case RIEN_NE_DEPEND_DE_LA_RACINE_DE_COMPOSITION, LA_RACINE_DE_COMPOSITION_N_ATTEINT_PAS_LES_DOMAINES ->
                    !ruche.racinesDeComposition().isEmpty();
            case CHAQUE_CLASSE_APPARTIENT_A_UNE_ZONE_DECLAREE -> ruche.paquetageDeBase().isPresent();
            case LES_SERVICES_DU_DOMAINE_SONT_ANNOTES, L_ANNOTATION_DE_SERVICE_EST_RESERVEE_AUX_SERVICES_DU_DOMAINE ->
                    ruche.annotationServiceDuDomaine().isPresent();
            case LES_BOUCHONS_IMPLEMENTENT_UN_PORT_SPI_DU_DOMAINE -> ruche.annotationBouchon().isPresent();
            default -> true;
        };
    }

    private ArchRule creer(RegleRuche regle) {
        TopologieRuche t = topologie;
        String etiquette = "[" + regle.name() + "] ";
        return switch (regle) {
            case LE_DOMAINE_NE_DEPEND_PAS_DE_SON_INFRASTRUCTURE -> aucuneDependance(etiquette, domaines(),
                    (origine, cible) -> t.memeModule(origine, cible) && t.estDansLInfrastructure(cible));
            case LE_DOMAINE_NE_DEPEND_QUE_DE_BIBLIOTHEQUES_PURES -> aucuneDependance(etiquette, domaines(),
                    (origine, cible) -> t.estExterne(cible));
            case LES_PORTS_API_SONT_DES_INTERFACES -> classes().that(decrit("ports API", t::estDansLApi))
                    .and().areTopLevelClasses()
                    .should(satisfaire(etiquette, "être des interfaces", JavaClass::isInterface,
                            type -> "n'est pas une interface"));
            case LES_PORTS_SPI_SONT_DES_INTERFACES -> classes().that(decrit("ports SPI", t::estDansLaSpi))
                    .and().areTopLevelClasses()
                    .should(satisfaire(etiquette, "être des interfaces", JavaClass::isInterface,
                            type -> "n'est pas une interface"));
            case LES_PORTS_API_SONT_FONCTIONNELS -> classes()
                    .that(decrit("ports API", this::estUneInterfaceDePortApi))
                    .should(satisfaire(etiquette, "déclarer exactement une méthode abstraite",
                            port -> methodesAbstraitesDe(port) == 1,
                            port -> methodesAbstraitesDe(port) == 0
                                    ? "ne déclare aucune méthode abstraite"
                                    : "déclare " + methodesAbstraitesDe(port) + " méthodes abstraites"));
            case LES_PORTS_API_SONT_IMPLEMENTES_PAR_LE_DOMAINE -> classes()
                    .that(decrit("classes qui implémentent un port API",
                            type -> type.getAllRawInterfaces().stream().anyMatch(t::estDansLApi)))
                    .should(nImplementerDesPortsQue(etiquette, t::estDansLApi,
                            (type, port) -> t.memeModule(type, port) && t.estDansLeDomaine(type)));
            case LES_PORTS_SPI_SONT_IMPLEMENTES_PAR_DES_ADAPTATEURS -> classes()
                    .that(decrit("classes qui implémentent un port SPI",
                            type -> type.getAllRawInterfaces().stream().anyMatch(t::estDansLaSpi)))
                    .should(nImplementerDesPortsQue(etiquette, t::estDansLaSpi,
                            (type, port) -> t.memeModule(type, port) && (t.estDansLInfrastructure(type)
                                    || (t.estDansLeDomaine(type) && t.estUnBouchon(type))
                                    || (type.isInterface() && t.estDansLaSpi(type)))));
            case LES_ADAPTATEURS_PRIMAIRES_N_UTILISENT_QUE_LES_PORTS_API -> aucuneDependance(etiquette,
                    decrit("adaptateurs primaires", t::estUnAdaptateurPrimaire),
                    (origine, cible) -> t.memeModule(origine, cible) && (t.estDansLaSpi(cible)
                            || (t.estDansLInfrastructure(cible) && !t.estUnAdaptateurPrimaire(cible)
                            && !t.estDansLaConfiguration(cible))));
            case L_INFRASTRUCTURE_ATTEINT_LES_SERVICES_DU_DOMAINE_PAR_LES_PORTS_API -> aucuneDependance(etiquette,
                    decrit("infrastructure hors de la configuration",
                            type -> t.estDansLInfrastructure(type) && !t.estDansLaConfiguration(type)),
                    (origine, cible) -> t.memeModule(origine, cible) && t.estDansLeDomaine(cible)
                            && t.estUnServiceDuDomaine(cible));
            case LES_ADAPTATEURS_SECONDAIRES_NE_RAPPELLENT_PAS_LEUR_DOMAINE -> aucuneDependance(etiquette,
                    decrit("adaptateurs SPI",
                            type -> t.estDansLInfrastructure(type) && t.implementeUnPortSpiDeSonModule(type)),
                    (origine, cible) -> t.memeModule(origine, cible) && t.estDansLApi(cible));
            case LES_DOMAINES_SONT_ISOLES -> aucuneDependance(etiquette, domaines(), t::estDansUnAutreModule);
            case LES_INFRASTRUCTURES_SONT_ISOLEES -> aucuneDependance(etiquette, infrastructures(),
                    (origine, cible) -> t.estDansUnAutreModule(origine, cible) && t.estDansLInfrastructure(cible));
            case LES_AUTRES_MODULES_SONT_ATTEINTS_PAR_LEUR_API -> aucuneDependance(etiquette, infrastructures(),
                    (origine, cible) -> t.estDansUnAutreModule(origine, cible) && t.estDansLeDomaine(cible)
                            && (t.estDansLaSpi(cible) || t.estUnServiceDuDomaine(cible) || t.estUnBouchon(cible)));
            case SEULS_LES_ADAPTATEURS_INTRAPROCESSUS_ATTEIGNENT_LES_AUTRES_MODULES -> aucuneDependance(etiquette,
                    decrit("infrastructure hors des adaptateurs intraprocessus",
                            type -> t.estDansLInfrastructure(type) && !t.estUnAdaptateurIntraprocessus(type)),
                    (origine, cible) -> t.estDansUnAutreModule(origine, cible) && t.estDansLeDomaine(cible));
            case LES_ADAPTATEURS_INTRAPROCESSUS_IMPLEMENTENT_UN_PORT_SPI -> classes()
                    .that(decrit("adaptateurs intraprocessus qui appellent un port API d'un autre module",
                            type -> t.estUnAdaptateurIntraprocessus(type) && appelleLApiDUnAutreModule(type)))
                    .should(satisfaire(etiquette, "implémenter un port SPI de leur propre module",
                            t::implementeUnPortSpiDeSonModule,
                            type -> "appelle un port API d'un autre module sans implémenter de port SPI de son "
                                    + "propre module"));
            case LES_DONNEES_SONT_CLOISONNEES -> classes().that(decrit("classes des modules", t::estDansUnModule))
                    .should(new NePartagerAucuneTable(etiquette));
            case LES_NOYAUX_PARTAGES_SONT_PURS -> aucuneDependance(etiquette, dans(TypeDeZone.NOYAU_PARTAGE),
                    (origine, cible) -> !t.estPure(cible) && !t.estDans(cible, TypeDeZone.NOYAU_PARTAGE)
                            && !t.estDans(cible, TypeDeZone.ANNOTATIONS)
                            && !t.estDans(cible, TypeDeZone.RACINE_DE_COMPOSITION));
            case LES_ANNOTATIONS_SONT_PURES -> aucuneDependance(etiquette, dans(TypeDeZone.ANNOTATIONS),
                    (origine, cible) -> !t.estPure(cible) && !t.estDans(cible, TypeDeZone.ANNOTATIONS)
                            && !t.estDans(cible, TypeDeZone.RACINE_DE_COMPOSITION));
            case LES_MODULES_TECHNIQUES_IGNORENT_LE_METIER -> aucuneDependance(etiquette,
                    dans(TypeDeZone.MODULE_TECHNIQUE), (origine, cible) -> t.estDansUnModule(cible));
            case LES_DOMAINES_NE_DEPENDENT_PAS_DES_MODULES_TECHNIQUES -> aucuneDependance(etiquette, domaines(),
                    (origine, cible) -> t.estDans(cible, TypeDeZone.MODULE_TECHNIQUE));
            case RIEN_NE_DEPEND_DE_LA_RACINE_DE_COMPOSITION -> aucuneDependance(etiquette,
                    decrit("classes hors des racines de composition",
                            type -> t.estDansUneZoneDeclaree(type)
                                    && !t.estDans(type, TypeDeZone.RACINE_DE_COMPOSITION)),
                    (origine, cible) -> t.estDans(cible, TypeDeZone.RACINE_DE_COMPOSITION));
            case LA_RACINE_DE_COMPOSITION_N_ATTEINT_PAS_LES_DOMAINES -> aucuneDependance(etiquette,
                    dans(TypeDeZone.RACINE_DE_COMPOSITION),
                    (origine, cible) -> t.estDansUnModule(cible) && t.estDansLeDomaine(cible));
            case CHAQUE_MODULE_A_SA_PROPRE_CONFIGURATION -> classes()
                    .that(decrit("classes des modules", t::estDansUnModule))
                    .should(new AvoirUneConfigurationDansChaqueModule(etiquette));
            case LES_MODULES_SE_DIVISENT_EN_DOMAINE_ET_INFRASTRUCTURE -> classes()
                    .that(decrit("classes des modules", type -> t.estDansUnModule(type) && !estUnPackageInfo(type)))
                    .should(satisfaire(etiquette, "appartenir au domaine ou à l'infrastructure de leur module",
                            type -> t.estDansLeDomaine(type) || t.estDansLInfrastructure(type),
                            type -> "n'appartient ni au domaine ni à l'infrastructure du module '"
                                    + nomDuModuleDe(type) + "'"));
            case CHAQUE_CLASSE_APPARTIENT_A_UNE_ZONE_DECLAREE -> classes()
                    .that(decrit("classes du paquetage de base",
                            type -> t.estDansLePaquetageDeBase(type) && !estUnPackageInfo(type)))
                    .should(satisfaire(etiquette, "appartenir à une zone déclarée", t::estDansUneZoneDeclaree,
                            type -> "n'appartient à aucune zone déclarée"));
            case LES_SERVICES_DU_DOMAINE_SONT_ANNOTES -> classes()
                    .that(decrit("services du domaine", type -> t.estDansLeDomaine(type) && estConcrete(type)
                            && t.implementeUnPortApiDeSonModule(type)))
                    .should(satisfaire(etiquette, "porter l'annotation de service du domaine",
                            type -> type.isAnnotatedWith(ruche.annotationServiceDuDomaine().orElseThrow()),
                            type -> "n'est pas annotée avec <" + ruche.annotationServiceDuDomaine().orElseThrow()
                                    + ">"));
            case L_ANNOTATION_DE_SERVICE_EST_RESERVEE_AUX_SERVICES_DU_DOMAINE -> classes()
                    .that().areAnnotatedWith(ruche.annotationServiceDuDomaine().orElseThrow())
                    .should(satisfaire(etiquette, "appartenir à un domaine et implémenter un de ses ports API",
                            type -> t.estDansLeDomaine(type) && t.implementeUnPortApiDeSonModule(type),
                            type -> t.estDansLeDomaine(type)
                                    ? "n'implémente aucun port API du module '" + nomDuModuleDe(type) + "'"
                                    : "n'appartient à aucun domaine (" + t.decrire(type) + ")"));
            case LES_BOUCHONS_IMPLEMENTENT_UN_PORT_SPI_DU_DOMAINE -> classes()
                    .that().areAnnotatedWith(ruche.annotationBouchon().orElseThrow())
                    .should(satisfaire(etiquette, "appartenir à un domaine et implémenter un de ses ports SPI",
                            type -> t.estDansLeDomaine(type) && t.implementeUnPortSpiDeSonModule(type),
                            type -> t.estDansLeDomaine(type)
                                    ? "n'implémente aucun port SPI du module '" + nomDuModuleDe(type) + "'"
                                    : "n'appartient à aucun domaine (" + t.decrire(type) + ")"));
        };
    }

    private DescribedPredicate<JavaClass> domaines() {
        return decrit("domaines", topologie::estDansLeDomaine);
    }

    private DescribedPredicate<JavaClass> infrastructures() {
        return decrit("infrastructures", topologie::estDansLInfrastructure);
    }

    private DescribedPredicate<JavaClass> dans(TypeDeZone type) {
        return decrit("classes des " + type.pluriel(), classe -> topologie.estDans(classe, type));
    }

    private static DescribedPredicate<JavaClass> decrit(String description, Predicate<JavaClass> predicat) {
        return DescribedPredicate.describe(description, predicat);
    }

    private ArchCondition<JavaClass> aucuneDependance(String etiquette, BiPredicate<JavaClass, JavaClass> interdite) {
        return new ArchCondition<>("ne pas avoir de dépendances interdites") {
            @Override
            public void check(JavaClass origine, ConditionEvents evenements) {
                for (Dependency dependance : origine.getDirectDependenciesFromSelf()) {
                    JavaClass cible = dependance.getTargetClass().getBaseComponentType();
                    if (interdite.test(origine, cible)) {
                        evenements.add(SimpleConditionEvent.violated(dependance, etiquette
                                + topologie.decrire(origine) + " -> " + topologie.decrire(cible) + " : "
                                + dependance.getDescription()));
                    }
                }
            }
        };
    }

    private ArchRule aucuneDependance(String etiquette, DescribedPredicate<JavaClass> origines,
                                      BiPredicate<JavaClass, JavaClass> interdite) {
        return classes().that(origines).should(aucuneDependance(etiquette, interdite));
    }

    private static ArchCondition<JavaClass> satisfaire(String etiquette, String description,
                                                       Predicate<JavaClass> predicat,
                                                       Function<JavaClass, String> violation) {
        return new ArchCondition<>(description) {
            @Override
            public void check(JavaClass type, ConditionEvents evenements) {
                if (!predicat.test(type)) {
                    evenements.add(SimpleConditionEvent.violated(type,
                            etiquette + "La classe <" + type.getName() + "> " + violation.apply(type) + " dans "
                                    + type.getSourceCodeLocation()));
                }
            }
        };
    }

    private ArchCondition<JavaClass> nImplementerDesPortsQue(String etiquette, Predicate<JavaClass> estUnPort,
                                                             BiPredicate<JavaClass, JavaClass> autorise) {
        return new ArchCondition<>("n'implémenter que des ports de leur propre couche") {
            @Override
            public void check(JavaClass type, ConditionEvents evenements) {
                type.getAllRawInterfaces().stream()
                        .filter(estUnPort)
                        .filter(port -> !autorise.test(type, port))
                        .forEach(port -> evenements.add(SimpleConditionEvent.violated(type,
                                etiquette + "La classe <" + type.getName() + "> (" + topologie.decrire(type)
                                        + ") implémente <" + port.getName() + "> (" + topologie.decrire(port)
                                        + ") dans " + type.getSourceCodeLocation())));
            }
        };
    }

    private String nomDuModuleDe(JavaClass type) {
        return topologie.moduleDe(type).orElseThrow().nom();
    }

    private boolean estUneInterfaceDePortApi(JavaClass type) {
        return topologie.estDansLApi(type) && type.isInterface() && !type.isAnnotation() && type.isTopLevelClass()
                && !estUnPackageInfo(type);
    }

    private static boolean estUnPackageInfo(JavaClass type) {
        return type.getSimpleName().equals("package-info");
    }

    private static long methodesAbstraitesDe(JavaClass port) {
        return port.getAllMethods().stream()
                .filter(methode -> methode.getOwner().isInterface())
                .filter(methode -> methode.getModifiers().contains(JavaModifier.ABSTRACT))
                .map(methode -> methode.getName()
                        + methode.getRawParameterTypes().stream().map(JavaClass::getName).toList())
                .distinct()
                .count();
    }

    private static boolean estConcrete(JavaClass type) {
        return !type.isInterface() && !type.getModifiers().contains(JavaModifier.ABSTRACT);
    }

    private boolean appelleLApiDUnAutreModule(JavaClass type) {
        return type.getDirectDependenciesFromSelf().stream()
                .map(dependance -> dependance.getTargetClass().getBaseComponentType())
                .anyMatch(cible -> topologie.estDansUnAutreModule(type, cible) && topologie.estDansLApi(cible));
    }

    private final class AvoirUneConfigurationDansChaqueModule extends ArchCondition<JavaClass> {

        private final String etiquette;
        private final Map<String, Boolean> modulesConfigures = new TreeMap<>();
        private final Map<String, ModuleRuche> modules = new TreeMap<>();

        AvoirUneConfigurationDansChaqueModule(String etiquette) {
            super("avoir une configuration dans chaque module");
            this.etiquette = etiquette;
        }

        @Override
        public void init(Collection<JavaClass> classesAVerifier) {
            modulesConfigures.clear();
            modules.clear();
        }

        @Override
        public void check(JavaClass type, ConditionEvents evenements) {
            topologie.moduleDe(type).ifPresent(module -> {
                modules.put(module.nom(), module);
                modulesConfigures.merge(module.nom(), topologie.estDansLaConfiguration(type), Boolean::logicalOr);
            });
        }

        @Override
        public void finish(ConditionEvents evenements) {
            modulesConfigures.forEach((nom, configure) -> {
                if (!configure) {
                    ModuleRuche module = modules.get(nom);
                    evenements.add(SimpleConditionEvent.violated(module, etiquette + "Le module '" + nom
                            + "' n'a aucune classe dans son paquetage de configuration <" + module.paquetage() + "."
                            + ruche.conventions().configuration() + ">"));
                }
            });
        }
    }

    private final class NePartagerAucuneTable extends ArchCondition<JavaClass> {

        private final String etiquette;
        private final Map<String, Map<String, List<String>>> classesParTable = new TreeMap<>();

        NePartagerAucuneTable(String etiquette) {
            super("mapper des tables qu'aucun autre module ne mappe");
            this.etiquette = etiquette;
        }

        @Override
        public void init(Collection<JavaClass> classesAVerifier) {
            classesParTable.clear();
        }

        @Override
        public void check(JavaClass type, ConditionEvents evenements) {
            tableDe(type).ifPresent(table -> classesParTable
                    .computeIfAbsent(table, ignoree -> new TreeMap<>())
                    .computeIfAbsent(topologie.moduleDe(type).orElseThrow().nom(), ignoree -> new ArrayList<>())
                    .add(type.getName()));
        }

        @Override
        public void finish(ConditionEvents evenements) {
            classesParTable.forEach((table, classesParModule) -> {
                if (classesParModule.size() > 1) {
                    evenements.add(SimpleConditionEvent.violated(table, etiquette + "La table '" + table
                            + "' est mappée par " + classesParModule.entrySet().stream()
                            .map(entree -> "le module '" + entree.getKey() + "' " + entree.getValue())
                            .collect(joining(" et "))));
                }
            });
        }

        private Optional<String> tableDe(JavaClass type) {
            Optional<JavaAnnotation<JavaClass>> entite = annotationDe(type, ENTITES_JPA);
            if (entite.isPresent()) {
                Optional<JavaAnnotation<JavaClass>> table = annotationDe(type, TABLES_JPA);
                String nom = attribut(table, "name")
                        .or(() -> attribut(entite, "name"))
                        .orElse(type.getSimpleName());
                return Optional.of(qualifiee(attribut(table, "schema"), nom));
            }
            Optional<JavaAnnotation<JavaClass>> tableSpringData = type.tryGetAnnotationOfType(TABLE_SPRING_DATA);
            if (tableSpringData.isPresent()) {
                String nom = attribut(tableSpringData, "value")
                        .or(() -> attribut(tableSpringData, "name"))
                        .orElse(type.getSimpleName());
                return Optional.of(qualifiee(attribut(tableSpringData, "schema"), nom));
            }
            return Optional.empty();
        }

        private static Optional<JavaAnnotation<JavaClass>> annotationDe(JavaClass type, List<String> annotations) {
            return annotations.stream()
                    .map(type::tryGetAnnotationOfType)
                    .flatMap(Optional::stream)
                    .findFirst();
        }

        private static Optional<String> attribut(Optional<JavaAnnotation<JavaClass>> annotation, String attribut) {
            return annotation.flatMap(presente -> presente.get(attribut))
                    .map(String::valueOf)
                    .filter(valeur -> !valeur.isBlank());
        }

        private static String qualifiee(Optional<String> schema, String table) {
            return schema.map(present -> normalise(present) + ".").orElse("") + normalise(table);
        }

        private static String normalise(String identifiant) {
            return identifiant.replaceAll("[\"`\\[\\]_]", "").toLowerCase(Locale.ROOT);
        }
    }
}

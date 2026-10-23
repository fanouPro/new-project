package io.github.fanoupro.ruche;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static io.github.fanoupro.ruche.Paquetages.dernierSegment;
import static io.github.fanoupro.ruche.Paquetages.estSousPaquetageDe;
import static io.github.fanoupro.ruche.Paquetages.exigerPaquetageValide;
import static java.util.Objects.requireNonNull;

/**
 * Déclaration d'une base de code construite selon le pattern Hive : quels paquetages sont des modules de la ruche,
 * des noyaux partagés, des annotations, des modules techniques et des racines de composition, et comment les modules
 * sont organisés.
 * <pre>{@code
 * Ruche ruche = Ruche.nouvelle()
 *         .module("com.acme.boutique.commande")
 *         .module("com.acme.boutique.catalogue")
 *         .noyauPartage("com.acme.boutique.monnaie")
 *         .annotations("com.acme.ddd")
 *         .moduleTechnique("com.acme.boutique.hypermedia")
 *         .racineDeComposition("com.acme.boutique.application")
 *         .paquetageDeBase("com.acme.boutique")
 *         .annotationServiceDuDomaine("com.acme.ddd.ServiceDuDomaine")
 *         .construire();
 * }</pre>
 */
public final class Ruche {

    private final List<ModuleRuche> modules;
    private final List<String> noyauxPartages;
    private final List<String> annotations;
    private final List<String> modulesTechniques;
    private final List<String> racinesDeComposition;
    private final Optional<String> paquetageDeBase;
    private final List<String> bibliothequesPures;
    private final ConventionsRuche conventions;
    private final Optional<String> annotationServiceDuDomaine;
    private final Optional<String> annotationBouchon;
    private final boolean portsApiFonctionnels;

    private Ruche(Monteur monteur) {
        this.modules = List.copyOf(monteur.modules);
        this.noyauxPartages = List.copyOf(monteur.noyauxPartages);
        this.annotations = List.copyOf(monteur.annotations);
        this.modulesTechniques = List.copyOf(monteur.modulesTechniques);
        this.racinesDeComposition = List.copyOf(monteur.racinesDeComposition);
        this.paquetageDeBase = Optional.ofNullable(monteur.paquetageDeBase);
        this.bibliothequesPures = List.copyOf(monteur.bibliothequesPures);
        this.conventions = monteur.conventions;
        this.annotationServiceDuDomaine = Optional.ofNullable(monteur.annotationServiceDuDomaine);
        this.annotationBouchon = Optional.ofNullable(monteur.annotationBouchon);
        this.portsApiFonctionnels = monteur.portsApiFonctionnels;
    }

    public static Monteur nouvelle() {
        return new Monteur();
    }

    public List<ModuleRuche> modules() {
        return modules;
    }

    public List<String> noyauxPartages() {
        return noyauxPartages;
    }

    public List<String> annotations() {
        return annotations;
    }

    public List<String> modulesTechniques() {
        return modulesTechniques;
    }

    public List<String> racinesDeComposition() {
        return racinesDeComposition;
    }

    public Optional<String> paquetageDeBase() {
        return paquetageDeBase;
    }

    /**
     * Identifiants de paquetages, en syntaxe ArchUnit, dont le code pur (domaines, noyaux partagés, annotations) peut
     * dépendre. Contient toujours {@code java..}.
     */
    public List<String> bibliothequesPures() {
        return bibliothequesPures;
    }

    public ConventionsRuche conventions() {
        return conventions;
    }

    public Optional<String> annotationServiceDuDomaine() {
        return annotationServiceDuDomaine;
    }

    public Optional<String> annotationBouchon() {
        return annotationBouchon;
    }

    public boolean exigePortsApiFonctionnels() {
        return portsApiFonctionnels;
    }

    public static final class Monteur {

        private final List<ModuleRuche> modules = new ArrayList<>();
        private final List<String> noyauxPartages = new ArrayList<>();
        private final List<String> annotations = new ArrayList<>();
        private final List<String> modulesTechniques = new ArrayList<>();
        private final List<String> racinesDeComposition = new ArrayList<>();
        private final List<String> bibliothequesPures = new ArrayList<>(List.of("java.."));
        private String paquetageDeBase;
        private ConventionsRuche conventions = ConventionsRuche.parDefaut();
        private String annotationServiceDuDomaine;
        private String annotationBouchon;
        private boolean portsApiFonctionnels;

        private Monteur() {
        }

        /**
         * Déclare un module de la ruche, nommé d'après le dernier segment de son paquetage.
         */
        public Monteur module(String paquetage) {
            exigerPaquetageValide(paquetage, "paquetage de module");
            return module(dernierSegment(paquetage), paquetage);
        }

        public Monteur module(String nom, String paquetage) {
            modules.add(new ModuleRuche(nom, paquetage));
            return this;
        }

        /**
         * Déclare des noyaux partagés : des sous-domaines génériques faits uniquement d'objets-valeurs, sans ports ni
         * adaptateurs, que tous les modules peuvent utiliser (par exemple la monnaie, les unités de mesure).
         */
        public Monteur noyauPartage(String... paquetages) {
            return ajouterPaquetages(noyauxPartages, "paquetage de noyau partagé", paquetages);
        }

        /**
         * Déclare les paquetages des annotations qui servent à câbler les domaines sans les rendre dépendants du
         * conteneur d'injection de dépendances (par exemple {@code @ServiceDuDomaine}, {@code @Bouchon}).
         */
        public Monteur annotations(String... paquetages) {
            return ajouterPaquetages(annotations, "paquetage d'annotations", paquetages);
        }

        /**
         * Déclare des modules techniques : du code d'infrastructure partagé par les adaptateurs de plusieurs modules
         * (par exemple l'hypermédia, la sérialisation). Les domaines ne les utilisent jamais.
         */
        public Monteur moduleTechnique(String... paquetages) {
            return ajouterPaquetages(modulesTechniques, "paquetage de module technique", paquetages);
        }

        /**
         * Déclare des racines de composition : les paquetages qui assemblent les modules en une application
         * déployable. Une racine de composition qui est aussi le paquetage parent d'autres zones ne couvre que son
         * propre paquetage, pas ses sous-paquetages.
         */
        public Monteur racineDeComposition(String... paquetages) {
            return ajouterPaquetages(racinesDeComposition, "paquetage de racine de composition", paquetages);
        }

        /**
         * Déclare le paquetage qui englobe toute la ruche : chaque classe qu'il contient doit appartenir à une zone
         * déclarée.
         */
        public Monteur paquetageDeBase(String paquetage) {
            this.paquetageDeBase = exigerPaquetageValide(paquetage, "paquetage de base");
            return this;
        }

        /**
         * Autorise le code pur (domaines, noyaux partagés, annotations) à dépendre de ces bibliothèques, en plus du
         * JDK.
         *
         * @param identifiantsDePaquetages identifiants de paquetages ArchUnit, par exemple {@code "kotlin.."} ou
         *                                 {@code "org.jspecify.."}
         */
        public Monteur bibliothequesPures(String... identifiantsDePaquetages) {
            for (String identifiant : identifiantsDePaquetages) {
                requireNonNull(identifiant, "identifiant de paquetage");
                bibliothequesPures.add(identifiant);
            }
            return this;
        }

        public Monteur conventions(ConventionsRuche conventions) {
            this.conventions = requireNonNull(conventions, "conventions");
            return this;
        }

        public Monteur annotationServiceDuDomaine(Class<? extends Annotation> annotation) {
            return annotationServiceDuDomaine(annotation.getName());
        }

        /**
         * Déclare l'annotation qui marque les services du domaine, les classes du domaine qui implémentent des ports
         * API.
         */
        public Monteur annotationServiceDuDomaine(String nomDeClasseAnnotation) {
            this.annotationServiceDuDomaine = requireNonNull(nomDeClasseAnnotation, "nomDeClasseAnnotation");
            return this;
        }

        public Monteur annotationBouchon(Class<? extends Annotation> annotation) {
            return annotationBouchon(annotation.getName());
        }

        /**
         * Déclare l'annotation qui marque les bouchons : des implémentations de ports SPI qui vivent dans le domaine,
         * comme des dépôts en mémoire.
         */
        public Monteur annotationBouchon(String nomDeClasseAnnotation) {
            this.annotationBouchon = requireNonNull(nomDeClasseAnnotation, "nomDeClasseAnnotation");
            return this;
        }

        /**
         * Exige que chaque port API expose exactement une fonctionnalité, comme dans l'implémentation de référence.
         */
        public Monteur exigerPortsApiFonctionnels() {
            this.portsApiFonctionnels = true;
            return this;
        }

        public Ruche construire() {
            if (modules.isEmpty()) {
                throw new IllegalStateException("Une ruche a besoin d'au moins un module");
            }
            exigerDesNomsDeModulesUniques();
            exigerDesZonesDistinctes();
            return new Ruche(this);
        }

        private Monteur ajouterPaquetages(List<String> cible, String quoi, String... paquetages) {
            for (String paquetage : paquetages) {
                cible.add(exigerPaquetageValide(paquetage, quoi));
            }
            return this;
        }

        private void exigerDesNomsDeModulesUniques() {
            Set<String> noms = new HashSet<>();
            for (ModuleRuche module : modules) {
                if (!noms.add(module.nom())) {
                    throw new IllegalStateException("Deux modules de la ruche s'appellent '" + module.nom() + "'");
                }
            }
        }

        private void exigerDesZonesDistinctes() {
            List<String> zones = Stream.of(
                            modules.stream().map(ModuleRuche::paquetage),
                            noyauxPartages.stream(),
                            annotations.stream(),
                            modulesTechniques.stream())
                    .flatMap(paquetages -> paquetages)
                    .toList();
            List<String> toutesLesZones = Stream.concat(zones.stream(), racinesDeComposition.stream()).toList();
            Set<String> vues = new HashSet<>();
            for (String zone : toutesLesZones) {
                if (!vues.add(zone)) {
                    throw new IllegalStateException("Le paquetage '" + zone + "' est déclaré deux fois");
                }
            }
            for (String zone : zones) {
                for (String autre : toutesLesZones) {
                    if (estSousPaquetageDe(zone, autre) && !racinesDeComposition.contains(autre)) {
                        throw new IllegalStateException(
                                "Le paquetage '" + zone + "' est imbriqué dans le paquetage déclaré '" + autre + "'");
                    }
                }
            }
            for (String racine : racinesDeComposition) {
                for (String autre : zones) {
                    if (estSousPaquetageDe(racine, autre)) {
                        throw new IllegalStateException("La racine de composition '" + racine
                                + "' est imbriquée dans le paquetage déclaré '" + autre + "'");
                    }
                }
            }
        }
    }
}

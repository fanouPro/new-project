package io.github.fanoupro.ruche;

/**
 * Catalogue des règles d'architecture du pattern Hive, avec leur identifiant, leur description et leur justification
 * dans chaque {@link Langue}.
 */
public enum RegleRuche {

    LE_DOMAINE_NE_DEPEND_PAS_DE_SON_INFRASTRUCTURE(Categorie.HEXAGONE,
            "le domaine d'un module ne dépend pas de son infrastructure",
            "l'hexagone doit rester indépendant des préoccupations techniques",
            en("DOMAIN_DOES_NOT_DEPEND_ON_ITS_INFRASTRUCTURE",
                    "the domain of a module does not depend on its infrastructure",
                    "the hexagon must stay independent of technical concerns")),
    LE_DOMAINE_NE_DEPEND_QUE_DE_BIBLIOTHEQUES_PURES(Categorie.HEXAGONE,
            "le domaine d'un module ne dépend que du JDK et des bibliothèques déclarées pures, hors de la ruche",
            "Spring, JPA ou Jackson appartiennent aux adaptateurs, pas au métier",
            en("DOMAIN_IS_FRAMEWORK_FREE",
                    "the domain of a module only depends on the JDK and on declared pure libraries outside the Hive",
                    "frameworks belong to adapters, not to business logic")),
    LES_PORTS_API_SONT_DES_INTERFACES(Categorie.HEXAGONE,
            "les ports API sont des interfaces",
            "un port API est le contrat d'une fonctionnalité, implémenté par un service du domaine",
            en("API_PORTS_ARE_INTERFACES",
                    "API ports are interfaces",
                    "an API port is the contract of a feature, implemented by a domain service")),
    LES_PORTS_SPI_SONT_DES_INTERFACES(Categorie.HEXAGONE,
            "les ports SPI sont des interfaces",
            "un port SPI est ce que le domaine attend du monde extérieur, implémenté par un adaptateur",
            en("SPI_PORTS_ARE_INTERFACES",
                    "SPI ports are interfaces",
                    "an SPI port is what the domain expects from the outside world, implemented by an adapter")),
    LES_PORTS_API_SONT_FONCTIONNELS(Categorie.HEXAGONE,
            "les ports API déclarent exactement une méthode abstraite",
            "chaque port API expose une seule fonctionnalité",
            en("API_PORTS_ARE_FUNCTIONAL",
                    "API ports declare exactly one abstract method",
                    "each API port exposes a single feature")),
    LES_PORTS_API_SONT_IMPLEMENTES_PAR_LE_DOMAINE(Categorie.HEXAGONE,
            "les ports API ne sont implémentés que par le domaine de leur propre module",
            "les fonctionnalités relèvent du métier, implémenté par les services du domaine",
            en("API_PORTS_ARE_IMPLEMENTED_BY_THE_DOMAIN",
                    "API ports are only implemented by the domain of their own module",
                    "features are business logic, implemented by domain services")),
    LES_PORTS_SPI_SONT_IMPLEMENTES_PAR_DES_ADAPTATEURS(Categorie.HEXAGONE,
            "les ports SPI ne sont implémentés que par l'infrastructure de leur propre module, "
                    + "ou par des bouchons de son domaine",
            "le domaine doit ignorer comment ses ports SPI sont satisfaits",
            en("SPI_PORTS_ARE_IMPLEMENTED_BY_ADAPTERS",
                    "SPI ports are only implemented by the infrastructure of their own module, "
                            + "or by stubs of its domain",
                    "the domain must not know how its SPI ports are fulfilled")),
    LES_ADAPTATEURS_PRIMAIRES_N_UTILISENT_QUE_LES_PORTS_API(Categorie.HEXAGONE,
            "les adaptateurs primaires atteignent leur module par ses ports API, "
                    + "jamais par ses ports SPI ni par ses adaptateurs secondaires",
            "on n'entre dans l'hexagone que par ses ports API",
            en("DRIVING_ADAPTERS_ONLY_USE_API_PORTS",
                    "driving adapters reach their module through API ports, "
                            + "never through SPI ports or driven adapters",
                    "the hexagon is entered through its API ports only")),
    L_INFRASTRUCTURE_ATTEINT_LES_SERVICES_DU_DOMAINE_PAR_LES_PORTS_API(Categorie.HEXAGONE,
            "hors de sa configuration, l'infrastructure d'un module ne dépend jamais des services de son domaine",
            "les adaptateurs dépendent des ports API, pas de leurs implémentations",
            en("INFRASTRUCTURE_USES_DOMAIN_SERVICES_THROUGH_API_PORTS",
                    "outside of its configuration, the infrastructure of a module never depends on its domain "
                            + "services",
                    "adapters depend on API ports, not on their implementations")),
    LES_ADAPTATEURS_SECONDAIRES_NE_RAPPELLENT_PAS_LEUR_DOMAINE(Categorie.HEXAGONE,
            "les adaptateurs qui implémentent un port SPI n'appellent jamais les ports API de leur propre module",
            "un adaptateur secondaire qui rappelle son propre hexagone crée une boucle cachée",
            en("DRIVEN_ADAPTERS_DO_NOT_CALL_BACK_INTO_THEIR_DOMAIN",
                    "adapters implementing an SPI port never call API ports of their own module",
                    "a driven adapter calling its own hexagon creates a hidden loop")),

    LES_DOMAINES_SONT_ISOLES(Categorie.RUCHE,
            "le domaine d'un module ne dépend jamais d'un autre module",
            "chaque module doit rester extractible en service autonome",
            en("DOMAINS_ARE_ISOLATED",
                    "the domain of a module never depends on another module",
                    "each module must stay extractable as a standalone service")),
    LES_INFRASTRUCTURES_SONT_ISOLEES(Categorie.RUCHE,
            "l'infrastructure d'un module ne dépend jamais de l'infrastructure d'un autre module",
            "chaque module possède ses adaptateurs et son stockage de données",
            en("INFRASTRUCTURES_ARE_ISOLATED",
                    "the infrastructure of a module never depends on the infrastructure of another module",
                    "each module owns its adapters and its datastore")),
    LES_AUTRES_MODULES_SONT_ATTEINTS_PAR_LEUR_API(Categorie.RUCHE,
            "un module atteint un autre module par ses ports API et le modèle qu'ils exposent, "
                    + "jamais par ses ports SPI, ses services du domaine ou ses bouchons",
            "les modules communiquent comme le feraient des microservices : par l'API de leur cible",
            en("OTHER_MODULES_ARE_REACHED_THROUGH_THEIR_API",
                    "a module reaches another module through its API ports and the model they expose, "
                            + "never through its SPI ports, domain services or stubs",
                    "modules communicate as they would as microservices: through the API of the target")),
    SEULS_LES_ADAPTATEURS_INTRAPROCESSUS_ATTEIGNENT_LES_AUTRES_MODULES(Categorie.RUCHE,
            "seuls les adaptateurs intraprocessus dépendent d'un autre module",
            "les appels entre modules sont des adaptateurs SPI qui deviennent des clients distants "
                    + "quand un module est extrait",
            en("ONLY_IN_PROCESS_ADAPTERS_REACH_OTHER_MODULES",
                    "only in-process adapters depend on another module",
                    "inter-module calls are SPI adapters that become remote clients when a module is extracted")),
    LES_ADAPTATEURS_INTRAPROCESSUS_IMPLEMENTENT_UN_PORT_SPI(Categorie.RUCHE,
            "les adaptateurs intraprocessus qui appellent un port API d'un autre module implémentent un port SPI "
                    + "de leur propre module",
            "l'appelant ne connaît que son propre port SPI, exprimé avec son propre modèle",
            en("IN_PROCESS_ADAPTERS_IMPLEMENT_AN_SPI_PORT",
                    "in-process adapters calling an API port of another module implement an SPI port of their own "
                            + "module",
                    "the caller only knows its own SPI port, expressed with its own model")),
    LES_DONNEES_SONT_CLOISONNEES(Categorie.RUCHE,
            "aucune table n'est mappée par deux modules",
            "chaque module possède un stockage de données qu'il est seul à utiliser",
            en("DATASTORES_ARE_SEGREGATED",
                    "no table is mapped by two modules",
                    "each module owns a datastore used exclusively by itself")),

    LES_NOYAUX_PARTAGES_SONT_PURS(Categorie.ZONES_PARTAGEES,
            "les noyaux partagés ne dépendent que du JDK, des bibliothèques déclarées pures, d'autres noyaux "
                    + "partagés et des annotations",
            "un noyau partagé est une bibliothèque d'objets-valeurs utilisée par tous les modules",
            en("SHARED_KERNELS_ARE_PURE",
                    "shared kernels only depend on the JDK, declared pure libraries, other shared kernels and "
                            + "annotations",
                    "a shared kernel is a library of value objects used by every module")),
    LES_ANNOTATIONS_SONT_PURES(Categorie.ZONES_PARTAGEES,
            "les annotations ne dépendent que du JDK, des bibliothèques déclarées pures et d'autres annotations",
            "les annotations existent pour tenir le conteneur d'injection de dépendances hors des domaines",
            en("ANNOTATIONS_ARE_PURE",
                    "annotations only depend on the JDK, declared pure libraries and other annotations",
                    "annotations exist to keep the dependency injection framework out of domains")),
    LES_MODULES_TECHNIQUES_IGNORENT_LE_METIER(Categorie.ZONES_PARTAGEES,
            "les modules techniques ne dépendent jamais d'un module de la ruche",
            "les modules techniques sont partagés par les adaptateurs de tous les modules",
            en("TECHNICAL_MODULES_ARE_DOMAIN_AGNOSTIC",
                    "technical modules never depend on a Hive module",
                    "technical modules are shared by the adapters of every module")),
    LES_DOMAINES_NE_DEPENDENT_PAS_DES_MODULES_TECHNIQUES(Categorie.ZONES_PARTAGEES,
            "les domaines ne dépendent jamais des modules techniques",
            "les modules techniques relèvent de l'infrastructure",
            en("DOMAINS_DO_NOT_DEPEND_ON_TECHNICAL_MODULES",
                    "domains never depend on technical modules",
                    "technical modules are infrastructure concerns")),
    RIEN_NE_DEPEND_DE_LA_RACINE_DE_COMPOSITION(Categorie.ZONES_PARTAGEES,
            "rien ne dépend d'une racine de composition",
            "la racine de composition ne fait qu'assembler les modules en un déployable",
            en("NOTHING_DEPENDS_ON_THE_COMPOSITION_ROOT",
                    "nothing depends on a composition root",
                    "the composition root only assembles modules into a deployable")),
    LA_RACINE_DE_COMPOSITION_N_ATTEINT_PAS_LES_DOMAINES(Categorie.ZONES_PARTAGEES,
            "les racines de composition ne dépendent jamais des domaines",
            "chaque module câble son propre domaine depuis la configuration de son infrastructure",
            en("COMPOSITION_ROOT_DOES_NOT_REACH_INTO_DOMAINS",
                    "composition roots never depend on domains",
                    "each module wires its own domain from its infrastructure configuration")),

    CHAQUE_MODULE_A_SA_PROPRE_CONFIGURATION(Categorie.STRUCTURE,
            "chaque module a une configuration dans son infrastructure",
            "un module se câble lui-même, pour pouvoir être déployé dans n'importe quelle racine de composition",
            en("EVERY_MODULE_HAS_ITS_OWN_CONFIGURATION",
                    "every module has a configuration in its infrastructure",
                    "a module wires itself, so that it can be deployed in any composition root")),
    LES_MODULES_SE_DIVISENT_EN_DOMAINE_ET_INFRASTRUCTURE(Categorie.STRUCTURE,
            "chaque classe d'un module appartient à son domaine ou à son infrastructure",
            "un module est une tranche verticale faite d'un hexagone et de ses adaptateurs",
            en("MODULES_ARE_SPLIT_INTO_DOMAIN_AND_INFRASTRUCTURE",
                    "every class of a module belongs to its domain or to its infrastructure",
                    "a module is a vertical slice made of a hexagon and its adapters")),
    CHAQUE_CLASSE_APPARTIENT_A_UNE_ZONE_DECLAREE(Categorie.STRUCTURE,
            "chaque classe du paquetage de base appartient à un module, un noyau partagé, un paquetage "
                    + "d'annotations, un module technique ou une racine de composition déclarés",
            "un paquetage non déclaré échappe à toutes les règles de la ruche",
            en("EVERY_CLASS_BELONGS_TO_A_DECLARED_ZONE",
                    "every class of the base package belongs to a declared module, shared kernel, annotations "
                            + "package, technical module or composition root",
                    "an undeclared package escapes every rule of the Hive")),

    LES_SERVICES_DU_DOMAINE_SONT_ANNOTES(Categorie.ANNOTATIONS,
            "les services du domaine qui implémentent un port API portent l'annotation de service du domaine",
            "la racine de composition découvre les services du domaine par leur annotation",
            en("DOMAIN_SERVICES_ARE_ANNOTATED",
                    "domain services implementing an API port are annotated with the domain service annotation",
                    "the composition root discovers domain services through their annotation")),
    L_ANNOTATION_DE_SERVICE_EST_RESERVEE_AUX_SERVICES_DU_DOMAINE(Categorie.ANNOTATIONS,
            "les classes qui portent l'annotation de service du domaine appartiennent à un domaine et implémentent "
                    + "un de ses ports API",
            "un service du domaine implémente les fonctionnalités de son propre domaine",
            en("DOMAIN_SERVICE_ANNOTATION_IS_RESERVED_TO_DOMAIN_SERVICES",
                    "classes annotated with the domain service annotation belong to a domain and implement one of "
                            + "its API ports",
                    "a domain service implements the features of its own domain")),
    LES_BOUCHONS_IMPLEMENTENT_UN_PORT_SPI_DU_DOMAINE(Categorie.ANNOTATIONS,
            "les classes qui portent l'annotation de bouchon appartiennent à un domaine et implémentent un de ses "
                    + "ports SPI",
            "un bouchon remplace un adaptateur de son propre domaine",
            en("STUBS_ARE_SPI_IMPLEMENTATIONS_IN_THE_DOMAIN",
                    "classes annotated with the stub annotation belong to a domain and implement one of its SPI ports",
                    "a stub stands in for an adapter of its own domain"));

    private final Categorie categorie;
    private final Libelles francais;
    private final Libelles anglais;

    RegleRuche(Categorie categorie, String description, String justification, Libelles anglais) {
        this.categorie = categorie;
        this.francais = new Libelles(name(), description, justification);
        this.anglais = anglais;
    }

    public Categorie categorie() {
        return categorie;
    }

    /**
     * L'identifiant qui préfixe chaque violation de la règle : le nom de la constante en français, son équivalent
     * anglais en anglais (par exemple {@code DOMAINS_ARE_ISOLATED} pour {@link #LES_DOMAINES_SONT_ISOLES}).
     */
    public String identifiant(Langue langue) {
        return libelles(langue).identifiant();
    }

    public String description(Langue langue) {
        return libelles(langue).description();
    }

    public String justification(Langue langue) {
        return libelles(langue).justification();
    }

    private Libelles libelles(Langue langue) {
        return switch (langue) {
            case FRANCAIS -> francais;
            case ANGLAIS -> anglais;
        };
    }

    private static Libelles en(String identifiant, String description, String justification) {
        return new Libelles(identifiant, description, justification);
    }

    private record Libelles(String identifiant, String description, String justification) {
    }

    public enum Categorie {
        HEXAGONE,
        RUCHE,
        ZONES_PARTAGEES,
        STRUCTURE,
        ANNOTATIONS
    }
}

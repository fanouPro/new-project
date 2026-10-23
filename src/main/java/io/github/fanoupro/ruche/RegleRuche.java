package io.github.fanoupro.ruche;

/**
 * Catalogue des règles d'architecture du pattern Hive.
 */
public enum RegleRuche {

    LE_DOMAINE_NE_DEPEND_PAS_DE_SON_INFRASTRUCTURE(Categorie.HEXAGONE,
            "le domaine d'un module ne dépend pas de son infrastructure",
            "l'hexagone doit rester indépendant des préoccupations techniques"),
    LE_DOMAINE_NE_DEPEND_QUE_DE_BIBLIOTHEQUES_PURES(Categorie.HEXAGONE,
            "le domaine d'un module ne dépend que du JDK et des bibliothèques déclarées pures, hors de la ruche",
            "Spring, JPA ou Jackson appartiennent aux adaptateurs, pas au métier"),
    LES_PORTS_API_SONT_DES_INTERFACES(Categorie.HEXAGONE,
            "les ports API sont des interfaces",
            "un port API est le contrat d'une fonctionnalité, implémenté par un service du domaine"),
    LES_PORTS_SPI_SONT_DES_INTERFACES(Categorie.HEXAGONE,
            "les ports SPI sont des interfaces",
            "un port SPI est ce que le domaine attend du monde extérieur, implémenté par un adaptateur"),
    LES_PORTS_API_SONT_FONCTIONNELS(Categorie.HEXAGONE,
            "les ports API déclarent exactement une méthode abstraite",
            "chaque port API expose une seule fonctionnalité"),
    LES_PORTS_API_SONT_IMPLEMENTES_PAR_LE_DOMAINE(Categorie.HEXAGONE,
            "les ports API ne sont implémentés que par le domaine de leur propre module",
            "les fonctionnalités relèvent du métier, implémenté par les services du domaine"),
    LES_PORTS_SPI_SONT_IMPLEMENTES_PAR_DES_ADAPTATEURS(Categorie.HEXAGONE,
            "les ports SPI ne sont implémentés que par l'infrastructure de leur propre module, "
                    + "ou par des bouchons de son domaine",
            "le domaine doit ignorer comment ses ports SPI sont satisfaits"),
    LES_ADAPTATEURS_PRIMAIRES_N_UTILISENT_QUE_LES_PORTS_API(Categorie.HEXAGONE,
            "les adaptateurs primaires atteignent leur module par ses ports API, "
                    + "jamais par ses ports SPI ni par ses adaptateurs secondaires",
            "on n'entre dans l'hexagone que par ses ports API"),
    L_INFRASTRUCTURE_ATTEINT_LES_SERVICES_DU_DOMAINE_PAR_LES_PORTS_API(Categorie.HEXAGONE,
            "hors de sa configuration, l'infrastructure d'un module ne dépend jamais des services de son domaine",
            "les adaptateurs dépendent des ports API, pas de leurs implémentations"),
    LES_ADAPTATEURS_SECONDAIRES_NE_RAPPELLENT_PAS_LEUR_DOMAINE(Categorie.HEXAGONE,
            "les adaptateurs qui implémentent un port SPI n'appellent jamais les ports API de leur propre module",
            "un adaptateur secondaire qui rappelle son propre hexagone crée une boucle cachée"),

    LES_DOMAINES_SONT_ISOLES(Categorie.RUCHE,
            "le domaine d'un module ne dépend jamais d'un autre module",
            "chaque module doit rester extractible en service autonome"),
    LES_INFRASTRUCTURES_SONT_ISOLEES(Categorie.RUCHE,
            "l'infrastructure d'un module ne dépend jamais de l'infrastructure d'un autre module",
            "chaque module possède ses adaptateurs et son stockage de données"),
    LES_AUTRES_MODULES_SONT_ATTEINTS_PAR_LEUR_API(Categorie.RUCHE,
            "un module atteint un autre module par ses ports API et le modèle qu'ils exposent, "
                    + "jamais par ses ports SPI, ses services du domaine ou ses bouchons",
            "les modules communiquent comme le feraient des microservices : par l'API de leur cible"),
    SEULS_LES_ADAPTATEURS_INTRAPROCESSUS_ATTEIGNENT_LES_AUTRES_MODULES(Categorie.RUCHE,
            "seuls les adaptateurs intraprocessus dépendent d'un autre module",
            "les appels entre modules sont des adaptateurs SPI qui deviennent des clients distants "
                    + "quand un module est extrait"),
    LES_ADAPTATEURS_INTRAPROCESSUS_IMPLEMENTENT_UN_PORT_SPI(Categorie.RUCHE,
            "les adaptateurs intraprocessus qui appellent un port API d'un autre module implémentent un port SPI "
                    + "de leur propre module",
            "l'appelant ne connaît que son propre port SPI, exprimé avec son propre modèle"),
    LES_DONNEES_SONT_CLOISONNEES(Categorie.RUCHE,
            "aucune table n'est mappée par deux modules",
            "chaque module possède un stockage de données qu'il est seul à utiliser"),

    LES_NOYAUX_PARTAGES_SONT_PURS(Categorie.ZONES_PARTAGEES,
            "les noyaux partagés ne dépendent que du JDK, des bibliothèques déclarées pures, d'autres noyaux "
                    + "partagés et des annotations",
            "un noyau partagé est une bibliothèque d'objets-valeurs utilisée par tous les modules"),
    LES_ANNOTATIONS_SONT_PURES(Categorie.ZONES_PARTAGEES,
            "les annotations ne dépendent que du JDK, des bibliothèques déclarées pures et d'autres annotations",
            "les annotations existent pour tenir le conteneur d'injection de dépendances hors des domaines"),
    LES_MODULES_TECHNIQUES_IGNORENT_LE_METIER(Categorie.ZONES_PARTAGEES,
            "les modules techniques ne dépendent jamais d'un module de la ruche",
            "les modules techniques sont partagés par les adaptateurs de tous les modules"),
    LES_DOMAINES_NE_DEPENDENT_PAS_DES_MODULES_TECHNIQUES(Categorie.ZONES_PARTAGEES,
            "les domaines ne dépendent jamais des modules techniques",
            "les modules techniques relèvent de l'infrastructure"),
    RIEN_NE_DEPEND_DE_LA_RACINE_DE_COMPOSITION(Categorie.ZONES_PARTAGEES,
            "rien ne dépend d'une racine de composition",
            "la racine de composition ne fait qu'assembler les modules en un déployable"),
    LA_RACINE_DE_COMPOSITION_N_ATTEINT_PAS_LES_DOMAINES(Categorie.ZONES_PARTAGEES,
            "les racines de composition ne dépendent jamais des domaines",
            "chaque module câble son propre domaine depuis la configuration de son infrastructure"),

    CHAQUE_MODULE_A_SA_PROPRE_CONFIGURATION(Categorie.STRUCTURE,
            "chaque module a une configuration dans son infrastructure",
            "un module se câble lui-même, pour pouvoir être déployé dans n'importe quelle racine de composition"),
    LES_MODULES_SE_DIVISENT_EN_DOMAINE_ET_INFRASTRUCTURE(Categorie.STRUCTURE,
            "chaque classe d'un module appartient à son domaine ou à son infrastructure",
            "un module est une tranche verticale faite d'un hexagone et de ses adaptateurs"),
    CHAQUE_CLASSE_APPARTIENT_A_UNE_ZONE_DECLAREE(Categorie.STRUCTURE,
            "chaque classe du paquetage de base appartient à un module, un noyau partagé, un paquetage "
                    + "d'annotations, un module technique ou une racine de composition déclarés",
            "un paquetage non déclaré échappe à toutes les règles de la ruche"),

    LES_SERVICES_DU_DOMAINE_SONT_ANNOTES(Categorie.ANNOTATIONS,
            "les services du domaine qui implémentent un port API portent l'annotation de service du domaine",
            "la racine de composition découvre les services du domaine par leur annotation"),
    L_ANNOTATION_DE_SERVICE_EST_RESERVEE_AUX_SERVICES_DU_DOMAINE(Categorie.ANNOTATIONS,
            "les classes qui portent l'annotation de service du domaine appartiennent à un domaine et implémentent "
                    + "un de ses ports API",
            "un service du domaine implémente les fonctionnalités de son propre domaine"),
    LES_BOUCHONS_IMPLEMENTENT_UN_PORT_SPI_DU_DOMAINE(Categorie.ANNOTATIONS,
            "les classes qui portent l'annotation de bouchon appartiennent à un domaine et implémentent un de ses "
                    + "ports SPI",
            "un bouchon remplace un adaptateur de son propre domaine");

    private final Categorie categorie;
    private final String description;
    private final String justification;

    RegleRuche(Categorie categorie, String description, String justification) {
        this.categorie = categorie;
        this.description = description;
        this.justification = justification;
    }

    public Categorie categorie() {
        return categorie;
    }

    public String description() {
        return description;
    }

    public String justification() {
        return justification;
    }

    public enum Categorie {
        HEXAGONE,
        RUCHE,
        ZONES_PARTAGEES,
        STRUCTURE,
        ANNOTATIONS
    }
}

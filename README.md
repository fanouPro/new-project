# Hive ArchUnit

Tests d'architecture [ArchUnit](https://www.archunit.org/) qui vérifient qu'une base de code Java respecte le
**pattern Hive** (Thomas Pierrain et Julien Topçu).

Le Hive découple la stratégie de déploiement (monolithe modulaire ou microservices) de la conception du logiciel :
*« Model once, deploy as you wish »*. Chaque module correspond à un contexte borné et est un hexagone (domaine et
infrastructure). Les modules ne communiquent que par ports et adaptateurs, comme le feraient des microservices. Un
module peut ainsi être extrait en service autonome sans réécriture. L'ensemble des modules forme une **ruche**.

Les règles sont déduites des deux implémentations de référence des auteurs :

- [`beyondxscratch/hive-pattern`](https://gitlab.com/beyondxscratch/hive-pattern) (Java, Spring Boot) : machine à
  voyager dans le temps, avec le module `nuclearreactor` extrait en microservice ;
- [`beyondxscratch/mandalore-express-hive-pattern`](https://gitlab.com/beyondxscratch/mandalore-express-hive-pattern)
  (Kotlin) : réservation de trains spatiaux.

Tout le code de la bibliothèque est en français : API, identifiants des règles, messages, tests et exemples. Les
identifiants s'écrivent sans accents, les textes avec.

## Utilisation

```java
@AnalyzeClasses(packages = "com.acme.boutique", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureRucheTest {

    static final Ruche RUCHE = Ruche.nouvelle()
            .module("com.acme.boutique.commande")                 // modules : un hexagone par contexte borné
            .module("com.acme.boutique.catalogue")
            .noyauPartage("com.acme.boutique.monnaie")            // objets-valeurs partagés, sans ports ni adaptateurs
            .annotations("com.acme.ddd")                          // @ServiceDuDomaine, @Bouchon : le domaine ignore Spring
            .moduleTechnique("com.acme.boutique.hypermedia")      // code technique partagé par les adaptateurs
            .racineDeComposition("com.acme.boutique.application") // assemble les modules en un déployable
            .paquetageDeBase("com.acme.boutique")                 // tout paquetage non déclaré dessous est une violation
            .annotationServiceDuDomaine("com.acme.ddd.ServiceDuDomaine")
            .annotationBouchon("com.acme.ddd.Bouchon")
            .exigerPortsApiFonctionnels()                         // un port API = une fonctionnalité
            .construire();

    @ArchTest
    static final ArchRule ruche = ReglesRuche.de(RUCHE).toutes();
}
```

`ReglesRuche.de(RUCHE).regle(RegleRuche.LES_DOMAINES_SONT_ISOLES)` donne une règle isolée, par exemple pour
l'adopter progressivement avec un
[`FreezingArchRule`](https://www.archunit.org/userguide/html/000_Index.html#_freezing_arch_rules). Chaque violation
est préfixée par l'identifiant de la règle enfreinte et situe les deux extrémités de la dépendance fautive :

```
[LES_DOMAINES_SONT_ISOLES] domaine du module 'catalogue' -> API du module 'commande' : Class
    <com.acme.boutique.catalogue.domaine.PreneurDeCommande> implements interface
    <com.acme.boutique.commande.domaine.api.PasserCommande> in (PreneurDeCommande.java:0)
```

La fin du message (`Class <…> implements interface <…>`) et l'en-tête du rapport (`Rule '…' was violated`) sont
produits par ArchUnit, en anglais.

La bibliothèque n'est pas encore publiée : `mvn install`, puis dépendance de test
`io.github.fanoupro:hive-archunit:0.1.0-SNAPSHOT`.

## Anatomie d'une ruche

```
com.acme.boutique
├── commande                        module de la ruche (contexte borné)
│   ├── domaine                     l'hexagone : uniquement du métier
│   │   ├── api                     ports API, les fonctionnalités exposées
│   │   └── spi                     ports SPI, ce que le domaine attend du monde extérieur
│   └── infrastructure              les adaptateurs
│       ├── configuration           câblage du module (par exemple son auto-configuration Spring)
│       ├── controleur              adaptateurs primaires
│       ├── intraprocessus          adaptateurs SPI qui appellent l'API d'un autre module, en mémoire
│       ├── clients                 adaptateurs SPI vers un module extrait (HTTP…)
│       └── persistance             adaptateurs SPI vers la base du module, qu'il est seul à utiliser
├── catalogue                       autre module de la ruche
├── monnaie                         noyau partagé
├── hypermedia                      module technique
└── application                     racine de composition
```

C'est la disposition des conventions par défaut, `ConventionsRuche.parDefaut()`. Pour une base de code organisée
comme l'implémentation de référence, avec des paquetages en anglais (`domain`, `infrastructure.controller`,
`infrastructure.inproc`…), déclarez `.conventions(ConventionsRuche.implementationDeReference())`.

Chaque nom de paquetage se configure, par exemple pour la disposition de Mandalore Express :

```java
.conventions(ConventionsRuche.implementationDeReference()
        .avecConfiguration("infrastructure.configurations")
        .avecAdaptateursPrimaires("infrastructure.controllers", "infrastructure.listeners")
        .avecAdaptateursIntraprocessus("infrastructure.clients"))
```

Une racine de composition peut être le paquetage parent des modules (`com.acme.boutique`) : elle ne couvre alors que
son propre paquetage, et non ses sous-paquetages.

### Qui peut dépendre de qui

| Depuis ↓ / vers → | son domaine | son infra | API et modèle d'un autre module | SPI, services, bouchons d'un autre module | infra d'un autre module | noyau partagé | annotations | module technique | racine de composition | bibliothèques externes |
|---|---|---|---|---|---|---|---|---|---|---|
| **Domaine** | ✅ | ❌ | ❌ | ❌ | ❌ | ✅ | ✅ | ❌ | ❌ | ❌ ¹ |
| **Infrastructure** | ✅ ² | ✅ | ✅ adaptateurs intraprocessus | ❌ | ❌ | ✅ | ✅ | ✅ | ❌ | ✅ |
| **Noyau partagé** | – | – | ❌ | ❌ | ❌ | ✅ | ✅ | ❌ | ❌ | ❌ ¹ |
| **Annotations** | – | – | ❌ | ❌ | ❌ | ❌ | ✅ | ❌ | ❌ | ❌ ¹ |
| **Module technique** | – | – | ❌ | ❌ | ❌ | ✅ | ✅ | ✅ | ❌ | ✅ |
| **Racine de composition** | – | – | ❌ | ❌ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |

¹ Sauf le JDK et les bibliothèques déclarées pures (`.bibliothequesPures("kotlin..", "org.jspecify..")`).
² Par les ports API pour les adaptateurs primaires, sans les services du domaine (sauf depuis la configuration), et
sans rappeler sa propre API depuis un adaptateur SPI.

## Catalogue des règles

Chaque règle est prouvée par au moins un exemple non conforme, dans
`src/test/java/io/github/fanoupro/ruche/exemples/violations/`. Les règles marquées † ne s'appliquent que si la ruche
déclare ce qu'elles vérifient.

### Hexagone : chaque module

| Règle | Vérifie | Exemples |
|---|---|---|
| `LE_DOMAINE_NE_DEPEND_PAS_DE_SON_INFRASTRUCTURE` | le domaine ne dépend pas de son infrastructure | `domaine_depend_de_son_infrastructure` |
| `LE_DOMAINE_NE_DEPEND_QUE_DE_BIBLIOTHEQUES_PURES` | le domaine ne dépend que du JDK et des bibliothèques déclarées pures (ni Spring, ni JPA, ni Jackson) | `domaine_depend_d_une_bibliotheque_externe` |
| `LES_PORTS_API_SONT_DES_INTERFACES` | les ports API sont des interfaces | `port_api_est_une_classe` |
| `LES_PORTS_SPI_SONT_DES_INTERFACES` | les ports SPI sont des interfaces | `port_spi_est_une_classe` |
| `LES_PORTS_API_SONT_FONCTIONNELS` † | un port API déclare une seule méthode abstraite | `port_api_expose_plusieurs_fonctionnalites` |
| `LES_PORTS_API_SONT_IMPLEMENTES_PAR_LE_DOMAINE` | un port API n'est implémenté que par le domaine de son module | `port_api_implemente_dans_l_infrastructure`, `port_api_implemente_par_un_autre_module` |
| `LES_PORTS_SPI_SONT_IMPLEMENTES_PAR_DES_ADAPTATEURS` | un port SPI n'est implémenté que par l'infrastructure de son module, ou par un bouchon de son domaine | `port_spi_implemente_dans_le_domaine`, `port_spi_implemente_par_un_autre_module` |
| `LES_ADAPTATEURS_PRIMAIRES_N_UTILISENT_QUE_LES_PORTS_API` | un adaptateur primaire n'utilise ni les ports SPI ni les adaptateurs secondaires | `adaptateur_primaire_utilise_un_port_spi`, `adaptateur_primaire_utilise_un_adaptateur_secondaire` |
| `L_INFRASTRUCTURE_ATTEINT_LES_SERVICES_DU_DOMAINE_PAR_LES_PORTS_API` | hors configuration, l'infrastructure ne dépend pas des services du domaine | `infrastructure_utilise_un_service_du_domaine` |
| `LES_ADAPTATEURS_SECONDAIRES_NE_RAPPELLENT_PAS_LEUR_DOMAINE` | un adaptateur SPI n'appelle pas l'API de son propre module | `adaptateur_secondaire_appelle_sa_propre_api` |

### Ruche : entre les modules

| Règle | Vérifie | Exemples |
|---|---|---|
| `LES_DOMAINES_SONT_ISOLES` | le domaine d'un module ne dépend d'aucun autre module | `domaine_depend_du_domaine_d_un_autre_module`, `domaine_depend_de_l_infrastructure_d_un_autre_module` |
| `LES_INFRASTRUCTURES_SONT_ISOLEES` | l'infrastructure d'un module ne dépend pas de celle d'un autre | `infrastructure_depend_de_l_infrastructure_d_un_autre_module` |
| `LES_AUTRES_MODULES_SONT_ATTEINTS_PAR_LEUR_API` | un autre module n'est atteint que par ses ports API et le modèle qu'ils exposent, jamais par ses ports SPI, ses services ou ses bouchons | `adaptateur_intraprocessus_utilise_la_spi_d_un_autre_module`, `…_un_service_du_domaine_d_un_autre_module`, `…_un_bouchon_d_un_autre_module` |
| `SEULS_LES_ADAPTATEURS_INTRAPROCESSUS_ATTEIGNENT_LES_AUTRES_MODULES` | seuls les adaptateurs intraprocessus dépendent d'un autre module | `adaptateur_primaire_appelle_un_autre_module` |
| `LES_ADAPTATEURS_INTRAPROCESSUS_IMPLEMENTENT_UN_PORT_SPI` | un adaptateur intraprocessus qui appelle l'API d'un autre module implémente un port SPI de son propre module | `adaptateur_intraprocessus_sans_port_spi` |
| `LES_DONNEES_SONT_CLOISONNEES` | aucune table n'est mappée par deux modules (`@Entity`/`@Table` JPA, `@Table` Spring Data ; schéma pris en compte) | `deux_modules_mappent_la_meme_table`, `deux_modules_mappent_la_meme_table_implicite` |

### Zones partagées

| Règle | Vérifie | Exemples |
|---|---|---|
| `LES_NOYAUX_PARTAGES_SONT_PURS` † | un noyau partagé ne dépend que du JDK, des bibliothèques pures, d'autres noyaux partagés et des annotations | `noyau_partage_depend_d_une_bibliotheque_externe`, `noyau_partage_depend_d_un_module` |
| `LES_ANNOTATIONS_SONT_PURES` † | les annotations ne dépendent que du JDK et des bibliothèques pures (pas de `@Component` méta-annoté) | `annotations_dependent_d_une_bibliotheque_externe` |
| `LES_MODULES_TECHNIQUES_IGNORENT_LE_METIER` † | un module technique ne dépend d'aucun module de la ruche | `module_technique_depend_d_un_module` |
| `LES_DOMAINES_NE_DEPENDENT_PAS_DES_MODULES_TECHNIQUES` † | un domaine ne dépend d'aucun module technique | `domaine_depend_d_un_module_technique` |
| `RIEN_NE_DEPEND_DE_LA_RACINE_DE_COMPOSITION` † | rien ne dépend d'une racine de composition | `module_depend_de_la_racine_de_composition` |
| `LA_RACINE_DE_COMPOSITION_N_ATTEINT_PAS_LES_DOMAINES` † | une racine de composition ne dépend d'aucun domaine : chaque module câble le sien | `racine_de_composition_atteint_un_domaine` |

### Structure

| Règle | Vérifie | Exemples |
|---|---|---|
| `CHAQUE_MODULE_A_SA_PROPRE_CONFIGURATION` | chaque module a au moins une classe dans son paquetage de configuration | `module_sans_configuration` |
| `LES_MODULES_SE_DIVISENT_EN_DOMAINE_ET_INFRASTRUCTURE` | toute classe d'un module est dans son domaine ou son infrastructure (`package-info` excepté) | `classe_hors_du_domaine_et_de_l_infrastructure` |
| `CHAQUE_CLASSE_APPARTIENT_A_UNE_ZONE_DECLAREE` † | toute classe du paquetage de base appartient à une zone déclarée | `module_non_declare`, `module_non_declare_sous_une_racine_de_composition` |

### Annotations

| Règle | Vérifie | Exemples |
|---|---|---|
| `LES_SERVICES_DU_DOMAINE_SONT_ANNOTES` † | un service du domaine qui implémente un port API porte l'annotation de service | `service_du_domaine_non_annote` |
| `L_ANNOTATION_DE_SERVICE_EST_RESERVEE_AUX_SERVICES_DU_DOMAINE` † | l'annotation de service ne s'applique qu'à une classe du domaine qui implémente un de ses ports API | `annotation_de_service_dans_l_infrastructure`, `annotation_de_service_sans_port_api` |
| `LES_BOUCHONS_IMPLEMENTENT_UN_PORT_SPI_DU_DOMAINE` † | l'annotation de bouchon ne s'applique qu'à une classe du domaine qui implémente un de ses ports SPI | `bouchon_hors_du_domaine`, `bouchon_sans_port_spi` |

## Comment les tests prouvent les règles

- **Deux ruches conformes** respectent chacune des règles applicables :
  - `exemples/valide` suit les conventions par défaut et exerce les 28 règles : adaptateur intraprocessus, client vers
    un module extrait, bouchon, tables homonymes dans des schémas différents, `package-info` annotés ;
  - `exemples/conventions_alternatives` suit la disposition de Mandalore Express (paquetages au pluriel), avec une
    racine de composition parente des modules.
- **39 ruches non conformes** dans `exemples/violations`, une par cas d'usage. Pour chacune, les tests vérifient trois
  choses :
  - elle enfreint *exactement* les règles attendues : aucune règle muette, aucun faux positif ;
  - le rapport de chaque règle nomme la classe fautive ;
  - chaque règle est enfreinte par au moins un exemple, et chaque exemple du dossier est référencé.
- `ExempleArchitectureRucheTest` exécute l'exemple ci-dessus avec le moteur JUnit d'ArchUnit.

```bash
mvn verify
```

## Choix et limites

- **Pas de règle de cycles entre modules.** Les domaines ne dépendent d'aucun autre module, et les infrastructures
  ne dépendent pas les unes des autres. Le graphe « infra → domaine » est donc acyclique par construction. Deux modules
  peuvent s'appeler mutuellement, comme deux microservices.
- **La configuration d'un module ne référence pas les autres modules.** Les adaptateurs intraprocessus sont découverts
  (component scan), comme dans l'implémentation de référence. Extraire un module revient alors à remplacer
  l'adaptateur par un client, sans toucher au câblage.
- **Le code de test est hors périmètre.** Importez les classes avec `ImportOption.DoNotIncludeTests` : les doublures
  de test implémentent librement les ports.
- **Kotlin** : déclarez `bibliothequesPures("kotlin..")`, car le bytecode Kotlin dépend de `kotlin.jvm.internal`.
- **Cloisonnement des données** : seuls les mappings JPA et Spring Data Relational sont détectés. Les noms sont
  comparés sans casse ni `_`, pour suivre la stratégie de nommage de Spring Boot.

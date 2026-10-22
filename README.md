# Hive ArchUnit

Tests d'architecture [ArchUnit](https://www.archunit.org/) qui vérifient qu'une base de code Java respecte le
**pattern Hive** (Thomas Pierrain et Julien Topçu).

Le Hive découple la stratégie de déploiement (monolithe modulaire ou microservices) de la conception du logiciel :
*« Model once, deploy as you wish »*. Chaque module correspond à un bounded context et est un hexagone (domaine et
infrastructure). Les modules ne communiquent que par ports et adaptateurs, comme le feraient des microservices. Un
module peut ainsi être extrait en service autonome sans réécriture.

Les règles sont déduites des deux implémentations de référence des auteurs :

- [`beyondxscratch/hive-pattern`](https://gitlab.com/beyondxscratch/hive-pattern) (Java, Spring Boot) : machine à
  voyager dans le temps, avec le module `nuclearreactor` extrait en microservice ;
- [`beyondxscratch/mandalore-express-hive-pattern`](https://gitlab.com/beyondxscratch/mandalore-express-hive-pattern)
  (Kotlin) : réservation de trains spatiaux.

## Utilisation

```java
@AnalyzeClasses(packages = "com.acme.shop", importOptions = ImportOption.DoNotIncludeTests.class)
class HiveArchitectureTest {

    static final Hive HIVE = Hive.builder()
            .module("com.acme.shop.ordering")             // modules Hive : un hexagone par bounded context
            .module("com.acme.shop.catalog")
            .sharedKernel("com.acme.shop.money")          // value objects partagés, sans ports ni adaptateurs
            .annotations("com.acme.ddd")                  // @DomainService, @Stub : le domaine ignore Spring
            .technicalModule("com.acme.shop.hypermedia")  // code technique partagé par les adaptateurs
            .compositionRoot("com.acme.shop.application") // assemble les modules en un déployable
            .basePackage("com.acme.shop")                 // tout package non déclaré dessous est une violation
            .domainServiceAnnotation("com.acme.ddd.DomainService")
            .stubAnnotation("com.acme.ddd.Stub")
            .requireFunctionalApiPorts()                  // un port API = une fonctionnalité
            .build();

    @ArchTest
    static final ArchRule hive = HiveRules.of(HIVE).all();
}
```

`HiveRules.of(HIVE).rule(HiveRule.DOMAINS_ARE_ISOLATED)` donne une règle isolée, par exemple pour l'adopter
progressivement avec un [`FreezingArchRule`](https://www.archunit.org/userguide/html/000_Index.html#_freezing_arch_rules).
Chaque violation est préfixée par l'identifiant de la règle enfreinte et situe les deux extrémités de la dépendance
fautive :

```
[DOMAINS_ARE_ISOLATED] module 'catalog' domain -> module 'ordering' API: Class <com.acme.shop.catalog.domain.ProductOrderer>
    implements interface <com.acme.shop.ordering.domain.api.PlaceOrder> in (ProductOrderer.java:0)
```

La bibliothèque n'est pas encore publiée : `mvn install`, puis dépendance de test
`io.github.fanoupro:hive-archunit:0.1.0-SNAPSHOT`.

## Anatomie d'une ruche

```
com.acme.shop
├── ordering                        module Hive (bounded context)
│   ├── domain                      l'hexagone : uniquement du métier
│   │   ├── api                     ports API, les fonctionnalités exposées
│   │   └── spi                     ports SPI, ce que le domaine attend du monde extérieur
│   └── infrastructure              les adaptateurs
│       ├── configuration           câblage du module (ex. son auto-configuration Spring)
│       ├── controller              adaptateurs primaires (driving)
│       ├── inproc                  adaptateurs SPI qui appellent l'API d'un autre module, en mémoire
│       ├── clients                 adaptateurs SPI vers un module extrait (HTTP…)
│       └── persistence             adaptateurs SPI vers la base du module, qu'il est seul à utiliser
├── catalog                         autre module Hive
├── money                           shared kernel
├── hypermedia                      module technique
└── application                     composition root
```

Les noms de packages se configurent avec `HiveConventions`, par exemple pour la disposition de Mandalore Express :

```java
.conventions(HiveConventions.defaults()
        .withConfiguration("infrastructure.configurations")
        .withDrivingAdapters("infrastructure.controllers", "infrastructure.listeners")
        .withInProcessAdapters("infrastructure.clients"))
```

Une composition root peut être le package parent des modules (`com.acme.shop`) : elle ne couvre alors que son propre
package, et non ses sous-packages.

### Qui peut dépendre de qui

| Depuis ↓ / vers → | son domaine | son infra | API et modèle d'un autre module | SPI, services, stubs d'un autre module | infra d'un autre module | shared kernel | annotations | module technique | composition root | libs externes |
|---|---|---|---|---|---|---|---|---|---|---|
| **Domaine** | ✅ | ❌ | ❌ | ❌ | ❌ | ✅ | ✅ | ❌ | ❌ | ❌ ¹ |
| **Infrastructure** | ✅ ² | ✅ | ✅ adaptateurs in-process | ❌ | ❌ | ✅ | ✅ | ✅ | ❌ | ✅ |
| **Shared kernel** | – | – | ❌ | ❌ | ❌ | ✅ | ✅ | ❌ | ❌ | ❌ ¹ |
| **Annotations** | – | – | ❌ | ❌ | ❌ | ❌ | ✅ | ❌ | ❌ | ❌ ¹ |
| **Module technique** | – | – | ❌ | ❌ | ❌ | ✅ | ✅ | ✅ | ❌ | ✅ |
| **Composition root** | – | – | ❌ | ❌ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |

¹ Sauf le JDK et les bibliothèques déclarées pures (`.pureLibraries("kotlin..", "org.jspecify..")`).
² Par les ports API pour les adaptateurs primaires, sans les services du domaine (sauf depuis la configuration), et
sans rappeler sa propre API depuis un adaptateur SPI.

## Catalogue des règles

Chaque règle est prouvée par au moins une fixture non conforme, dans
`src/test/java/io/github/fanoupro/hive/fixtures/violations/`. Les règles marquées † ne s'appliquent que si le Hive
déclare ce qu'elles vérifient.

### Hexagone : chaque module

| Règle | Vérifie | Fixtures |
|---|---|---|
| `DOMAIN_DOES_NOT_DEPEND_ON_ITS_INFRASTRUCTURE` | le domaine ne dépend pas de son infrastructure | `domain_depends_on_its_infrastructure` |
| `DOMAIN_IS_FRAMEWORK_FREE` | le domaine ne dépend que du JDK et des bibliothèques déclarées pures (ni Spring, ni JPA, ni Jackson) | `domain_uses_a_framework` |
| `API_PORTS_ARE_INTERFACES` | les ports API sont des interfaces | `api_port_is_a_class` |
| `SPI_PORTS_ARE_INTERFACES` | les ports SPI sont des interfaces | `spi_port_is_a_class` |
| `API_PORTS_ARE_FUNCTIONAL` † | un port API déclare une seule méthode abstraite | `api_port_exposes_several_features` |
| `API_PORTS_ARE_IMPLEMENTED_BY_THE_DOMAIN` | un port API n'est implémenté que par le domaine de son module | `api_port_implemented_in_infrastructure`, `api_port_implemented_by_another_module` |
| `SPI_PORTS_ARE_IMPLEMENTED_BY_ADAPTERS` | un port SPI n'est implémenté que par l'infrastructure de son module, ou par un stub de son domaine | `spi_port_implemented_in_domain`, `spi_port_implemented_by_another_module` |
| `DRIVING_ADAPTERS_ONLY_USE_API_PORTS` | un adaptateur primaire n'utilise ni les ports SPI ni les adaptateurs secondaires | `driving_adapter_uses_an_spi_port`, `driving_adapter_uses_a_driven_adapter` |
| `INFRASTRUCTURE_USES_DOMAIN_SERVICES_THROUGH_API_PORTS` | hors configuration, l'infrastructure ne dépend pas des services du domaine | `infrastructure_uses_a_domain_service` |
| `DRIVEN_ADAPTERS_DO_NOT_CALL_BACK_INTO_THEIR_DOMAIN` | un adaptateur SPI n'appelle pas l'API de son propre module | `driven_adapter_calls_its_own_api` |

### Ruche : entre les modules

| Règle | Vérifie | Fixtures |
|---|---|---|
| `DOMAINS_ARE_ISOLATED` | le domaine d'un module ne dépend d'aucun autre module | `domain_depends_on_another_module_domain`, `domain_depends_on_another_module_infrastructure` |
| `INFRASTRUCTURES_ARE_ISOLATED` | l'infrastructure d'un module ne dépend pas de celle d'un autre | `infrastructure_depends_on_another_module_infrastructure` |
| `OTHER_MODULES_ARE_REACHED_THROUGH_THEIR_API` | un autre module n'est atteint que par ses ports API et le modèle qu'ils exposent, jamais par ses ports SPI, ses services ou ses stubs | `in_process_adapter_uses_another_module_spi`, `…_domain_service`, `…_stub` |
| `ONLY_IN_PROCESS_ADAPTERS_REACH_OTHER_MODULES` | seuls les adaptateurs in-process dépendent d'un autre module | `driving_adapter_calls_another_module` |
| `IN_PROCESS_ADAPTERS_IMPLEMENT_AN_SPI_PORT` | un adaptateur in-process qui appelle l'API d'un autre module implémente un port SPI de son propre module | `in_process_adapter_without_spi_port` |
| `DATASTORES_ARE_SEGREGATED` | aucune table n'est mappée par deux modules (`@Entity`/`@Table` JPA, `@Table` Spring Data ; schéma pris en compte) | `two_modules_map_the_same_table`, `two_modules_map_the_same_implicit_table` |

### Zones partagées

| Règle | Vérifie | Fixtures |
|---|---|---|
| `SHARED_KERNELS_ARE_PURE` † | un shared kernel ne dépend que du JDK, des bibliothèques pures, d'autres shared kernels et des annotations | `shared_kernel_uses_a_framework`, `shared_kernel_depends_on_a_module` |
| `ANNOTATIONS_ARE_PURE` † | les annotations ne dépendent que du JDK et des bibliothèques pures (pas de `@Component` méta-annoté) | `annotations_depend_on_a_framework` |
| `TECHNICAL_MODULES_ARE_DOMAIN_AGNOSTIC` † | un module technique ne dépend d'aucun module Hive | `technical_module_depends_on_a_module` |
| `DOMAINS_DO_NOT_DEPEND_ON_TECHNICAL_MODULES` † | un domaine ne dépend d'aucun module technique | `domain_depends_on_a_technical_module` |
| `NOTHING_DEPENDS_ON_THE_COMPOSITION_ROOT` † | rien ne dépend d'une composition root | `module_depends_on_the_composition_root` |
| `COMPOSITION_ROOT_DOES_NOT_REACH_INTO_DOMAINS` † | une composition root ne dépend d'aucun domaine : chaque module câble le sien | `composition_root_reaches_into_a_domain` |

### Structure

| Règle | Vérifie | Fixtures |
|---|---|---|
| `EVERY_MODULE_HAS_ITS_OWN_CONFIGURATION` | chaque module a au moins une classe dans son package de configuration | `module_without_configuration` |
| `MODULES_ARE_SPLIT_INTO_DOMAIN_AND_INFRASTRUCTURE` | toute classe d'un module est dans son domaine ou son infrastructure (`package-info` excepté) | `class_outside_domain_and_infrastructure` |
| `EVERY_CLASS_BELONGS_TO_A_DECLARED_ZONE` † | toute classe du package de base appartient à une zone déclarée | `undeclared_module`, `undeclared_module_under_a_composition_root` |

### Annotations

| Règle | Vérifie | Fixtures |
|---|---|---|
| `DOMAIN_SERVICES_ARE_ANNOTATED` † | un service du domaine qui implémente un port API porte l'annotation de service | `domain_service_not_annotated` |
| `DOMAIN_SERVICE_ANNOTATION_IS_RESERVED_TO_DOMAIN_SERVICES` † | l'annotation de service ne s'applique qu'à une classe du domaine qui implémente un de ses ports API | `domain_service_annotation_in_infrastructure`, `domain_service_annotation_without_api_port` |
| `STUBS_ARE_SPI_IMPLEMENTATIONS_IN_THE_DOMAIN` † | l'annotation de stub ne s'applique qu'à une classe du domaine qui implémente un de ses ports SPI | `stub_outside_the_domain`, `stub_without_spi_port` |

## Comment les tests prouvent les règles

- **Deux ruches conformes** respectent chacune des règles applicables :
  - `fixtures/valid` suit la disposition de référence et exerce les 28 règles : adaptateur in-process, client vers un
    module extrait, stub, tables homonymes dans des schémas différents, `package-info` annotés ;
  - `fixtures/alternative_conventions` suit les conventions de Mandalore Express, avec une composition root parente
    des modules.
- **39 ruches non conformes** dans `fixtures/violations`, une par cas d'usage. Pour chacune, les tests vérifient trois
  choses :
  - elle enfreint *exactement* les règles attendues : aucune règle muette, aucun faux positif ;
  - le rapport de chaque règle nomme la classe fautive ;
  - chaque règle est enfreinte par au moins une fixture, et chaque fixture du dossier est référencée.
- `HiveArchitectureExampleTest` exécute l'exemple ci-dessus avec le moteur JUnit d'ArchUnit.

```bash
mvn verify
```

## Choix et limites

- **Pas de règle de cycles entre modules.** Les domaines ne dépendent d'aucun autre module, et les infrastructures
  ne dépendent pas les unes des autres. Le graphe « infra → domaine » est donc acyclique par construction. Deux modules
  peuvent s'appeler mutuellement, comme deux microservices.
- **La configuration d'un module ne référence pas les autres modules.** Les adaptateurs in-process sont découverts
  (component scan), comme dans l'implémentation de référence. Extraire un module revient alors à remplacer
  l'adaptateur par un client, sans toucher au câblage.
- **Le code de test est hors périmètre.** Importez les classes avec `ImportOption.DoNotIncludeTests` : les fakes
  implémentent librement les ports.
- **Kotlin** : déclarez `pureLibraries("kotlin..")`, car le bytecode Kotlin dépend de `kotlin.jvm.internal`.
- **Ségrégation des données** : seuls les mappings JPA et Spring Data Relational sont détectés. Les noms sont comparés
  sans casse ni `_`, pour suivre la stratégie de nommage de Spring Boot.

package io.github.fanoupro.ruche;

import java.util.List;

import static io.github.fanoupro.ruche.Paquetages.appartientA;
import static io.github.fanoupro.ruche.Paquetages.exigerPaquetageValide;
import static java.util.Objects.requireNonNull;

/**
 * Disposition des paquetages de chaque module de la ruche, relativement au paquetage racine du module.
 * <p>
 * Les conventions {@linkplain #parDefaut() par défaut} reprennent la disposition de l'implémentation de référence du
 * pattern Hive, avec des paquetages nommés en français :
 * <pre>
 * commande
 * ├── domaine                 l'hexagone : uniquement du métier
 * │   ├── api                 ports API, les fonctionnalités que le module expose
 * │   └── spi                 ports SPI, ce que le module attend du monde extérieur
 * └── infrastructure          les adaptateurs
 *     ├── configuration       câblage du module (par exemple son auto-configuration Spring)
 *     ├── controleur          adaptateurs primaires
 *     └── intraprocessus      adaptateurs qui appellent en mémoire les ports API d'autres modules
 * </pre>
 * Les conventions de l'{@linkplain #implementationDeReference() implémentation de référence} gardent ses noms de
 * paquetages anglais.
 *
 * @param domaine                   l'hexagone
 * @param api                       ports API, dans le domaine
 * @param spi                       ports SPI, dans le domaine
 * @param infrastructure            les adaptateurs
 * @param configuration             câblage du module, dans l'infrastructure
 * @param adaptateursPrimaires      adaptateurs primaires (côté gauche), comme les contrôleurs REST ou les
 *                                  consommateurs de messages, dans l'infrastructure
 * @param adaptateursIntraprocessus adaptateurs SPI qui appellent en mémoire les ports API d'autres modules, dans
 *                                  l'infrastructure
 */
public record ConventionsRuche(
        String domaine,
        String api,
        String spi,
        String infrastructure,
        String configuration,
        List<String> adaptateursPrimaires,
        List<String> adaptateursIntraprocessus) {

    public ConventionsRuche {
        exigerPaquetageValide(domaine, "paquetage du domaine");
        exigerPaquetageValide(infrastructure, "paquetage de l'infrastructure");
        if (appartientA(domaine, infrastructure) || appartientA(infrastructure, domaine)) {
            throw new IllegalArgumentException("Le domaine et l'infrastructure doivent être des paquetages distincts");
        }
        exigerSousPaquetage(api, domaine, "paquetage des ports API");
        exigerSousPaquetage(spi, domaine, "paquetage des ports SPI");
        if (appartientA(api, spi) || appartientA(spi, api)) {
            throw new IllegalArgumentException("Les ports API et SPI doivent être dans des paquetages distincts");
        }
        exigerSousPaquetage(configuration, infrastructure, "paquetage de configuration");
        adaptateursPrimaires = List.copyOf(requireNonNull(adaptateursPrimaires, "adaptateursPrimaires"));
        adaptateursIntraprocessus = List.copyOf(requireNonNull(adaptateursIntraprocessus, "adaptateursIntraprocessus"));
        adaptateursPrimaires.forEach(adaptateurs ->
                exigerSousPaquetage(adaptateurs, infrastructure, "paquetage d'adaptateurs primaires"));
        adaptateursIntraprocessus.forEach(adaptateurs ->
                exigerSousPaquetage(adaptateurs, infrastructure, "paquetage d'adaptateurs intraprocessus"));
    }

    public static ConventionsRuche parDefaut() {
        return new ConventionsRuche(
                "domaine",
                "domaine.api",
                "domaine.spi",
                "infrastructure",
                "infrastructure.configuration",
                List.of("infrastructure.controleur"),
                List.of("infrastructure.intraprocessus"));
    }

    /**
     * Les conventions de l'implémentation de référence
     * (<a href="https://gitlab.com/beyondxscratch/hive-pattern">gitlab.com/beyondxscratch/hive-pattern</a>), dont
     * les paquetages sont nommés en anglais : {@code domain}, {@code infrastructure.controller},
     * {@code infrastructure.inproc}…
     */
    public static ConventionsRuche implementationDeReference() {
        return new ConventionsRuche(
                "domain",
                "domain.api",
                "domain.spi",
                "infrastructure",
                "infrastructure.configuration",
                List.of("infrastructure.controller"),
                List.of("infrastructure.inproc"));
    }

    public ConventionsRuche avecConfiguration(String configuration) {
        return new ConventionsRuche(domaine, api, spi, infrastructure, configuration, adaptateursPrimaires,
                adaptateursIntraprocessus);
    }

    public ConventionsRuche avecAdaptateursPrimaires(String... adaptateursPrimaires) {
        return new ConventionsRuche(domaine, api, spi, infrastructure, configuration, List.of(adaptateursPrimaires),
                adaptateursIntraprocessus);
    }

    public ConventionsRuche avecAdaptateursIntraprocessus(String... adaptateursIntraprocessus) {
        return new ConventionsRuche(domaine, api, spi, infrastructure, configuration, adaptateursPrimaires,
                List.of(adaptateursIntraprocessus));
    }

    private static void exigerSousPaquetage(String paquetage, String parent, String quoi) {
        exigerPaquetageValide(paquetage, quoi);
        if (!Paquetages.estSousPaquetageDe(paquetage, parent)) {
            throw new IllegalArgumentException(
                    "Le " + quoi + " '" + paquetage + "' doit être un sous-paquetage de '" + parent + "'");
        }
    }
}

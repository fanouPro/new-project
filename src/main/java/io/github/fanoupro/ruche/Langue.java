package io.github.fanoupro.ruche;

import com.tngtech.archunit.ArchConfiguration;

import java.util.Arrays;

import static java.util.Objects.requireNonNull;

/**
 * Langue de la vérification : celle des identifiants des règles, de leurs descriptions, des messages de violation et
 * des {@linkplain ConventionsRuche#pour(Langue) conventions de paquetages} attendues par défaut.
 * <p>
 * Une ruche est vérifiée dans la langue déclarée par {@link Ruche.Monteur#langue(Langue)}, sinon dans celle de la
 * propriété {@value #PROPRIETE} de la configuration ArchUnit, sinon en français. Cette propriété se déclare dans le
 * fichier {@code archunit.properties} des tests ({@code ruche.langue=en}), ou par la propriété système
 * {@code -Darchunit.ruche.langue=en}, qui l'emporte sur le fichier.
 */
public enum Langue {

    FRANCAIS("fr", new TextesFrancais()),
    ANGLAIS("en", new TextesAnglais());

    /**
     * Propriété de la configuration ArchUnit qui choisit la langue : {@code fr} ou {@code en}.
     */
    public static final String PROPRIETE = "ruche.langue";

    private final String code;
    private final Textes textes;

    Langue(String code, Textes textes) {
        this.code = code;
        this.textes = textes;
    }

    /**
     * Le code ISO 639-1 de la langue : {@code fr} ou {@code en}.
     */
    public String code() {
        return code;
    }

    public static Langue deCode(String code) {
        requireNonNull(code, "code");
        return Arrays.stream(values())
                .filter(langue -> langue.code.equalsIgnoreCase(code.strip()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "La langue '" + code + "' n'est pas prise en charge : utilisez fr ou en"));
    }

    /**
     * La langue de la propriété {@value #PROPRIETE} de la configuration ArchUnit, ou le français si elle n'est pas
     * déclarée.
     */
    public static Langue configuree() {
        String code = ArchConfiguration.get().getPropertyOrDefault(PROPRIETE, FRANCAIS.code);
        try {
            return deCode(code);
        } catch (IllegalArgumentException inconnue) {
            throw new IllegalArgumentException(
                    "Configuration ArchUnit " + PROPRIETE + " : " + inconnue.getMessage(), inconnue);
        }
    }

    Textes textes() {
        return textes;
    }
}

package io.github.fanoupro.ruche;

import static io.github.fanoupro.ruche.Paquetages.exigerPaquetageValide;
import static java.util.Objects.requireNonNull;

/**
 * Un module de la ruche : un contexte borné implémenté en hexagone (un domaine et son infrastructure), conçu pour
 * pouvoir être extrait en service autonome.
 *
 * @param nom       le nom utilisé dans les messages de violation
 * @param paquetage le paquetage racine du module, par exemple {@code com.acme.boutique.commande}
 */
public record ModuleRuche(String nom, String paquetage) {

    public ModuleRuche {
        requireNonNull(nom, "nom");
        if (nom.isBlank()) {
            throw new IllegalArgumentException("Un module de la ruche a besoin d'un nom");
        }
        exigerPaquetageValide(paquetage, "paquetage de module");
    }
}

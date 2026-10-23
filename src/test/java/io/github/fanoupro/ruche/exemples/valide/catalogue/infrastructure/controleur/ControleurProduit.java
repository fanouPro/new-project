package io.github.fanoupro.ruche.exemples.valide.catalogue.infrastructure.controleur;

import io.github.fanoupro.ruche.exemples.valide.catalogue.domaine.api.TrouverProduit;
import org.springframework.stereotype.Controller;

import java.util.Optional;

@Controller
public class ControleurProduit {

    private final TrouverProduit trouverProduit;

    public ControleurProduit(TrouverProduit trouverProduit) {
        this.trouverProduit = trouverProduit;
    }

    public Optional<Long> prixEnCentimes(String reference) {
        return trouverProduit.parReference(reference).map(produit -> produit.prix().centimes());
    }
}

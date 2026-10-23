package io.github.fanoupro.ruche.exemples.valide.commande.domaine;

public class ProduitInconnu extends RuntimeException {

    public ProduitInconnu(String reference) {
        super("Produit inconnu " + reference);
    }
}

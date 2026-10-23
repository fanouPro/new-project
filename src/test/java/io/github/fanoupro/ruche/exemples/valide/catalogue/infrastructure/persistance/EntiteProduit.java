package io.github.fanoupro.ruche.exemples.valide.catalogue.infrastructure.persistance;

import io.github.fanoupro.ruche.exemples.valide.catalogue.domaine.Produit;
import io.github.fanoupro.ruche.exemples.valide.monnaie.Montant;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "produit")
public class EntiteProduit {

    @Id
    private String reference;
    private long prixEnCentimes;
    private String devise;

    protected EntiteProduit() {
    }

    public EntiteProduit(Produit produit) {
        this.reference = produit.reference();
        this.prixEnCentimes = produit.prix().centimes();
        this.devise = produit.prix().devise();
    }

    public Produit versDomaine() {
        return new Produit(reference, new Montant(prixEnCentimes, devise));
    }
}

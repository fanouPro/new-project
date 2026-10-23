package io.github.fanoupro.ruche.exemples.valide.commande.infrastructure.persistance;

import io.github.fanoupro.ruche.exemples.valide.commande.domaine.Commande;
import io.github.fanoupro.ruche.exemples.valide.monnaie.Montant;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "commandes")
public class EntiteCommande {

    @Id
    private UUID id;
    private String reference;
    private long totalEnCentimes;
    private String devise;

    protected EntiteCommande() {
    }

    public EntiteCommande(Commande commande) {
        this.id = commande.id();
        this.reference = commande.reference();
        this.totalEnCentimes = commande.total().centimes();
        this.devise = commande.total().devise();
    }

    public Commande versDomaine() {
        return new Commande(id, reference, new Montant(totalEnCentimes, devise));
    }
}

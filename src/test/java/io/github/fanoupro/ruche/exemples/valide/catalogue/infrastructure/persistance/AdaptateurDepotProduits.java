package io.github.fanoupro.ruche.exemples.valide.catalogue.infrastructure.persistance;

import io.github.fanoupro.ruche.exemples.valide.catalogue.domaine.Produit;
import io.github.fanoupro.ruche.exemples.valide.catalogue.domaine.spi.Produits;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AdaptateurDepotProduits implements Produits {

    private final Map<String, EntiteProduit> table = new HashMap<>();

    @Override
    public Optional<Produit> avecReference(String reference) {
        return Optional.ofNullable(table.get(reference)).map(EntiteProduit::versDomaine);
    }
}

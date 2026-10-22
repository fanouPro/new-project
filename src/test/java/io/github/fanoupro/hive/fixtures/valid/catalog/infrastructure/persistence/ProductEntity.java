package io.github.fanoupro.hive.fixtures.valid.catalog.infrastructure.persistence;

import io.github.fanoupro.hive.fixtures.valid.catalog.domain.Product;
import io.github.fanoupro.hive.fixtures.valid.money.Money;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "product")
public class ProductEntity {

    @Id
    private String sku;
    private long priceInCents;
    private String currency;

    protected ProductEntity() {
    }

    public ProductEntity(Product product) {
        this.sku = product.sku();
        this.priceInCents = product.price().cents();
        this.currency = product.price().currency();
    }

    public Product toDomain() {
        return new Product(sku, new Money(priceInCents, currency));
    }
}

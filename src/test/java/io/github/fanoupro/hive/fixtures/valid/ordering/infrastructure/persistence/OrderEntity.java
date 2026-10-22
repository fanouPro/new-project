package io.github.fanoupro.hive.fixtures.valid.ordering.infrastructure.persistence;

import io.github.fanoupro.hive.fixtures.valid.money.Money;
import io.github.fanoupro.hive.fixtures.valid.ordering.domain.Order;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    private UUID id;
    private String sku;
    private long totalInCents;
    private String currency;

    protected OrderEntity() {
    }

    public OrderEntity(Order order) {
        this.id = order.id();
        this.sku = order.sku();
        this.totalInCents = order.total().cents();
        this.currency = order.total().currency();
    }

    public Order toDomain() {
        return new Order(id, sku, new Money(totalInCents, currency));
    }
}

package io.github.fanoupro.hive.fixtures.valid.ordering.infrastructure.controller.resources;

import io.github.fanoupro.hive.fixtures.valid.hypermedia.Link;
import io.github.fanoupro.hive.fixtures.valid.ordering.domain.Order;

import java.net.URI;
import java.util.List;
import java.util.UUID;

public record OrderResource(UUID id, long totalInCents, List<Link> links) {

    public static OrderResource of(Order order) {
        return new OrderResource(order.id(), order.total().cents(),
                List.of(new Link("self", URI.create("/orders/" + order.id()))));
    }
}

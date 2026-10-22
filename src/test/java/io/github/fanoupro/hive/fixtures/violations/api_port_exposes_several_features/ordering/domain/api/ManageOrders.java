package io.github.fanoupro.hive.fixtures.violations.api_port_exposes_several_features.ordering.domain.api;

public interface ManageOrders {
    void place(String sku);

    void cancel(String orderId);
}

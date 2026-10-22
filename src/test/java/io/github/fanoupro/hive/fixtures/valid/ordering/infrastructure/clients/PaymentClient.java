package io.github.fanoupro.hive.fixtures.valid.ordering.infrastructure.clients;

import io.github.fanoupro.hive.fixtures.valid.money.Money;
import io.github.fanoupro.hive.fixtures.valid.ordering.domain.spi.Payments;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.UUID;

public class PaymentClient implements Payments {

    private final HttpClient http = HttpClient.newHttpClient();
    private final URI paymentService;

    public PaymentClient(URI paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public void charge(UUID orderId, Money amount) {
        HttpRequest request = HttpRequest.newBuilder(paymentService.resolve("/payments/" + orderId))
                .POST(HttpRequest.BodyPublishers.ofString(amount.cents() + " " + amount.currency()))
                .build();
        http.sendAsync(request, java.net.http.HttpResponse.BodyHandlers.discarding());
    }
}

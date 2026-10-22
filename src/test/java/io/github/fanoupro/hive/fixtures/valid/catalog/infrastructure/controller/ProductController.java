package io.github.fanoupro.hive.fixtures.valid.catalog.infrastructure.controller;

import io.github.fanoupro.hive.fixtures.valid.catalog.domain.api.FindProduct;
import org.springframework.stereotype.Controller;

import java.util.Optional;

@Controller
public class ProductController {

    private final FindProduct findProduct;

    public ProductController(FindProduct findProduct) {
        this.findProduct = findProduct;
    }

    public Optional<Long> priceInCents(String sku) {
        return findProduct.bySku(sku).map(product -> product.price().cents());
    }
}

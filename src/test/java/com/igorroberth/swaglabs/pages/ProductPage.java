package com.igorroberth.swaglabs.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class ProductPage {

    private final Locator backToProducts;
    private final Locator name;
    private final Locator price;
    private final Locator description;

    public ProductPage(Page page) {
        this.backToProducts = page.getByTestId("back-to-products");
        this.name = page.getByTestId("inventory-item-name");
        this.price = page.getByTestId("inventory-item-price");
        this.description = page.getByTestId("inventory-item-desc");
    }

    /**
     * Prova a navegacao no lugar da URL: nome, preco e descricao reusam os mesmos
     * data-test da listagem, entao nao servem de identidade. Este botao tambem
     * aparece na confirmacao do pedido, mas nenhum fluxo chega la pela listagem.
     */
    public Locator backToProducts() {
        return backToProducts;
    }

    public Locator name() {
        return name;
    }

    public Locator price() {
        return price;
    }

    public Locator description() {
        return description;
    }
}

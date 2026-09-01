package com.igorroberth.swaglabs.components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/** Carrinho do cabecalho, presente em toda pagina apos o login. */
public class CartBadge {

    private final Locator link;
    private final Locator counter;

    public CartBadge(Page page) {
        this.link = page.getByTestId("shopping-cart-link");
        this.counter = page.getByTestId("shopping-cart-badge");
    }

    public void open() {
        link.click();
    }

    /** O badge nao existe no DOM com o carrinho vazio — nao fica com texto zero. */
    public Locator counter() {
        return counter;
    }
}

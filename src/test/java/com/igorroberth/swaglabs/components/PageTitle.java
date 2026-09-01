package com.igorroberth.swaglabs.components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/** Titulo da secao no cabecalho. Presente no inventario, no carrinho e no checkout. */
public class PageTitle {

    private final Locator text;

    public PageTitle(Page page) {
        this.text = page.getByTestId("title");
    }

    public Locator text() {
        return text;
    }
}

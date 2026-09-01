package com.igorroberth.swaglabs.pages;

import com.igorroberth.swaglabs.components.PageTitle;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/** Confirmacao do pedido. */
public class CheckoutCompletePage {

    private final PageTitle pageTitle;
    private final Locator container;
    private final Locator header;
    private final Locator text;

    public CheckoutCompletePage(Page page) {
        this.pageTitle = new PageTitle(page);
        this.container = page.getByTestId("checkout-complete-container");
        this.header = page.getByTestId("complete-header");
        this.text = page.getByTestId("complete-text");
    }

    /** Elemento exclusivo desta pagina — e o que prova a navegacao, no lugar da URL. */
    public Locator container() {
        return container;
    }

    public Locator title() {
        return pageTitle.text();
    }

    public Locator header() {
        return header;
    }

    public Locator text() {
        return text;
    }
}

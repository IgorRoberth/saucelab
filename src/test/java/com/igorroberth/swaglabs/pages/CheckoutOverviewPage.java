package com.igorroberth.swaglabs.pages;

import com.igorroberth.swaglabs.components.PageTitle;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/** Segundo passo do checkout: resumo do pedido e totais. */
public class CheckoutOverviewPage {

    private final Page page;
    private final PageTitle pageTitle;

    private final Locator container;
    private final Locator items;
    private final Locator itemNames;
    private final Locator subtotal;
    private final Locator tax;
    private final Locator total;
    private final Locator finishButton;
    private final Locator cancelButton;

    public CheckoutOverviewPage(Page page) {
        this.page = page;
        this.pageTitle = new PageTitle(page);
        this.container = page.getByTestId("checkout-summary-container");
        this.items = page.getByTestId("inventory-item");
        this.itemNames = page.getByTestId("inventory-item-name");
        this.subtotal = page.getByTestId("subtotal-label");
        this.tax = page.getByTestId("tax-label");
        this.total = page.getByTestId("total-label");
        this.finishButton = page.getByTestId("finish");
        this.cancelButton = page.getByTestId("cancel");
    }

    public CheckoutCompletePage finish() {
        finishButton.click();
        return new CheckoutCompletePage(page);
    }

    public InventoryPage cancel() {
        cancelButton.click();
        return new InventoryPage(page);
    }

    /** Elemento exclusivo desta pagina — e o que prova a navegacao, no lugar da URL. */
    public Locator container() {
        return container;
    }

    public Locator title() {
        return pageTitle.text();
    }

    public Locator items() {
        return items;
    }

    public Locator itemNames() {
        return itemNames;
    }

    public Locator subtotal() {
        return subtotal;
    }

    public Locator tax() {
        return tax;
    }

    public Locator total() {
        return total;
    }
}

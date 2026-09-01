package com.igorroberth.swaglabs.pages;

import com.igorroberth.swaglabs.components.PageTitle;
import com.igorroberth.swaglabs.data.Product;
import io.qameta.allure.Step;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class CartPage {

    private final Page page;
    private final PageTitle pageTitle;

    private final Locator container;
    private final Locator items;
    private final Locator itemNames;
    private final Locator quantities;
    private final Locator continueShoppingButton;
    private final Locator checkoutButton;

    public CartPage(Page page) {
        this.page = page;
        this.pageTitle = new PageTitle(page);
        this.container = page.getByTestId("cart-contents-container");
        this.items = page.getByTestId("inventory-item");
        this.itemNames = page.getByTestId("inventory-item-name");
        this.quantities = page.getByTestId("item-quantity");
        this.continueShoppingButton = page.getByTestId("continue-shopping");
        this.checkoutButton = page.getByTestId("checkout");
    }

    @Step("Remover {0} dentro do carrinho")
    public CartPage removeItem(Product product) {
        itemRow(product).getByRole(AriaRole.BUTTON,
                new Locator.GetByRoleOptions().setName("Remove")).click();
        return this;
    }

    @Step("Iniciar o checkout")
    public CheckoutInformationPage checkout() {
        checkoutButton.click();
        return new CheckoutInformationPage(page);
    }

    @Step("Voltar às compras")
    public InventoryPage continueShopping() {
        continueShoppingButton.click();
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

    public Locator quantities() {
        return quantities;
    }

    // Derivado do locator da lista, declarado no construtor: o nome do produto vem de
    // data/, e o botao e alcancado pelo papel dentro da linha — sem montar seletor aqui.
    private Locator itemRow(Product product) {
        return items.filter(new Locator.FilterOptions().setHasText(product.displayName()));
    }
}

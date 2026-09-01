package com.igorroberth.swaglabs.pages;

import com.igorroberth.swaglabs.components.PageTitle;
import com.igorroberth.swaglabs.components.CartBadge;
import com.igorroberth.swaglabs.components.SidebarMenu;
import com.igorroberth.swaglabs.data.Product;
import com.igorroberth.swaglabs.data.SortOption;
import com.igorroberth.swaglabs.support.TestConfig;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class InventoryPage {

    private static final String PATH = "/inventory.html";

    private final Page page;
    private final PageTitle pageTitle;
    private final SidebarMenu sidebarMenu;
    private final CartBadge cartBadge;

    private final Locator container;
    private final Locator items;
    private final Locator itemNames;
    private final Locator itemPrices;
    private final Locator sortSelect;

    public InventoryPage(Page page) {
        this.page = page;
        this.pageTitle = new PageTitle(page);
        this.sidebarMenu = new SidebarMenu(page);
        this.cartBadge = new CartBadge(page);
        this.container = page.getByTestId("inventory-container");
        this.items = page.getByTestId("inventory-item");
        this.itemNames = page.getByTestId("inventory-item-name");
        this.itemPrices = page.getByTestId("inventory-item-price");
        this.sortSelect = page.getByTestId("product-sort-container");
    }

    /**
     * Navega pela URL, sem passar pelo login. Sem sessao o Swag Labs nao redireciona:
     * mantem a URL do inventario e renderiza a tela de login no lugar (AUTH-006).
     */
    public InventoryPage navigate() {
        page.navigate(TestConfig.baseUrl() + PATH);
        return this;
    }

    public InventoryPage sortBy(SortOption option) {
        sortSelect.selectOption(option.value());
        return this;
    }

    public ProductPage openProduct(Product product) {
        // Derivado do locator da lista, declarado no construtor — o nome do produto vem
        // de data/, nao de uma string de seletor montada aqui.
        itemNames.filter(new Locator.FilterOptions().setHasText(product.displayName())).click();
        return new ProductPage(page);
    }

    public InventoryPage addToCart(Product product) {
        itemButton(product, "Add to cart").click();
        return this;
    }

    public InventoryPage removeFromCart(Product product) {
        itemButton(product, "Remove").click();
        return this;
    }

    public CartPage openCart() {
        cartBadge.open();
        return new CartPage(page);
    }

    public LoginPage logout() {
        sidebarMenu.logout();
        return new LoginPage(page);
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

    public Locator itemPrices() {
        return itemPrices;
    }

    public Locator itemImage(Product product) {
        return itemRow(product).locator("img");
    }

    public Locator cartCounter() {
        return cartBadge.counter();
    }

    // Derivado do locator da lista, declarado no construtor: o nome do produto vem de
    // data/, e o botao e alcancado pelo papel dentro da linha — sem montar seletor aqui.
    private Locator itemButton(Product product, String buttonName) {
        return itemRow(product)
                .getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName(buttonName));
    }

    private Locator itemRow(Product product) {
        return items.filter(new Locator.FilterOptions().setHasText(product.displayName()));
    }
}

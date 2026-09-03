package com.igorroberth.swaglabs.tests;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.igorroberth.swaglabs.data.Catalog;
import com.igorroberth.swaglabs.data.Product;
import com.igorroberth.swaglabs.data.SortOption;
import com.igorroberth.swaglabs.data.User;
import com.igorroberth.swaglabs.pages.InventoryPage;
import com.igorroberth.swaglabs.pages.LoginPage;
import com.igorroberth.swaglabs.pages.ProductPage;
import com.igorroberth.swaglabs.support.BaseTest;
import io.qameta.allure.Allure;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("CAT — Catálogo")
class CatalogTest extends BaseTest {

    @Severity(SeverityLevel.BLOCKER)
    @Test
    @DisplayName("CAT-001: a listagem deve carregar todos os produtos do catálogo")
    void shouldListEveryProduct() {
        LoginPage loginPage = new LoginPage(page).navigate();

        InventoryPage inventoryPage = loginPage.loginAs(User.STANDARD);

        Allure.step("A listagem deve trazer todos os produtos do catálogo", () -> {
            assertThat(inventoryPage.items()).hasCount(Catalog.size());
            assertThat(inventoryPage.itemNames()).hasText(Catalog.namesByNameAscending());
        });
    }

    @Severity(SeverityLevel.MINOR)
    @Test
    @DisplayName("CAT-002: ordenação por nome A→Z deve listar em ordem alfabética crescente")
    void shouldSortByNameAscending() {
        // A listagem ja carrega em A→Z: sem inverter antes, o teste passaria mesmo
        // que o sortBy nao fizesse nada.
        InventoryPage inventoryPage = new LoginPage(page).navigate()
                .loginAs(User.STANDARD)
                .sortBy(SortOption.NAME_DESCENDING);

        inventoryPage.sortBy(SortOption.NAME_ASCENDING);

        Allure.step("A listagem deve estar em ordem alfabética crescente", () ->
                assertThat(inventoryPage.itemNames()).hasText(Catalog.namesByNameAscending()));
    }

    @Severity(SeverityLevel.MINOR)
    @Test
    @DisplayName("CAT-002: ordenação por nome Z→A deve listar em ordem alfabética decrescente")
    void shouldSortByNameDescending() {
        InventoryPage inventoryPage = new LoginPage(page).navigate().loginAs(User.STANDARD);

        inventoryPage.sortBy(SortOption.NAME_DESCENDING);

        Allure.step("A listagem deve estar em ordem alfabética decrescente", () ->
                assertThat(inventoryPage.itemNames()).hasText(Catalog.namesByNameDescending()));
    }

    @Severity(SeverityLevel.NORMAL)
    @Test
    @DisplayName("CAT-003: ordenação por preço deve listar do mais barato ao mais caro")
    void shouldSortByPriceAscending() {
        InventoryPage inventoryPage = new LoginPage(page).navigate().loginAs(User.STANDARD);

        inventoryPage.sortBy(SortOption.PRICE_ASCENDING);

        Allure.step("Os preços devem estar do menor para o maior", () ->
                assertThat(inventoryPage.itemPrices()).hasText(Catalog.pricesByPriceAscending()));
    }

    @Severity(SeverityLevel.NORMAL)
    @Test
    @DisplayName("CAT-003: ordenação por preço deve listar do mais caro ao mais barato")
    void shouldSortByPriceDescending() {
        InventoryPage inventoryPage = new LoginPage(page).navigate().loginAs(User.STANDARD);

        inventoryPage.sortBy(SortOption.PRICE_DESCENDING);

        Allure.step("Os preços devem estar do maior para o menor", () ->
                assertThat(inventoryPage.itemPrices()).hasText(Catalog.pricesByPriceDescending()));
    }

    @Severity(SeverityLevel.NORMAL)
    @Test
    @DisplayName("CAT-004: o detalhe do produto deve exibir nome, preço e descrição do item")
    void shouldOpenProductDetail() {
        InventoryPage inventoryPage = new LoginPage(page).navigate().loginAs(User.STANDARD);

        ProductPage productPage = inventoryPage.openProduct(Product.BACKPACK);

        Allure.step("O detalhe deve exibir nome, preço e descrição do produto", () -> {
            assertThat(productPage.backToProducts()).isVisible();
            assertThat(productPage.name()).hasText(Product.BACKPACK.displayName());
            assertThat(productPage.price()).hasText(Product.BACKPACK.price());
            assertThat(productPage.description()).hasText(Product.BACKPACK.description());
        });
    }
}

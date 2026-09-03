package com.igorroberth.swaglabs.tests;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.igorroberth.swaglabs.data.Catalog;
import com.igorroberth.swaglabs.data.Product;
import com.igorroberth.swaglabs.data.User;
import com.igorroberth.swaglabs.pages.CartPage;
import com.igorroberth.swaglabs.pages.InventoryPage;
import com.igorroberth.swaglabs.pages.LoginPage;
import com.igorroberth.swaglabs.support.BaseTest;
import io.qameta.allure.Allure;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Epic("Compra")
@Feature("Carrinho")
@DisplayName("CART — Carrinho")
class CartTest extends BaseTest {

    @Story("Adicionar item")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    @DisplayName("CART-001: adicionar um item deve marcar o carrinho com uma unidade")
    void shouldAddSingleItem() {
        InventoryPage inventoryPage = new LoginPage(page).navigate().loginAs(User.STANDARD);

        inventoryPage.addToCart(Product.BACKPACK);

        Allure.step("O carrinho deve marcar uma unidade", () ->
                assertThat(inventoryPage.cartCounter()).hasText("1"));
    }

    @Story("Adicionar item")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    @DisplayName("CART-002: adicionar vários itens deve somar a quantidade no carrinho")
    void shouldAddMultipleItems() {
        InventoryPage inventoryPage = new LoginPage(page).navigate().loginAs(User.STANDARD);

        inventoryPage.addToCart(Product.BACKPACK).addToCart(Product.BIKE_LIGHT);

        Allure.step("O carrinho deve marcar duas unidades", () ->
                assertThat(inventoryPage.cartCounter()).hasText("2"));
    }

    @Story("Remover item")
    @Severity(SeverityLevel.NORMAL)
    @Test
    @DisplayName("CART-003: remover item pela listagem deve decrementar o carrinho")
    void shouldRemoveItemFromListing() {
        InventoryPage inventoryPage = new LoginPage(page).navigate()
                .loginAs(User.STANDARD)
                .addToCart(Product.BACKPACK)
                .addToCart(Product.BIKE_LIGHT);

        inventoryPage.removeFromCart(Product.BIKE_LIGHT);

        Allure.step("O carrinho deve voltar para uma unidade", () ->
                assertThat(inventoryPage.cartCounter()).hasText("1"));
    }

    @Story("Remover item")
    @Severity(SeverityLevel.NORMAL)
    @Test
    @DisplayName("CART-004: remover item dentro do carrinho deve retirá-lo da lista")
    void shouldRemoveItemInsideCart() {
        CartPage cartPage = new LoginPage(page).navigate()
                .loginAs(User.STANDARD)
                .addToCart(Product.BACKPACK)
                .addToCart(Product.BIKE_LIGHT)
                .openCart();

        cartPage.removeItem(Product.BIKE_LIGHT);

        Allure.step("O carrinho deve conter apenas a mochila", () ->
                assertThat(cartPage.itemNames()).hasText(Catalog.namesOf(Product.BACKPACK)));
    }

    @Story("Persistência do carrinho")
    @Severity(SeverityLevel.NORMAL)
    @Test
    @DisplayName("CART-005: o conteúdo do carrinho deve sobreviver à navegação entre páginas")
    void shouldKeepCartContentsAcrossNavigation() {
        InventoryPage inventoryPage = new LoginPage(page).navigate()
                .loginAs(User.STANDARD)
                .addToCart(Product.BACKPACK)
                .addToCart(Product.BIKE_LIGHT);

        CartPage cartPage = inventoryPage.openCart().continueShopping().openCart();

        Allure.step("O carrinho deve manter os dois itens após a navegação", () ->
                assertThat(cartPage.itemNames())
                        .hasText(Catalog.namesOf(Product.BACKPACK, Product.BIKE_LIGHT)));
    }
}

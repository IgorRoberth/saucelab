package com.igorroberth.swaglabs.tests;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.igorroberth.swaglabs.data.Catalog;
import com.igorroberth.swaglabs.data.Customer;
import com.igorroberth.swaglabs.data.DegradedState;
import com.igorroberth.swaglabs.data.ErrorMessages;
import com.igorroberth.swaglabs.data.Product;
import com.igorroberth.swaglabs.data.SortOption;
import com.igorroberth.swaglabs.data.User;
import com.igorroberth.swaglabs.pages.CheckoutInformationPage;
import com.igorroberth.swaglabs.pages.InventoryPage;
import com.igorroberth.swaglabs.pages.LoginPage;
import com.igorroberth.swaglabs.support.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Cenarios sob usuarios degradados. As asserções fixam o defeito observado em vez de
 * confirmarem o caminho feliz: se a aplicacao for corrigida, estes testes quebram —
 * que e exatamente o sinal desejado.
 */
@DisplayName("STATE — Estados degradados")
class DegradedStateTest extends BaseTest {

    @Test
    @DisplayName("STATE-001: sob problem_user o catálogo exibe imagem genérica no lugar da foto")
    void shouldDetectPlaceholderImagesUnderProblemUser() {
        LoginPage loginPage = new LoginPage(page).navigate();

        InventoryPage inventoryPage = loginPage.loginAs(User.PROBLEM);

        assertThat(inventoryPage.itemImage(Product.BACKPACK))
                .hasAttribute("src", DegradedState.PLACEHOLDER_IMAGE);
    }

    @Test
    @DisplayName("STATE-001: sob problem_user a ordenação do catálogo não reordena a listagem")
    void shouldDetectSortingDefectUnderProblemUser() {
        InventoryPage inventoryPage = new LoginPage(page).navigate().loginAs(User.PROBLEM);

        inventoryPage.sortBy(SortOption.NAME_DESCENDING);

        assertThat(inventoryPage.itemNames()).hasText(Catalog.namesByNameAscending());
    }

    @Test
    @DisplayName("STATE-002: sob problem_user o checkout rejeita um formulário preenchido")
    void shouldDetectCheckoutDefectUnderProblemUser() {
        CheckoutInformationPage informationPage = new LoginPage(page).navigate()
                .loginAs(User.PROBLEM)
                .addToCart(Product.BACKPACK)
                .openCart()
                .checkout();

        informationPage.continueExpectingFailure(Customer.COMPLETE);

        assertThat(informationPage.errorMessage()).hasText(ErrorMessages.LAST_NAME_REQUIRED);
    }

    @Test
    @DisplayName("STATE-003: login sob performance_glitch_user deve concluir dentro do timeout")
    void shouldSignInPerformanceGlitchUserWithinTimeout() {
        LoginPage loginPage = new LoginPage(page).navigate();

        InventoryPage inventoryPage = loginPage.loginAs(User.PERFORMANCE_GLITCH);

        assertThat(inventoryPage.container()).isVisible();
    }
}

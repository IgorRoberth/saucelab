package com.igorroberth.swaglabs.tests;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.igorroberth.swaglabs.data.Customer;
import com.igorroberth.swaglabs.data.ErrorMessages;
import com.igorroberth.swaglabs.data.OrderMessages;
import com.igorroberth.swaglabs.data.OrderTotals;
import com.igorroberth.swaglabs.data.Product;
import com.igorroberth.swaglabs.data.User;
import com.igorroberth.swaglabs.pages.CheckoutCompletePage;
import com.igorroberth.swaglabs.pages.CheckoutInformationPage;
import com.igorroberth.swaglabs.pages.CheckoutOverviewPage;
import com.igorroberth.swaglabs.pages.InventoryPage;
import com.igorroberth.swaglabs.pages.LoginPage;
import com.igorroberth.swaglabs.support.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("CKO — Checkout")
class CheckoutTest extends BaseTest {

    @Test
    @DisplayName("CKO-001: o fluxo completo deve terminar na confirmação do pedido")
    void shouldCompleteCheckout() {
        CheckoutOverviewPage overviewPage = signInWithReferenceBasket()
                .openCart()
                .checkout()
                .continueAs(Customer.COMPLETE);

        CheckoutCompletePage completePage = overviewPage.finish();

        assertThat(completePage.header()).hasText(OrderMessages.CONFIRMATION_HEADER);
        assertThat(completePage.text()).hasText(OrderMessages.CONFIRMATION_TEXT);
    }

    @Test
    @DisplayName("CKO-002: formulário vazio deve exigir o primeiro nome")
    void shouldRequireFirstName() {
        CheckoutInformationPage informationPage = openCheckoutInformation();

        informationPage.continueExpectingFailure(Customer.EMPTY);

        assertThat(informationPage.errorMessage()).hasText(ErrorMessages.FIRST_NAME_REQUIRED);
    }

    @Test
    @DisplayName("CKO-002: formulário sem sobrenome deve exigir o sobrenome")
    void shouldRequireLastName() {
        CheckoutInformationPage informationPage = openCheckoutInformation();

        informationPage.continueExpectingFailure(Customer.WITHOUT_LAST_NAME);

        assertThat(informationPage.errorMessage()).hasText(ErrorMessages.LAST_NAME_REQUIRED);
    }

    @Test
    @DisplayName("CKO-002: formulário sem CEP deve exigir o código postal")
    void shouldRequirePostalCode() {
        CheckoutInformationPage informationPage = openCheckoutInformation();

        informationPage.continueExpectingFailure(Customer.WITHOUT_POSTAL_CODE);

        assertThat(informationPage.errorMessage()).hasText(ErrorMessages.POSTAL_CODE_REQUIRED);
    }

    @Test
    @DisplayName("CKO-003: o resumo deve somar subtotal, imposto e total da cesta")
    void shouldCalculateOrderTotals() {
        CheckoutInformationPage informationPage = openCheckoutInformation();

        CheckoutOverviewPage overviewPage = informationPage.continueAs(Customer.COMPLETE);

        assertThat(overviewPage.subtotal()).hasText(OrderTotals.SUBTOTAL);
        assertThat(overviewPage.tax()).hasText(OrderTotals.TAX);
        assertThat(overviewPage.total()).hasText(OrderTotals.TOTAL);
    }

    @Test
    @DisplayName("CKO-004: cancelar no resumo deve voltar sem pedido e manter o carrinho")
    void shouldCancelWithoutLosingCart() {
        CheckoutOverviewPage overviewPage = openCheckoutInformation().continueAs(Customer.COMPLETE);

        InventoryPage inventoryPage = overviewPage.cancel();

        assertThat(inventoryPage.container()).isVisible();
        assertThat(inventoryPage.cartCounter()).hasText("2");
    }

    @Test
    @DisplayName("CKO-005: checkout com carrinho vazio conclui o pedido — comportamento observado")
    void shouldDocumentCheckoutWithEmptyCart() {
        CheckoutOverviewPage overviewPage = new LoginPage(page).navigate()
                .loginAs(User.STANDARD)
                .openCart()
                .checkout()
                .continueAs(Customer.COMPLETE);

        CheckoutCompletePage completePage = overviewPage.finish();

        assertThat(completePage.header()).hasText(OrderMessages.CONFIRMATION_HEADER);
    }

    private InventoryPage signInWithReferenceBasket() {
        return new LoginPage(page).navigate()
                .loginAs(User.STANDARD)
                .addToCart(Product.BACKPACK)
                .addToCart(Product.BIKE_LIGHT);
    }

    private CheckoutInformationPage openCheckoutInformation() {
        return signInWithReferenceBasket().openCart().checkout();
    }
}

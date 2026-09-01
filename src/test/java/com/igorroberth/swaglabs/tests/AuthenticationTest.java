package com.igorroberth.swaglabs.tests;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.igorroberth.swaglabs.data.ErrorMessages;
import com.igorroberth.swaglabs.data.User;
import com.igorroberth.swaglabs.pages.InventoryPage;
import com.igorroberth.swaglabs.pages.LoginPage;
import com.igorroberth.swaglabs.support.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("AUTH — Autenticação")
class AuthenticationTest extends BaseTest {

    @Test
    @DisplayName("AUTH-001: usuário válido deve acessar o inventário")
    void shouldSignInStandardUser() {
        LoginPage loginPage = new LoginPage(page).navigate();

        InventoryPage inventoryPage = loginPage.loginAs(User.STANDARD);

        assertThat(inventoryPage.container()).isVisible();
    }

    @Test
    @DisplayName("AUTH-002: senha incorreta deve manter o usuário na tela de login")
    void shouldRejectInvalidPassword() {
        LoginPage loginPage = new LoginPage(page).navigate();

        loginPage.loginExpectingFailure(User.STANDARD_WITH_WRONG_PASSWORD);

        assertThat(loginPage.errorMessage()).hasText(ErrorMessages.INVALID_CREDENTIALS);
    }

    @Test
    @DisplayName("AUTH-003: envio com campos vazios deve exigir o usuário")
    void shouldRequireUsername() {
        LoginPage loginPage = new LoginPage(page).navigate();

        loginPage.submitWithoutCredentials();

        assertThat(loginPage.errorMessage()).hasText(ErrorMessages.USERNAME_REQUIRED);
    }

    @Test
    @DisplayName("AUTH-004: usuário bloqueado não deve acessar o inventário")
    void shouldBlockLockedOutUser() {
        LoginPage loginPage = new LoginPage(page).navigate();

        loginPage.loginExpectingFailure(User.LOCKED_OUT);

        assertThat(loginPage.errorMessage()).hasText(ErrorMessages.LOCKED_OUT);
    }

    @Test
    @DisplayName("AUTH-005: logout pelo menu lateral deve devolver o usuário ao login")
    void shouldSignOutThroughSidebarMenu() {
        InventoryPage inventoryPage = new LoginPage(page).navigate().loginAs(User.STANDARD);

        LoginPage loginPage = inventoryPage.logout();

        assertThat(loginPage.container()).isVisible();
    }

    @Test
    @DisplayName("AUTH-006: acesso direto ao inventário sem sessão deve ser bloqueado")
    void shouldBlockDirectInventoryAccessWithoutSession() {
        LoginPage loginPage = new LoginPage(page);

        new InventoryPage(page).navigate();

        assertThat(loginPage.errorMessage()).hasText(ErrorMessages.INVENTORY_REQUIRES_SESSION);
    }
}

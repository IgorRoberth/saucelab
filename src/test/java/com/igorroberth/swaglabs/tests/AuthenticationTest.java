package com.igorroberth.swaglabs.tests;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.igorroberth.swaglabs.data.ErrorMessages;
import com.igorroberth.swaglabs.data.User;
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

@Epic("Acesso")
@Feature("Autenticação")
@DisplayName("AUTH — Autenticação")
class AuthenticationTest extends BaseTest {

    @Story("Login")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    @DisplayName("AUTH-001: usuário válido deve acessar o inventário")
    void shouldSignInStandardUser() {
        LoginPage loginPage = new LoginPage(page).navigate();

        InventoryPage inventoryPage = loginPage.loginAs(User.STANDARD);

        Allure.step("O inventário deve estar visível", () ->
                assertThat(inventoryPage.container()).isVisible());
    }

    @Story("Credenciais inválidas")
    @Severity(SeverityLevel.NORMAL)
    @Test
    @DisplayName("AUTH-002: senha incorreta deve manter o usuário na tela de login")
    void shouldRejectInvalidPassword() {
        LoginPage loginPage = new LoginPage(page).navigate();

        loginPage.loginExpectingFailure(User.STANDARD_WITH_WRONG_PASSWORD);

        Allure.step("A tela de login deve acusar credencial inválida", () ->
                assertThat(loginPage.errorMessage()).hasText(ErrorMessages.INVALID_CREDENTIALS));
    }

    @Story("Credenciais inválidas")
    @Severity(SeverityLevel.NORMAL)
    @Test
    @DisplayName("AUTH-003: envio com campos vazios deve exigir o usuário")
    void shouldRequireUsername() {
        LoginPage loginPage = new LoginPage(page).navigate();

        loginPage.submitWithoutCredentials();

        Allure.step("A tela de login deve exigir o usuário", () ->
                assertThat(loginPage.errorMessage()).hasText(ErrorMessages.USERNAME_REQUIRED));
    }

    @Story("Usuário bloqueado")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    @DisplayName("AUTH-004: usuário bloqueado não deve acessar o inventário")
    void shouldBlockLockedOutUser() {
        LoginPage loginPage = new LoginPage(page).navigate();

        loginPage.loginExpectingFailure(User.LOCKED_OUT);

        Allure.step("A tela de login deve informar o bloqueio do usuário", () ->
                assertThat(loginPage.errorMessage()).hasText(ErrorMessages.LOCKED_OUT));
    }

    @Story("Logout")
    @Severity(SeverityLevel.NORMAL)
    @Test
    @DisplayName("AUTH-005: logout pelo menu lateral deve devolver o usuário ao login")
    void shouldSignOutThroughSidebarMenu() {
        InventoryPage inventoryPage = new LoginPage(page).navigate().loginAs(User.STANDARD);

        LoginPage loginPage = inventoryPage.logout();

        Allure.step("O usuário deve estar de volta na tela de login", () ->
                assertThat(loginPage.container()).isVisible());
    }

    @Story("Proteção de sessão")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    @DisplayName("AUTH-006: acesso direto ao inventário sem sessão deve ser bloqueado")
    void shouldBlockDirectInventoryAccessWithoutSession() {
        LoginPage loginPage = new LoginPage(page);

        new InventoryPage(page).navigate();

        Allure.step("O acesso deve ser recusado por falta de sessão", () ->
                assertThat(loginPage.errorMessage()).hasText(ErrorMessages.INVENTORY_REQUIRES_SESSION));
    }
}

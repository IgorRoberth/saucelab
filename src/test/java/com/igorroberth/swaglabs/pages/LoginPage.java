package com.igorroberth.swaglabs.pages;

import com.igorroberth.swaglabs.data.User;
import com.igorroberth.swaglabs.support.TestConfig;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class LoginPage {

    private final Page page;

    private final Locator container;
    private final Locator usernameField;
    private final Locator passwordField;
    private final Locator submitButton;
    private final Locator errorMessage;

    public LoginPage(Page page) {
        this.page = page;
        this.container = page.getByTestId("login-container");
        this.usernameField = page.getByTestId("username");
        this.passwordField = page.getByTestId("password");
        this.submitButton = page.getByTestId("login-button");
        this.errorMessage = page.getByTestId("error");
    }

    public LoginPage navigate() {
        page.navigate(TestConfig.baseUrl());
        return this;
    }

    public InventoryPage loginAs(User user) {
        fillCredentials(user);
        submitButton.click();
        return new InventoryPage(page);
    }

    public LoginPage loginExpectingFailure(User user) {
        fillCredentials(user);
        submitButton.click();
        return this;
    }

    public LoginPage submitWithoutCredentials() {
        submitButton.click();
        return this;
    }

    /** Elemento exclusivo desta pagina — e o que prova o retorno ao login, no lugar da URL. */
    public Locator container() {
        return container;
    }

    public Locator errorMessage() {
        return errorMessage;
    }

    private void fillCredentials(User user) {
        usernameField.fill(user.username());
        passwordField.fill(user.password());
    }
}

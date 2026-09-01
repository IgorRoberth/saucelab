package com.igorroberth.swaglabs.pages;

import com.igorroberth.swaglabs.components.PageTitle;
import com.igorroberth.swaglabs.data.Customer;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/** Primeiro passo do checkout: dados do comprador. */
public class CheckoutInformationPage {

    private final Page page;
    private final PageTitle pageTitle;

    private final Locator container;
    private final Locator firstNameField;
    private final Locator lastNameField;
    private final Locator postalCodeField;
    private final Locator continueButton;
    private final Locator cancelButton;
    private final Locator errorMessage;

    public CheckoutInformationPage(Page page) {
        this.page = page;
        this.pageTitle = new PageTitle(page);
        this.container = page.getByTestId("checkout-info-container");
        this.firstNameField = page.getByTestId("firstName");
        this.lastNameField = page.getByTestId("lastName");
        this.postalCodeField = page.getByTestId("postalCode");
        this.continueButton = page.getByTestId("continue");
        this.cancelButton = page.getByTestId("cancel");
        this.errorMessage = page.getByTestId("error");
    }

    public CheckoutOverviewPage continueAs(Customer customer) {
        fillCustomer(customer);
        continueButton.click();
        return new CheckoutOverviewPage(page);
    }

    public CheckoutInformationPage continueExpectingFailure(Customer customer) {
        fillCustomer(customer);
        continueButton.click();
        return this;
    }

    public CartPage cancel() {
        cancelButton.click();
        return new CartPage(page);
    }

    /** Elemento exclusivo desta pagina — e o que prova a navegacao, no lugar da URL. */
    public Locator container() {
        return container;
    }

    public Locator title() {
        return pageTitle.text();
    }

    public Locator errorMessage() {
        return errorMessage;
    }

    private void fillCustomer(Customer customer) {
        firstNameField.fill(customer.firstName());
        lastNameField.fill(customer.lastName());
        postalCodeField.fill(customer.postalCode());
    }
}

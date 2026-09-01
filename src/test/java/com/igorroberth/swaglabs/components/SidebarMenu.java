package com.igorroberth.swaglabs.components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

/** Menu lateral do cabecalho, presente em toda pagina apos o login. */
public class SidebarMenu {

    private final Locator openButton;
    private final Locator logoutLink;

    public SidebarMenu(Page page) {
        // O data-test="open-menu" esta no <img>, que nao recebe o clique: o
        // <button id="react-burger-menu-btn"> fica por cima e intercepta o evento.
        // Por isso o getByRole aqui, e nao o getByTestId da ordem de preferencia.
        this.openButton = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Open Menu"));
        this.logoutLink = page.getByTestId("logout-sidebar-link");
    }

    public void logout() {
        openButton.click();
        logoutLink.click();
    }
}

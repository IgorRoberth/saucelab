package com.igorroberth.swaglabs.support;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.options.ScreenshotType;
import io.qameta.allure.Allure;
import io.qameta.allure.listener.StepLifecycleListener;
import io.qameta.allure.model.StepResult;
import java.io.ByteArrayInputStream;

/**
 * Anexa um screenshot ao fim de cada passo do Allure, para que o relatorio mostre
 * a sequencia do fluxo e nao so o estado final. Registrado por SPI em
 * META-INF/services, e nao por chamada dentro do teste ou da Page Object.
 *
 * JPEG a 60%: com oito a dez passos por teste vezes 28 testes vezes tres browsers,
 * PNG cheio produziria dezenas de MB por execucao e um relatorio lento de abrir.
 */
public class StepScreenshot implements StepLifecycleListener {

    private static final int QUALITY = 60;

    private static final ThreadLocal<Page> CURRENT_PAGE = new ThreadLocal<>();

    static void watch(Page page) {
        CURRENT_PAGE.set(page);
    }

    static void stopWatching() {
        CURRENT_PAGE.remove();
    }

    @Override
    public void beforeStepStop(StepResult result) {
        Page page = CURRENT_PAGE.get();
        if (page == null) {
            return;
        }
        capture(page);
    }

    private static void capture(Page page) {
        try {
            byte[] image = page.screenshot(new Page.ScreenshotOptions()
                    .setType(ScreenshotType.JPEG)
                    .setQuality(QUALITY));
            Allure.getLifecycle().addAttachment("passo", "image/jpeg", "jpg",
                    new ByteArrayInputStream(image));
        } catch (PlaywrightException e) {
            // Evidencia nunca derruba teste. Um passo pode terminar com a pagina em
            // navegacao ou ja fechada, e ai o screenshot falha sem que nada esteja errado.
            Allure.getLifecycle().updateStep(step ->
                    step.setDescription("Screenshot indisponivel: " + e.getMessage()));
        }
    }
}

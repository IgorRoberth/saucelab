package com.igorroberth.swaglabs.support;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;
import io.qameta.allure.Allure;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Ciclo de vida do Playwright. Playwright e Browser sobem uma vez por classe;
 * o BrowserContext e novo a cada teste, que e o que garante isolamento de sessao.
 */
@ExtendWith(FailureDetector.class)
public abstract class BaseTest {

    // Diretorio de gravacao bruta do Playwright. A evidencia final e copiada dali
    // para a pasta do teste; o arquivo original e descartado no fim de cada teste.
    private static final Path VIDEO_DIRECTORY = Path.of("target", "videos");

    private static Playwright playwright;
    private static Browser browser;

    protected Page page;

    private BrowserContext context;
    private Evidence evidence;
    private boolean failed;

    @BeforeAll
    protected static void launchBrowser() {
        playwright = Playwright.create();
        // O Swag Labs marca os elementos com data-test, e nao com o data-testid que o
        // Playwright procura por padrao. Sem esta linha todo getByTestId da suite falha.
        playwright.selectors().setTestIdAttribute("data-test");
        browser = browserType().launch(
                new BrowserType.LaunchOptions().setHeadless(!TestConfig.headed()));
    }

    @AfterAll
    protected static void closeBrowser() {
        browser.close();
        playwright.close();
    }

    @BeforeEach
    protected void openContext(TestInfo testInfo) {
        String testName = testInfo.getTestMethod().map(Method::getName).orElse("unknown");
        evidence = new Evidence(testInfo.getDisplayName(), testName);
        // Sem este parametro o historyId dos tres browsers e identico, e o relatorio
        // agregado do CI colapsa as tres execucoes do mesmo caso numa so, como retry.
        Allure.parameter("browser", TestConfig.browser());
        context = browser.newContext(
                new Browser.NewContextOptions().setRecordVideoDir(VIDEO_DIRECTORY));
        context.setDefaultTimeout(TestConfig.timeoutMs());
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true));
        page = context.newPage();
    }

    @AfterEach
    protected void closeContext() {
        saveScreenshot();
        stopTracing();
        var video = page.video();
        context.close();
        handleVideo(video);
    }

    void markFailed(boolean testFailed) {
        this.failed = testFailed;
    }

    private static BrowserType browserType() {
        return switch (TestConfig.browser()) {
            case "chromium" -> playwright.chromium();
            case "firefox" -> playwright.firefox();
            case "webkit" -> playwright.webkit();
            default -> throw new IllegalArgumentException(
                    "Unsupported browser: " + TestConfig.browser()
                            + ". Use chromium, firefox or webkit.");
        };
    }

    private void saveScreenshot() {
        Path screenshot = evidence.file("screenshot", ".png");
        page.screenshot(new Page.ScreenshotOptions().setPath(screenshot).setFullPage(true));
        attach("screenshot", "image/png", screenshot, ".png");
    }

    private void stopTracing() {
        Path trace = evidence.file("trace", ".zip");
        // Path nulo faz o Playwright descartar o trace em vez de grava-lo.
        context.tracing().stop(new Tracing.StopOptions().setPath(failed ? trace : null));
        if (failed) {
            attach("trace", "application/zip", trace, ".zip");
        }
    }

    private void handleVideo(com.microsoft.playwright.Video video) {
        if (video == null) {
            return;
        }
        if (failed) {
            Path saved = evidence.file("video", ".webm");
            video.saveAs(saved);
            attach("video", "video/webm", saved, ".webm");
        }
        video.delete();
    }

    private void attach(String name, String contentType, Path file, String extension) {
        try {
            Allure.addAttachment(name, contentType, Files.newInputStream(file), extension);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to attach " + name + " from " + file, e);
        }
    }
}

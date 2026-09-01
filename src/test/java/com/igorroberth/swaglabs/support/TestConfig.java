package com.igorroberth.swaglabs.support;

import java.util.Locale;

/**
 * Resolve a configuracao de execucao. A propriedade de sistema vence a variavel
 * de ambiente para que `mvn test -D browser=firefox` sobreponha o que o CI define.
 */
public final class TestConfig {

    private static final String DEFAULT_BASE_URL = "https://www.saucedemo.com";
    private static final String DEFAULT_BROWSER = "chromium";
    private static final String DEFAULT_TIMEOUT_MS = "30000";

    private TestConfig() {
    }

    public static String baseUrl() {
        return resolve("baseUrl", "BASE_URL", DEFAULT_BASE_URL);
    }

    public static String browser() {
        return resolve("browser", "BROWSER", DEFAULT_BROWSER).toLowerCase(Locale.ROOT);
    }

    public static boolean headed() {
        return Boolean.parseBoolean(resolve("headed", "HEADED", "false"));
    }

    public static double timeoutMs() {
        return Double.parseDouble(resolve("timeoutMs", "TIMEOUT_MS", DEFAULT_TIMEOUT_MS));
    }

    private static String resolve(String property, String environmentVariable, String fallback) {
        String value = System.getProperty(property);
        if (value == null || value.isBlank()) {
            value = System.getenv(environmentVariable);
        }
        return value == null || value.isBlank() ? fallback : value;
    }
}

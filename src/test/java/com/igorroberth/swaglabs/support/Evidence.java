package com.igorroberth.swaglabs.support;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Pasta de evidencia de um teste. O nome comeca pelo identificador do catalogo
 * ("AUTH-001"), extraido do @DisplayName: e o que deixa as pastas na ordem do
 * SPEC quando o diretorio e listado, sem depender da ordem de execucao.
 */
final class Evidence {

    private static final Path ROOT = Path.of("target", "evidencias");
    private static final Pattern CATALOG_ID = Pattern.compile("^([A-Z]+-\\d+):.*");

    private final Path directory;

    Evidence(String displayName, String methodName) {
        this.directory = ROOT.resolve(catalogId(displayName) + methodName);
    }

    /** Caminho dentro da pasta do teste, sufixado pelo browser da execucao. */
    Path file(String name, String extension) {
        return createDirectory().resolve(name + "-" + TestConfig.browser() + extension);
    }

    private Path createDirectory() {
        try {
            return Files.createDirectories(directory);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to create evidence directory " + directory, e);
        }
    }

    private static String catalogId(String displayName) {
        Matcher matcher = CATALOG_ID.matcher(displayName);
        return matcher.matches() ? matcher.group(1) + "-" : "";
    }
}

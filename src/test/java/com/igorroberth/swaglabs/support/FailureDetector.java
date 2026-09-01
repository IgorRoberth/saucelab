package com.igorroberth.swaglabs.support;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * O @AfterEach precisa saber se o teste falhou para decidir entre salvar e descartar
 * trace e video. O JUnit so expoe a excecao neste callback, que roda antes do @AfterEach;
 * o TestWatcher rodaria depois, com o contexto do browser ja fechado.
 */
public class FailureDetector implements AfterTestExecutionCallback {

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) {
        if (extensionContext.getRequiredTestInstance() instanceof BaseTest baseTest) {
            baseTest.markFailed(extensionContext.getExecutionException().isPresent());
        }
    }
}

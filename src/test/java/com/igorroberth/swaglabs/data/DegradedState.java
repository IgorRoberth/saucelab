package com.igorroberth.swaglabs.data;

import java.util.regex.Pattern;

/** Valores observados nos estados degradados que a aplicacao expoe de proposito. */
public final class DegradedState {

    /**
     * O problem_user troca a foto de todo produto por uma imagem generica de erro.
     * O nome do arquivo carrega um hash de build, entao a expectativa e o trecho estavel.
     */
    public static final Pattern PLACEHOLDER_IMAGE = Pattern.compile("sl-404");

    private DegradedState() {
    }
}

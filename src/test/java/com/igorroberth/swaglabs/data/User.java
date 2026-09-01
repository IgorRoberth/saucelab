package com.igorroberth.swaglabs.data;

/**
 * Massa de usuarios do Swag Labs. A lista e a senha sao publicadas pela propria
 * tela de login da aplicacao — nao sao segredo e nao vivem em variavel de ambiente.
 */
public enum User {

    STANDARD("standard_user"),
    LOCKED_OUT("locked_out_user"),
    PROBLEM("problem_user"),
    PERFORMANCE_GLITCH("performance_glitch_user"),
    ERROR("error_user"),
    VISUAL("visual_user"),

    // Existe so para o cenario de credencial invalida (AUTH-002).
    STANDARD_WITH_WRONG_PASSWORD("standard_user", "wrong_password");

    private static final String SHARED_PASSWORD = "secret_sauce";

    private final String username;
    private final String password;

    User(String username) {
        this(username, SHARED_PASSWORD);
    }

    User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }
}

package com.igorroberth.swaglabs.data;

/** Mensagens exatas que o Swag Labs exibe na tela de login. */
public final class ErrorMessages {

    public static final String USERNAME_REQUIRED =
            "Epic sadface: Username is required";

    public static final String PASSWORD_REQUIRED =
            "Epic sadface: Password is required";

    public static final String INVALID_CREDENTIALS =
            "Epic sadface: Username and password do not match any user in this service";

    public static final String LOCKED_OUT =
            "Epic sadface: Sorry, this user has been locked out.";

    public static final String INVENTORY_REQUIRES_SESSION =
            "Epic sadface: You can only access '/inventory.html' when you are logged in.";

    public static final String FIRST_NAME_REQUIRED =
            "Error: First Name is required";

    public static final String LAST_NAME_REQUIRED =
            "Error: Last Name is required";

    public static final String POSTAL_CODE_REQUIRED =
            "Error: Postal Code is required";

    private ErrorMessages() {
    }
}

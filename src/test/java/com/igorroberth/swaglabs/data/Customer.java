package com.igorroberth.swaglabs.data;

/** Dados do formulario de checkout. Variantes incompletas cobrem o CKO-002. */
public record Customer(String firstName, String lastName, String postalCode) {

    public static final Customer COMPLETE = new Customer("Igor", "Roberth", "30110-000");

    public static final Customer EMPTY = new Customer("", "", "");

    public static final Customer WITHOUT_LAST_NAME = new Customer("Igor", "", "");

    public static final Customer WITHOUT_POSTAL_CODE = new Customer("Igor", "Roberth", "");
}

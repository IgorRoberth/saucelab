package com.igorroberth.swaglabs.data;

/** Opcoes do seletor de ordenacao do inventario, pelo value do <option>. */
public enum SortOption {

    NAME_ASCENDING("az"),
    NAME_DESCENDING("za"),
    PRICE_ASCENDING("lohi"),
    PRICE_DESCENDING("hilo");

    private final String value;

    SortOption(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}

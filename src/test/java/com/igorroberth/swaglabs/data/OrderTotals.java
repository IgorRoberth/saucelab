package com.igorroberth.swaglabs.data;

/**
 * Valores esperados do resumo do pedido para a cesta de referencia do CKO-003:
 * Sauce Labs Backpack ($29.99) + Sauce Labs Bike Light ($9.99).
 * Sao declarados, e nao calculados: um teste que refizesse a conta do imposto
 * passaria mesmo com a aplicacao somando errado.
 */
public final class OrderTotals {

    public static final String SUBTOTAL = "Item total: $39.98";
    public static final String TAX = "Tax: $3.20";
    public static final String TOTAL = "Total: $43.18";

    private OrderTotals() {
    }
}

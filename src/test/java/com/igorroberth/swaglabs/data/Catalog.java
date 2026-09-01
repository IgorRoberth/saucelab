package com.igorroberth.swaglabs.data;

import java.util.Arrays;

/**
 * Ordem esperada do catalogo em cada criterio de ordenacao. A expectativa e declarada
 * de forma independente da aplicacao: um teste que derivasse a ordem do proprio DOM
 * passaria mesmo com a ordenacao quebrada.
 */
public final class Catalog {

    private static final Product[] BY_NAME_ASCENDING = {
            Product.BACKPACK, Product.BIKE_LIGHT, Product.BOLT_T_SHIRT,
            Product.FLEECE_JACKET, Product.ONESIE, Product.TEST_ALL_THE_THINGS
    };

    private static final Product[] BY_NAME_DESCENDING = {
            Product.TEST_ALL_THE_THINGS, Product.ONESIE, Product.FLEECE_JACKET,
            Product.BOLT_T_SHIRT, Product.BIKE_LIGHT, Product.BACKPACK
    };

    // Bolt T-Shirt e Test.allTheThings() empatam em $15.99; a aplicacao mantem
    // essa ordem relativa nos dois sentidos.
    private static final Product[] BY_PRICE_ASCENDING = {
            Product.ONESIE, Product.BIKE_LIGHT, Product.BOLT_T_SHIRT,
            Product.TEST_ALL_THE_THINGS, Product.BACKPACK, Product.FLEECE_JACKET
    };

    private static final Product[] BY_PRICE_DESCENDING = {
            Product.FLEECE_JACKET, Product.BACKPACK, Product.BOLT_T_SHIRT,
            Product.TEST_ALL_THE_THINGS, Product.BIKE_LIGHT, Product.ONESIE
    };

    private Catalog() {
    }

    public static int size() {
        return Product.values().length;
    }

    public static String[] namesByNameAscending() {
        return namesOf(BY_NAME_ASCENDING);
    }

    public static String[] namesByNameDescending() {
        return namesOf(BY_NAME_DESCENDING);
    }

    public static String[] pricesByPriceAscending() {
        return pricesOf(BY_PRICE_ASCENDING);
    }

    public static String[] pricesByPriceDescending() {
        return pricesOf(BY_PRICE_DESCENDING);
    }

    /** Nomes na ordem em que os produtos forem passados. */
    public static String[] namesOf(Product... products) {
        return Arrays.stream(products).map(Product::displayName).toArray(String[]::new);
    }

    private static String[] pricesOf(Product[] products) {
        return Arrays.stream(products).map(Product::price).toArray(String[]::new);
    }
}

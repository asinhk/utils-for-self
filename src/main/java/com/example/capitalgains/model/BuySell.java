package com.example.capitalgains.model;

public enum BuySell {
    B("Buy"),
    S("Sell");
    private final String strVal;

    BuySell(String buysell) {
        this.strVal = buysell;
    }

    public static BuySell getFromString(String val) {
        if (B.strVal.equalsIgnoreCase(val))
            return B;
        else if (S.strVal.equalsIgnoreCase(val)) {
            return S;
        } else
            return null;
    }
}

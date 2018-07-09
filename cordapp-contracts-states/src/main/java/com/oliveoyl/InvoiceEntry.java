package com.oliveoyl;

import java.math.BigDecimal;

public class InvoiceEntry {
    private String description;
    private BigDecimal quantity;
    private BigDecimal unitPrice;

    public InvoiceEntry(String description, BigDecimal quantity, BigDecimal unitPrice) {
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getAmount() {
        return quantity.multiply(unitPrice);
    }
}

package com.tirana.smartparking.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Money {
    @JsonProperty("currency")
    private final String currency;
    
    @JsonProperty("amount")
    private final int amount; // in minor units (e.g., cents)
    
    @JsonProperty("breakdown")
    private final String breakdown; // JSON string with line items

    public Money(String currency, int amount, String breakdown) {
        this.currency = currency;
        this.amount = amount;
        this.breakdown = breakdown;
    }

    public static Money zero(String currency) {
        return new Money(currency, 0, "{}");
    }

    public String getCurrency() {
        return currency;
    }

    public int getAmount() {
        return amount;
    }

    public String getBreakdown() {
        return breakdown;
    }

    public double getAmountInMajorUnits() {
        return amount / 100.0; // Assuming 2 decimal places
    }

    @Override
    public String toString() {
        return String.format("%.2f %s", getAmountInMajorUnits(), currency);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Money money = (Money) obj;
        return amount == money.amount && 
               java.util.Objects.equals(currency, money.currency);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(currency, amount);
    }
}

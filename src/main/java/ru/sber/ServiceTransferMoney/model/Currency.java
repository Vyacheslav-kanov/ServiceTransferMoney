package ru.sber.ServiceTransferMoney.model;

import lombok.Data;

import java.util.Objects;

@Data
public class Currency {

    private int numCode;
    private String charCode;
    private int nominal;
    private String name;
    private double value;

    public Currency(int numCode, String charCode, int nominal, String name, double value) {
        this.numCode = numCode;
        this.charCode = charCode;
        this.nominal = nominal;
        this.name = name;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Currency currency = (Currency) o;
        return numCode == currency.numCode && Objects.equals(charCode, currency.charCode) && Objects.equals(name, currency.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numCode, charCode, name);
    }
}

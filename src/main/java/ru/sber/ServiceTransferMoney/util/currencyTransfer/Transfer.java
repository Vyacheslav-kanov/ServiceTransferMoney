package ru.sber.ServiceTransferMoney.util.currencyTransfer;

import ru.sber.ServiceTransferMoney.model.Currency;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Transfer {

    private Currency from;
    private Currency to;

    public Transfer(Currency from, Currency to) {
        this.from = from;
        this.to = to;
    }

    /**
     * Конвертировать сумму валюты from в валюту to.
     * @param quantity сумма для конвертирования.
     * @return вернется конвертированная BigDecimal сумма.
     */

    public BigDecimal convert (BigDecimal quantity){
        quantity = quantity.abs();
        BigDecimal ru = convertRU(quantity, from);
        BigDecimal divider = ru.divide(new BigDecimal(to.getNominal())).divide(new BigDecimal(to.getValue()), 3, RoundingMode.CEILING);
        return divider;
    }

    /**
     * Так как имеющиеся курсы в рублях для перевода других валют в другую валюту
     * требуется перевести одну из них в рубли и далее в требуемую валюту.
     * @param quality сумма валюты для перевода в рубли.
     * @param from объект валюты которую требуется перевести в рубли.
     * @return вернется BigDecimal сумма рублей.
     */

    private BigDecimal convertRU(BigDecimal quality, Currency from){
        return quality.multiply(new BigDecimal(from.getValue()));
    }
}

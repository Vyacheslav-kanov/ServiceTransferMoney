package ru.sber.ServiceTransferMoney.util.validation;

import ru.sber.ServiceTransferMoney.ServiceTransferMoneyApplication;
import ru.sber.ServiceTransferMoney.model.Currency;
import ru.sber.ServiceTransferMoney.model.CurrencyCode;
import ru.sber.ServiceTransferMoney.service.CurrencyService;
import ru.sber.ServiceTransferMoney.util.date.DateAssistant;

import java.math.BigDecimal;
import java.util.*;

public class Validator {

    private static CurrencyService service = ServiceTransferMoneyApplication.context.getBean("currencyService", CurrencyService.class);

    public static boolean isValid(String currencyCode){
       return Arrays.stream(CurrencyCode.values()).anyMatch(x -> x.getName().equals(currencyCode));
    }

    public static boolean isValid(BigDecimal decimal){
        return decimal != null;
    }

    public static boolean isValid(String date, int step, String meter, String cur){
        if(date == null || step == 0 || meter == null || cur == null) return false;
        if(date.split("\\.").length != 3) return false;
        if(meter.equals(DateAssistant.DAY)) return false;
        if(meter.equals(DateAssistant.MONTH)) return false;
        if(meter.equals(DateAssistant.YEAR)) return false;
        return isValid(cur);
    }

    public static boolean isTableDate(Date date){
        Map<Date, Map<Integer, Currency>> map = service.getExchangeRate(date);
        Date key = service.takeDate(map);
        return key.equals(date) & !map.get(key).isEmpty();
    }

    public static boolean isDayCourse(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek != 7 & dayOfWeek != 1;
    }

}

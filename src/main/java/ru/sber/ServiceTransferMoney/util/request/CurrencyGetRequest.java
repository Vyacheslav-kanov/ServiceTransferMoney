package ru.sber.ServiceTransferMoney.util.request;

import lombok.SneakyThrows;
import ru.sber.ServiceTransferMoney.ServiceTransferMoneyApplication;
import ru.sber.ServiceTransferMoney.model.Currency;
import ru.sber.ServiceTransferMoney.service.CurrencyService;
import ru.sber.ServiceTransferMoney.util.date.DateAssistant;
import ru.sber.ServiceTransferMoney.util.convertFormat.ConvertXML;
import ru.sber.ServiceTransferMoney.util.loger.Logger;
import ru.sber.ServiceTransferMoney.util.validation.Validator;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class CurrencyGetRequest {
    private static CurrencyService service = ServiceTransferMoneyApplication.context.getBean("currencyService", CurrencyService.class);

    /**
     * Произвести запрос с возможностью указать уточняющую дату
     * для получения курса валют за указанное число.
     * Если дату не указать (null/"") будет сделан запрос на последний курс валют.
     * Осуществление запрос является дорогостоящей операцией по памяти.
     * @param date уточняющая дата.
     * @return возвращает Map, где ключ: дата курса, значение: Map
     * в которой ключ: номер валюты, значение: объект валюты.
     */

    public static Map<Date, Map<Integer, Currency>> requestExchangeRate(String date) {                  //<Дата <номер-валюты, Объект Валюты>>
        Logger.debugLog("Request date " + date);
        String requestDate = ((date == null || date.isEmpty()) ? "":"date_req=" + toRequestDate(date));
        Map<Date, Map<Integer, Currency>> map;
        ConvertXML convertXML;
        try {
            URL url = new URL("http://www.cbr.ru/scripts/XML_daily.asp?" + requestDate);
            Logger.debugLog(url.toString());
            final HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(5000);                                                                //Время ожидания подключения
            con.setReadTimeout(30000);                                                                  //Время ожидания получения данных

            convertXML = new ConvertXML(con.getInputStream());                                          //Передаю поток парсеру
            map = convertXML.getValCurse();                                                             //забираю map
            return map;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    /**
     * Произвести многократные запросы для получения нескольких курсов валют
     * в промежутке от начальной даты до нынешней.
     * @param dateStart дата с которой начнется отчет до настоящего времени.
     * @param step промежуток, через который будут браться даты.
     * @param meter единица измерения шага "DAY", "MONTH" или "YEAR".
     * @return возвращает таблицу курса валют представленную как Map,
     * где ключ: дата курса, значение: Map
     * в которой ключ: номер валюты, значение: объект валюты.
     */

    @SneakyThrows
    public static Map<Date, Map<Integer, Currency>> requestManyExchangeRate(Date dateStart, int step, String meter){
        Map<Date, Map<Integer, Currency>> resultMap = new HashMap<>();
        Map<Date, Map<Integer, Currency>> requestMap = new HashMap<>();
        Date keyOfMap;

        while (DateAssistant.isAfter(dateStart) & !DateAssistant.getToDay().equals(dateStart)){
            Date actualDate = getLastActualDate(dateStart);
            if(!Validator.isTableDate(actualDate)){
                requestMap = requestExchangeRate(DateAssistant.toStringFormat(actualDate));
                keyOfMap = service.takeDate(requestMap);
                resultMap.put(actualDate, requestMap.get(keyOfMap));
            }
            dateStart = DateAssistant.plus(dateStart, step, meter);
            Logger.debugLog("Точка " + DateAssistant.toStringFormat(dateStart));
        }
        return resultMap;
    }

    private static Date getLastActualDate(Date date){
        Date changeDate = date;
        while (Validator.isDayCourse(date)) changeDate = DateAssistant.minusDay(changeDate, 1);
        return changeDate;
    }

    /**
     * Преобразование даты в формат для вставки в url запроса.
     * @param date дата для преобразования.
     * @return преобразованная дата.
     */

    private static String toRequestDate (String date){
        String[] strings = date.split("\\.");

        int day = Integer.parseInt(strings[0]);
        int month = Integer.parseInt(strings[1]);
        int year = Integer.parseInt(strings[2]);

        String dayStr = (day < 10)? "0" + day : day + "";
        String mothStr = (month < 10)? "0" + month : month + "";

        return dayStr + "/" + mothStr + "/" + year;
    }
}

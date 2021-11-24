package ru.sber.ServiceTransferMoney.util.chart;

import ru.sber.ServiceTransferMoney.ServiceTransferMoneyApplication;
import ru.sber.ServiceTransferMoney.model.Currency;
import ru.sber.ServiceTransferMoney.model.CurrencyCode;
import ru.sber.ServiceTransferMoney.service.CurrencyService;
import ru.sber.ServiceTransferMoney.util.date.DateAssistant;

import java.util.*;

public class ChartConfig {

    private static final CurrencyService service = ServiceTransferMoneyApplication.context.getBean("currencyService", CurrencyService.class);

    /**
     * Собрать статистику в промежутке с указанной начальной датой до нынешней даты
     * с шагом в "DAY", "MONTH" или "YEAR".
     * @param startDate дата с которой начнется путь до нынешней даты.
     * @param step промежуток, через который будут браться даты.
     * @param meter возвращает таблицу курса валют представленную как Map,
     * где ключ: дата курса, значение: Map
     * в которой ключ: номер валюты, значение: объект валюты.
     */

    public static Map<String, Map<Integer, Currency>> getStatistic(Date startDate, int step, String meter){
        var resultMap = new HashMap<String, Map<Integer, Currency>>();

        Map<Date, Map<Integer, Currency>> statistic = new HashMap<>();                      //Собираю статистику до сегодня
        statistic.putAll(service.getExchangeRateInGap(startDate, step, meter));
        statistic.putAll(service.getExchangeRate(service.getLastDate()));                   //Добавляю последний курс

        for(Date e: statistic.keySet()){                                                    //Изменяю тип ключа Date на String нужного формата
            resultMap.put(DateAssistant.toStringFormat(e), statistic.get(e));
        }
        return resultMap;
    }

    /**
     * Создать конфигурацию для графика.
     * Структура объектов должна быть построена так, чтобы при конвертации ее в JSON
     * имела корректный формат для постройки графика.
     * @param map Map из которой будут браться данные для постройки графика.
     * @param code Объект кода курса валюты, для понимания какой валюты график.
     * @return Возвращает Map спроектированной для постройки графика на сайте.
     */

    public static Map<String, Object> buildingConfig(Map<String, Map<Integer, Currency>> map, CurrencyCode code){
        TreeMap<String, Map<Integer, Currency>> treeMap = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                Date date1 = DateAssistant.parseDate(o1);
                Date date2 = DateAssistant.parseDate(o2);

                if(DateAssistant.isAfter(date1, date2)) return 1;
                if(DateAssistant.isBefore(date1, date2)) return -1;
                return 0;
            }
        });
        treeMap.putAll(map);                                                                //Упорядочиваю даты в хронологическом порядке

        TreeMap<String, Object> config = new TreeMap<>();

        List<String> labels = new ArrayList<>();
        TreeMap<String, Object> data = new TreeMap<>();
        List<Map<String, Object>> datasets = new ArrayList<>();
        List<Object> datasetsData = new ArrayList<>();
        TreeMap<String, Object> options = new TreeMap<>();

        datasets.add(new HashMap<>());
        datasets.get(0).put("label", code.getFullName());
        datasets.get(0).put("backgroundColor", "rgb(255, 99, 132)");
        datasets.get(0).put("borderColor", "rgb(255, 99, 132)");

        for(String date: treeMap.keySet()){
            labels.add(date);
            Currency currency = treeMap.get(date).get(code.getNumCode());
            if(currency == null) continue;
            datasetsData.add(currency.getValue());
        }
        datasets.get(0).put("data", datasetsData);

        data.put("labels", labels);
        data.put("datasets", datasets);

        config.put("type", "line");
        config.put("data", data);
        config.put("options", options);

        return config;
    }
}

package ru.sber.ServiceTransferMoney.repository;

import org.springframework.stereotype.Repository;
import ru.sber.ServiceTransferMoney.model.Currency;
import ru.sber.ServiceTransferMoney.util.date.DateAssistant;
import ru.sber.ServiceTransferMoney.util.loger.Logger;
import ru.sber.ServiceTransferMoney.util.validation.Validator;

import java.sql.*;
import java.util.*;
import java.util.Date;

import static ru.sber.ServiceTransferMoney.util.updateExchangeRate.RestorerExchangeRate.*;

@Repository
public class CurrencyRepository {

    private static final String url = "jdbc:postgresql://localhost:5432/CurrencyRate";
    private static final String user = "postgres";
    private static final String password = "kanov2001";

    protected static Connection connection;

    static {
        try {
            Class.forName("org.postgresql.Driver");

            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static Date lastDate = getLastReceivedDate();

    /**
     * Получить курс валюты за конкретную дату.
     * @param date дата за которую нужно получить курс валюты
     * @param id уникальный номер валюты
     * @return возвращает объект валюты.
     * @throws NullPointerException
     */

    public Currency getById(Date date, int id) throws NullPointerException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM exchange_rate WHERE date = ? AND num_code = ?")){
            preparedStatement.setString(1, DateAssistant.toStringFormat(date));
            preparedStatement.setInt(2, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();

            return new Currency(
                    resultSet.getInt("num_code"),
                    resultSet.getString("char_code"),
                    resultSet.getInt("nominal"),
                    resultSet.getString("title"),
                    resultSet.getDouble("value")
            );
        } catch (SQLException e) {
            e.printStackTrace();
            return (date.equals(lastDate))? null : getById(lastDate, id);
        }
    }

    /**
     * Получить последний актуальный курс относительно заданной даты
     * @param date дата за которую нужно получить курс валют
     * @return возвращает Map, где ключ: дата курса, значение: Map
     * в которой ключ: номер валюты, значение: объект валюты
     */

    public Map<Date, Map<Integer, Currency>> getExchangeRate(Date date){
        Date changeableDate = date;                                                         //Указатель для запроса (сдвигается при откате)
        Map<Date, Map<Integer, Currency>> resultMap = new HashMap<>();
        HashMap<Integer, Currency> unit = new HashMap<>();

        for (int i = 0; i < 3; i++) {                                                       //Если не существует курс на данную дату откатываемся на день (максимум на 3)
            changeableDate = DateAssistant.minusDay(changeableDate, i);                     //Сдвигаем указатель
            try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM exchange_rate WHERE date = \'" + DateAssistant.toStringFormat(changeableDate) + "\'")) {
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    Currency currency = new Currency(
                            resultSet.getInt("num_code"),
                            resultSet.getString("char_code"),
                            resultSet.getInt("nominal"),
                            resultSet.getString("title"),
                            resultSet.getDouble("value")
                    );
                    unit.put(currency.getNumCode(), currency);
                }
            } catch (SQLException throwables) {}
            if(!unit.isEmpty()) break;                                                      //Если нашли таблицу до конца цикла, выходим из него
            changeableDate = date;                                                          //Обновляем дату
        }
        resultMap.put(changeableDate, unit);
        return resultMap;
    }

    /**
     * Получить промежуток до последнего курса.
     * @param dateStart дата с которой начнется отчет до настоящего времени.
     * @param step промежуток, через который будут браться даты.
     * @param meter единица измерения шага "DAY", "MONTH" или "YEAR".
     * @return возвращает таблицу курса валют представленную как Map,
     * где ключ: дата курса, значение: Map
     * в которой ключ: номер валюты, значение: объект валюты.
     */

    public Map<Date, Map<Integer, Currency>> getExchangeRateInGap(Date dateStart, int step, String meter){
        Date now = dateStart;                                                               //Указатель, дата на которой сейчас находится цикл
        var resultMap = new HashMap<Date, Map<Integer, Currency>>();

        while (DateAssistant.isAfter(lastDate, now)){                                       //Шагаем по датам пока не зайдем за сегодня
            var map = getExchangeRate(now);
            if(map.isEmpty()){
                now = DateAssistant.plus(now, step, meter);
                continue;
            }
            Date dateOfMap = takeDate(map);                                                 //В случае если откатывались по дате получаем дату из ключа map
            resultMap.put(dateOfMap, map.get(dateOfMap));                                   //Добавляем к результату
            now = DateAssistant.plus(now, step, meter);                                     //Делаем шаг
        }
        return resultMap;
    }

    /**
     * Сохранить курс валюты в таблицу
     * @param date дата таблицы в которую нужно сохранить курс валюты
     * @param currency сохраняемый объект курса валюты
     */

    public void saveCurrency(Date date , Currency currency){
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO exchange_rate  " + "VALUES(?, ?, ?, ?, ?, ?)")){
            preparedStatement.setString(1, DateAssistant.toStringFormat(date));
            preparedStatement.setInt(2, currency.getNumCode());
            preparedStatement.setString(3, currency.getCharCode());
            preparedStatement.setInt(4, currency.getNominal());
            preparedStatement.setString(5, currency.getName());
            preparedStatement.setDouble(6, currency.getValue());

            preparedStatement.executeUpdate();
            Logger.debugLogNews("Save in exchange_rate - " + currency.getName() + " " + DateAssistant.toStringFormat(date) + " " + " completed");
        } catch (SQLException throwables) {
            Logger.debugLogNews("Save in exchange_rate - " + currency.getName() + " " + DateAssistant.toStringFormat(date) + " " + " fail");
            throwables.printStackTrace();
        }
    }

    /**
     * Сохранить/создать таблицу курса валют
     * @param map таблица курса валют представленная как Map,
     * где ключ: дата курса, значение: Map
     * в которой ключ: номер валюты, значение: объект валюты
     */

    public void saveExchangeRate(Map<Date, Map<Integer, Currency>> map){
        for(Date key: map.keySet()){
            if(!Validator.isTableDate(key)){
                for(Currency a: map.get(key).values()){
                    saveCurrency(key, a);
                }
                Logger.debugLogNews("SaveAll in exchange_rate completed");
            }
            else Logger.debugLogNews("SaveAll in exchange_rate fail");
        }
    }

    /**
     * Изменить дату на новую актуальную
     * @param newDate дата для изменения
     */

    public void setLastDate(Date newDate){

        if(lastDate == null){
            lastDate = newDate;
            Logger.debugLog("SetDate - " + newDate);
            return;
        }

        if(DateAssistant.isAfter(newDate, lastDate)){
            lastDate = newDate;
            Logger.debugLog("SetDate - " + newDate);
        }
    }

    /**
     * взять дату из ключа Map
     * @param map Map из которой нужно взять дату
     * @return полученная дата
     */

    public static Date takeDate(Map<Date, Map<Integer, Currency>> map){
        return (Date)map.keySet().toArray()[0];
    }

    public static Date getLastDate() {
        return lastDate;
    }
}

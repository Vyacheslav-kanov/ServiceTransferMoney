package ru.sber.ServiceTransferMoney.util.updateExchangeRate;

import ru.sber.ServiceTransferMoney.repository.CurrencyRepository;
import ru.sber.ServiceTransferMoney.util.date.DateAssistant;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class RestorerExchangeRate extends CurrencyRepository {

    public static Date getLastReceivedDate(){
        Date result = new Date();
        try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT date FROM exchange_rate,TO_DATE(date, 'DD.MM.YYYY') ORDER BY to_date DESC LIMIT 1")){
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            result = DateAssistant.parseDate(resultSet.getString("date"));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        System.out.println(DateAssistant.toStringFormat(result));
        return result;
    }
}

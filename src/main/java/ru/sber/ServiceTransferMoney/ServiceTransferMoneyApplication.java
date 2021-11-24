package ru.sber.ServiceTransferMoney;

import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.sber.ServiceTransferMoney.service.CurrencyService;
import ru.sber.ServiceTransferMoney.util.date.DateAssistant;
import ru.sber.ServiceTransferMoney.util.request.CurrencyGetRequest;
import ru.sber.ServiceTransferMoney.util.updateExchangeRate.TaskUpdate;
import ru.sber.ServiceTransferMoney.util.validation.Validator;

import java.sql.Time;

@SpringBootApplication
public class ServiceTransferMoneyApplication {

	public static final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("ru.sber.ServiceTransferMoney");

	@SneakyThrows
	public static void main(String[] args) {
		SpringApplication.run(ServiceTransferMoneyApplication.class, args);

		CurrencyService service = context.getBean("currencyService", CurrencyService.class);

		/**
		 * В начале работы программы идет проверка актуальности БД
		 */

		var map = CurrencyGetRequest.requestManyExchangeRate(service.getLastDate(), 1, DateAssistant.DAY);
		if(!map.isEmpty() && Validator.isTableDate(service.takeDate(map))) service.saveExchangeRate(map);

		/**
		 * Далее запускается таймер на 12ч дня.
		 * В это время обновляется курс валют в Центр Банке (только будние дни)
		 */

		TaskUpdate.timerExchangeRate(new Time(43200000)); //12ч
	}
}

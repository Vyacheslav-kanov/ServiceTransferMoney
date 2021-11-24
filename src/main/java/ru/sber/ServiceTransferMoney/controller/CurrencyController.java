package ru.sber.ServiceTransferMoney.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sber.ServiceTransferMoney.model.Currency;
import ru.sber.ServiceTransferMoney.service.CurrencyService;
import ru.sber.ServiceTransferMoney.model.CurrencyCode;
import ru.sber.ServiceTransferMoney.util.currencyTransfer.Transfer;
import ru.sber.ServiceTransferMoney.util.chart.ChartConfig;
import ru.sber.ServiceTransferMoney.util.date.DateAssistant;
import ru.sber.ServiceTransferMoney.util.loger.Logger;
import ru.sber.ServiceTransferMoney.util.validation.Validator;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/serviceTransferMoney")
public class CurrencyController {
    private final CurrencyService service;
    private final File file = new File("src/main/resources/form.html/result-currency.html");

    public CurrencyController(CurrencyService service) {
        this.service = service;
    }

    @GetMapping("/convert")
    private ResponseEntity<Double> transfer(@RequestParam("from") String from, @RequestParam("to") String to, @RequestParam("quantity") BigDecimal quantity) {
        Logger.debugLog("Request: " + quantity + " " + from + " -> " + to);

        if(!Validator.isValid(from) && !Validator.isValid(to) && !Validator.isValid(quantity)){              //Проверка валидности. Возвращаю BAD REQUEST
            Logger.debugLog("Request: " + from + " -> " + to + " " + quantity + " BAD REQUEST");
            return ResponseEntity.badRequest().build();
        }

        if(quantity.equals(0)){                                                                             //При любом курсе 0 будет 0. Возвращаю 0
            Logger.debugLog("Response: " + from + " -> " + to + " " + 0);
            return ResponseEntity.ok(0.0);
        }

        CurrencyCode fromCode = CurrencyCode.valueOf(from);                                                 //Получаю коды валют
        Currency fromCurrency = service.getById(service.getLastDate(), fromCode.getNumCode());
        CurrencyCode toCode = CurrencyCode.valueOf(to);
        Currency toCurrency = service.getById(service.getLastDate(), toCode.getNumCode());

        if(fromCode.equals(toCode)){                                                                        //При одинаковом курсе количество не измениться. Возвращаю это же количество
            Logger.debugLog("Response: " + from + " -> " + to + " " + quantity);
            return ResponseEntity.ok(quantity.doubleValue());
        }

        BigDecimal result = new Transfer(fromCurrency, toCurrency).convert(quantity);                       //Конвертирование валюты
        Logger.debugLog("Response: " + from + " -> " + to + " " + result);
        return ResponseEntity.ok(result.doubleValue());
    }

    @GetMapping("")
    private ResponseEntity mainPage(){
        String html = "";
        try(FileReader reader = new FileReader(file.getPath()))
        {
            int i;
            while((i = reader.read()) != -1) html += (char)i;
            return ResponseEntity.ok(html);
        }
        catch(IOException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/chart")
    private ResponseEntity jsonChart(@RequestParam("date") String date, @RequestParam("step") int step, @RequestParam("meter") String meter, @RequestParam("currency") String cur){
        Logger.debugLog("Request: " + date + " " + step +  " " + meter + " " + cur);
        if(Validator.isValid(date, step, meter, cur)) ResponseEntity.badRequest();

        Date dateForm = DateAssistant.parseDate(date);
        Map map = ChartConfig.getStatistic(dateForm, step, meter);
        CurrencyCode code = CurrencyCode.valueOf(cur);

        return ResponseEntity.ok(ChartConfig.buildingConfig(map, code));
    }
}

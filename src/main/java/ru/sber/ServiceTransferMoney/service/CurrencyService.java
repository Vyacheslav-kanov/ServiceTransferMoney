package ru.sber.ServiceTransferMoney.service;

import org.springframework.stereotype.Service;
import ru.sber.ServiceTransferMoney.model.Currency;
import ru.sber.ServiceTransferMoney.repository.CurrencyRepository;

import java.util.Date;
import java.util.Map;

@Service
public class CurrencyService {
    private final CurrencyRepository repository;

    public CurrencyService(CurrencyRepository repository) {
        this.repository = repository;
    }

    public Currency getById(Date date, int id){
        return repository.getById(date, id);
    }

    public Map<Date, Map<Integer, Currency>> getExchangeRate(Date date){
        return repository.getExchangeRate(date);
    }

    public Map<Date, Map<Integer, Currency>> getExchangeRateInGap(Date dateStart, int step, String meter){
        return repository.getExchangeRateInGap(dateStart, step, meter);
    }

    public void saveCurrency(Date date, Currency currency){
        repository.saveCurrency(date, currency);
    }

    public void saveExchangeRate(Map<Date, Map<Integer, Currency>> map){
        repository.saveExchangeRate(map);
    }

    public void setLastDate(Date newDate){
        repository.setLastDate(newDate);
    }

    public Date takeDate(Map<Date, Map<Integer, Currency>> map){
        return repository.takeDate(map);
    }

    public Date getLastDate() {
        return repository.getLastDate();
    }
}

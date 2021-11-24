package ru.sber.ServiceTransferMoney.util.updateExchangeRate;

import ru.sber.ServiceTransferMoney.ServiceTransferMoneyApplication;
import ru.sber.ServiceTransferMoney.service.CurrencyService;
import ru.sber.ServiceTransferMoney.util.loger.Logger;
import ru.sber.ServiceTransferMoney.util.request.CurrencyGetRequest;

import java.sql.Time;
import java.time.LocalTime;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TaskUpdate {
    private static final long milsOfDay = 86400000L; //24ч
    private static final long zone = 18000000L; //5ч

    private static final CurrencyService service = ServiceTransferMoneyApplication.context.getBean("currencyService", CurrencyService.class);

    /**
     * Каждый день в установленное время производить запрос на получение
     * свежего курса валют.
     * @param time время в которое производить запрос
     * @throws InterruptedException
     */

    public static void timerExchangeRate(Time time) throws InterruptedException {
        TimerTask repeatedTask = new TimerTask() {
            public void run() {
                Logger.debugLog("Start update " + new Date());
                Logger.debugLog("Update after " + new Time(setupTimer(time)));
                var map = CurrencyGetRequest.requestExchangeRate("");
                service.setLastDate(service.takeDate(map));
                service.saveExchangeRate(map);
            }
        };
        Timer timer = new Timer("Timer");
        timer.scheduleAtFixedRate(repeatedTask, 0, setupTimer(time));
    }

    private static long setupTimer(Time time){
        long milsTime = time.getTime() - zone;
        long now = getNowMils();

        if(now > milsTime){
            return milsOfDay - now + milsTime;
        }else {
            return milsTime - now;
        }
    }

    private static long getNowMils(){
        long hourMils = LocalTime.now().getHour() * 60 * 60 * 1000;
        long minuteMils = LocalTime.now().getMinute() * 60 * 1000;
        long secMils = LocalTime.now().getSecond() *  1000;

        return hourMils + minuteMils + secMils;
    }
}

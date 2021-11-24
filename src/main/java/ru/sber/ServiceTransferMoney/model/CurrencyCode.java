package ru.sber.ServiceTransferMoney.model;

public enum CurrencyCode {

     RU("Русский рубль", "RU", 0)
    ,AUD("Австралийский доллар", "AUD",036)
    ,AZN("Азербайджанский манат", "AZN",944)
    ,GBP("Фунт стерлингов Соединенного королевства", "GBP",826)
    ,AMD("Армянский драмов", "AMD",51)
    ,BYN("Белорусский рубль", "BYN",933)
    ,BGN("Болгарский лев", "BGN",975)
    ,BRL("Бразильский реал", "BRL",986)
    ,HUF("Венгерских форинтов", "HUF",348)
    ,HKD("Гонконгских долларов", "HKD",344)
    ,DKK("Датский крон", "DKK",208)
    ,USD("Доллар США", "USD",840)
    ,EUR("Евро", "EUR",978)
    ,INR("Индийских рупий", "INR",356)
    ,KZT("Казахских танге", "KZT",398)
    ,CAD("Канадский доллар", "CAD",124)
    ,KGS("Киргизских сомов", "KGS",417)
    ,CNY("Китайских юаней", "CNY",156)
    ,MDL("Молдавский леев", "MDL",498)
    ,NOK("Норвежский крон", "NOK",578)
    ,PLN("Польский злотый", "PLN",986)
    ,RON("Румынский лей", "RON",946)
    ,XDR("СДР (специальные права заимствования)", "XDR",960)
    ,SGD("Сингапурский доллар", "SGD",702)
    ,TJS("Таджикских сомони", "TJS",972)
    ,TRY("Турецкая лира", "TRY",949)
    ,TMT("Новый туркменский манат", "TMT",934)
    ,UZS("Узбекских юаней", "UZS",860)
    ,UAH("Украинский гривен", "UAH",980)
    ,CZK("Чешский крон", "CZK",203)
    ,SEK("Шведский крон", "SEK",752)
    ,CHF("Швейцарский франк", "CHF",756)
    ,ZAR("Южноафриканский рэндов", "ZAR",710)
    ,KRW("Вон Республики Корея", "KRW",410)
    ,JPY("Японский иен", "JPY",392);

     private String fullName;
     private String name;
     private int numCode;


    CurrencyCode(String fullName, String name, int numCode) {
        this.fullName = fullName;
        this.numCode = numCode;
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public String getName() {
        return name;
    }

    public int getNumCode() {
        return numCode;
    }
}

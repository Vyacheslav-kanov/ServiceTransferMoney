# Сервис перевода денег
## Описание проекта
Название: ServiceTransferMoney;  
Языки: 
- Java 17;
- JavaScript; 
- HTML; 
- PostgresSQL;  
  
Платформа разработки: INTELLIJ IDEA Communiti idition.  
Библиотеки/инструменты:
- Lambok;
- Spring fratmwork; 
- Postgresql;
- Maven.   

Сервис предназначен для конвертирования валюты в любую другую валюту по курсу ЦентрБанка.
Сервис дает возможность ознакомиться с статистикой изменения курсов валют начиная с 01.01.2000.

## Алгоритм работы
1. Запуск Postgessql server;
2. Запуск INTELLIJ IDEA Communiti idition;
3. Запуск консольного приложения;
4. Запустить веб браузер и в вставить ссылку в строку url: http://localhost:8080/serviceTransferMoney/? .

## Требования
- Веб Браузер;
- Postgressql server;
- JVM;
- Java compiler.  

## Функционал
- Перевод указанной суммы валюты в требуемую по курсу ЦентрБанка;  
- График статистики курса валюты начиная с 01.01.2000;  
- Обновление курса валют при работающей программе каждый день в 12ч;  
- Докачивание отсутствующих курсов валют с момента последнего обновления.  
- При отсутствии запрашиваемого курса валюты, использует ближаюшую дату до 3-х дней.

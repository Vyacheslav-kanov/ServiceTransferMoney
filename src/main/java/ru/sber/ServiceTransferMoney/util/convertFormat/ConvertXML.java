package ru.sber.ServiceTransferMoney.util.convertFormat;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.sber.ServiceTransferMoney.model.Currency;
import ru.sber.ServiceTransferMoney.util.date.DateAssistant;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ConvertXML {

    private NodeList nodeList;
    private Document root;

    /**
     * Принимает поток ввода, парсит его в лист элементов из XML.
     * @param inputStream поток ввода XML документа.
     */

    public ConvertXML(InputStream inputStream) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            root = documentBuilder.parse(inputStream);
            nodeList = root.getDocumentElement().getChildNodes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Получить курс валют из листа элементов XML.
     * @return возвращает Map, где ключ: дата курса, значение: Map
     * в которой ключ: номер валюты, значение: объект валюты.
     */

    public Map<Date, Map<Integer, Currency>> getValCurse(){
        Map<Date, Map<Integer, Currency>> map = new HashMap<>();
        String dateStrExchangeRate = root.getDocumentElement().getAttribute("Date");
        Date dateExchangeRate = DateAssistant.parseDate(dateStrExchangeRate);
        Node node;

        map.put(dateExchangeRate, new HashMap<>());
        map.get(dateExchangeRate).put(0, new Currency(0, "RU", 1, "Русский Рубль", 1)); //Добавляю курс рубля для конвертации валют

        for (int i = 0; i < nodeList.getLength(); i++) {
            node = nodeList.item(i);

            if(Node.ELEMENT_NODE == node.getNodeType()){
                Element element = (Element) node;

                Currency currency = new Currency(                                                                           //Получение и парсинг элементов
                        Integer.parseInt(element.getElementsByTagName("NumCode").item(0).getTextContent()),
                        element.getElementsByTagName("CharCode").item(0).getTextContent(),
                        Integer.parseInt(element.getElementsByTagName("Nominal").item(0).getTextContent()),
                        element.getElementsByTagName("Name").item(0).getTextContent(),
                        Double.parseDouble(element.getElementsByTagName("Value").item(0).getTextContent().replace(",", "."))
                );
                map.get(dateExchangeRate).put(currency.getNumCode(), currency);
            }
        }
        return map;
    }
}

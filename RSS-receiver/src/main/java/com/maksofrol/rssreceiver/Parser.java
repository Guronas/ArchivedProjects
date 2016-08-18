package com.maksofrol.rssreceiver;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Приложение для получения rss-новостей:
 * Приложение, при вызове метода outputFeed(), считывает список источников из файла (файл передается в конструкторе).
 * Запускается несколько потоков, каждый из которых обрабатывает свой источник.
 * Новости выводятся непосредственно на консоль. При выводе результатов новости выводятся по дате.
 * Каждые 5 минут проверяется наличие обновлений в указанных источниках.
 */

public class Parser {
    private List<String> sources = new ArrayList<>();
    private final ConcurrentSkipListMap<Date, String> rssFeed = new ConcurrentSkipListMap<>();
    private Date lastDate = new Date(0);

    public Parser(String sourceFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(sourceFile))) {
            String nextLine;
            while ((nextLine = reader.readLine()) != null) {
                sources.add(nextLine);
            }

            if (sources.isEmpty()) {
                throw new RuntimeException("Source file is empty");
            }

        } catch (RuntimeException | IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    /**
     * Метод вызывает parseFeed(), который заполняет rssFeed распарсеной лентой новостей, после этого они выводятся
     * на консоль отсортированная по дате. Метод отрабатывает через каждые 5 минут, при этом выводятся только те новости,
     * которые еще не выводились на консоль.
     */
    public void outputFeed() {
        while (true) {
            parseFeed();
            try {
                if (rssFeed.isEmpty()) throw new RuntimeException("Feed is empty...");
                for (Date date : rssFeed.keySet()) {
                    if (date.after(lastDate)) {
                        lastDate = date;
                        System.out.println(date);
                        System.out.println(rssFeed.get(date) + "\n");
                    }
                }
                rssFeed.clear();
            } catch (RuntimeException ex) {
                System.out.println(ex.getMessage());
            }

            try {
                Thread.sleep(300000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Метод создает несколько потоков, по числу источников. Каждый поток загружает свою rss-ленту, парсит ее и
     * добавляет в rssFeed. Основной поток при этом ожидает завершение всех остальных потоков, обрабатывающих источники.
     */
    public void parseFeed() {
        try {
            if (sources.isEmpty()) throw new RuntimeException("There are no specific sources");
        } catch (RuntimeException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }

        Thread thread;
        ArrayList<Thread> threadPool = new ArrayList<>();
        for (String source : sources) {
            thread = new Thread(new DOMParser(source));
            threadPool.add(thread);
            thread.start();
        }

        for (Thread t : threadPool) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Класс реализующий интерфейс Runnable и метод run(), в котором происходит парсинг rss-ленты.
     * Конструктор принимает в качестве параметра url источника. Экземпляры класса с каждым из источников
     * передаются в соответсвующие потоки, которые их обрабатывают.
     */
    private class DOMParser implements Runnable {
        private String url;

        DOMParser(String url) {
            this.url = url;
        }

        private SimpleDateFormat checkDate(String date) {
            SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
            try {
                new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US).parse(date);
            } catch (ParseException ex) {
                format = new SimpleDateFormat("dd MMM yyyy HH:mm:ss z", Locale.US);
            }
            return format;
        }

        @Override
        public void run() {
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setIgnoringElementContentWhitespace(true);
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(url);
                doc.getDocumentElement().normalize();
                NodeList items = doc.getElementsByTagName("item");

                SimpleDateFormat format = null;
                for (int i = 0; i < items.getLength(); i++) {
                    String titleAndDiscr;
                    Date date;
                    Node n = items.item(i);

                    if (n.getNodeType() != Node.ELEMENT_NODE) {
                        continue;
                    }

                    Element e = (Element) n;
                    if (format == null) {
                        format = checkDate(e.getElementsByTagName("pubDate").item(0).getTextContent());
                    }
                    titleAndDiscr = e.getElementsByTagName("title").item(0).getTextContent() + "\n" +
                            e.getElementsByTagName("description").item(0).getTextContent();
                    date = format.parse(e.getElementsByTagName("pubDate").item(0).getTextContent());
                    rssFeed.put(date, titleAndDiscr);
                }
            } catch (ParserConfigurationException | SAXException e) {
                e.printStackTrace();
            } catch (IOException ex) {
                System.out.println("One of the URL is wrong. Check source file.");
            } catch (ParseException ex) {
                System.out.println("One of the URL is wrong. Unparseable type.");
            }
        }
    }
}

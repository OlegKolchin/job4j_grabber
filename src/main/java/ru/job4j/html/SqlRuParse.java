package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SqlRuParse {
    public static void main(String[] args) throws Exception {
        int index = 1;
        while (index <= 5) {
            Document doc = Jsoup.connect("https://www.sql.ru/forum/job-offers/" + index).get();
            Elements row = doc.select(".postslisttopic");
            for (Element td : row) {
                Element date = td.parent().child(5);
                Element href = td.child(0);
                System.out.println(href.attr("href"));
                System.out.println(href.text());
                System.out.println(date.text());
            }
            index++;
        }
    }
}
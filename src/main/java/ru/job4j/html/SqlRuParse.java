package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class SqlRuParse {
    public static String postReader(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Element el = doc.selectFirst(".msgBody");
        Element body = el.parent().child(1);
        Element date = el.parent().parent().child(2).child(0);
        return String.format("%s%n%s", body.text(), date.text().split("\\[")[0]);
    }

    public static void main(String[] args) throws Exception {
        int index = 1;
        while (index <= 5) {
            Document doc = Jsoup.connect("https://www.sql.ru/forum/job-offers/" + index).get();
            Elements row = doc.select(".postslisttopic");
            for (Element td : row) {
                Element date = td.parent().child(5);
                Element href = td.child(0);
                String url = href.attr("href");
                System.out.println(url);
                System.out.println(href.text());
                System.out.println(date.text());
                System.out.println(postReader(url));
            }
            index++;
        }
    }
}
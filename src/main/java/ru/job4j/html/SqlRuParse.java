package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.Parse;
import ru.job4j.grabber.Post;
import ru.job4j.grabber.utils.DateTimeParser;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SqlRuParse implements Parse {
    private final DateTimeParser dateTimeParser;

    public SqlRuParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    @Override
    public List<Post> list(String link) throws IOException, ParseException {
        List<Post> rsl = new ArrayList<>();
        int index = 1;
        while (index <= 5) {
            Document doc = Jsoup.connect(link + index).get();
            Elements row = doc.select(".postslisttopic");
            for (Element td : row) {
                Element href = td.child(0);
                String url = href.attr("href");
                String title = href.text();
                String desc = postReader(url);
                String[] temp = desc.split("\\n");
                int id = 0;
                LocalDateTime created = dateTimeParser.parse(temp[temp.length - 1]);
                rsl.add(new Post(id++, title, url, desc, created));
            }
            index++;
        }
        return rsl;
    }

    @Override
    public Post detail(String link) throws IOException, ParseException {
        Document doc = Jsoup.connect(link).get();
        String name = doc.selectFirst(".messageHeader").text();
        String desc = postReader(link);
        String[] temp = desc.split("\\n");
        LocalDateTime created = dateTimeParser.parse(temp[temp.length - 1]);
        return new Post(0, name, link, desc, created);
    }

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
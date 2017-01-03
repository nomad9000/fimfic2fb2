package org.anon;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URL;

public class Parser {

    public static Book parseBook(URL url) {
        Book book = new Book();
        try {
            Document doc = Jsoup.connect(url.toString()).get();
            book.setBookTitle(doc.select("story_name").first().text());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return book;
    }
}

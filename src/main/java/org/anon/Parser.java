package org.anon;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;

public class Parser {

    public static Book parseBook(URL url) {
        Book book = new Book();
        try {
            Document doc = Jsoup.connect(url.toString()).get();
            String bookTitle;
            if ((bookTitle = doc.select("story_name").first().text()) != null) {
                book.setBookTitle(bookTitle);
            }
            book.setBookTitle(doc.select("story_name").first().text());


        } catch (IOException e) {
            e.printStackTrace();
        }
        return book;
    }

    private static boolean chekcConsistency(Document doc) {
        Elements elements = doc.getAllElements();
        return false;
    }
}

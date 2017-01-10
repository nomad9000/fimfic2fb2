package org.anon;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.ParseError;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Parser {

    public static Book parseBook(URL url) throws Exception {
        Book book = new Book();
        try {
            Document doc = Jsoup.connect(url.toString()).get();
            Elements genres;
            Element bookTitle, author, keywords, lastUpdated, coverpage, rating, wordcount, status;
            List<String> ex = new ArrayList<>();
            //TODO: check for validness with Cleaner
            if ((bookTitle = doc.select("html body .body_container .content .content_background " +
                    ".inner .user_blog_post .left .story_container .story_content_box .no_padding .title " +
                    ".resize_text .story_name").first()) != null) {
                book.setBookTitle(bookTitle.text());
            } else {
                ex.add(".story_name");
            }
            if ((rating = doc.select("html body .body_container .content .content_background .inner " +
                    ".user_blog_post  .left .story_container .story_content_box .no_padding " +
                    ".title [class^=content-rating]").first()) != null) {
                book.setRating(rating.attr("title"));
            } else {
                ex.add("[class^=content-rating]");
            }
            if ((wordcount = doc.select("html body .body_container .content .content_background .inner " +
                    ".user_blog_post .left .story_container .story_content_box .no_padding .story .story_data .right " +
                    ".padding [id^=form-chapter-list] .chapters .bottom .word_count").first()) != null) {
                book.setWordcount(wordcount.text());
            } else {
                ex.add(".word_count");
            }
            if ((status = doc.select("html body .body_container .content .content_background .inner " +
                    ".user_blog_post .left .story_container .story_content_box .no_padding .story .story_data .right " +
                    ".padding [id^=form-chapter-list] .chapters .bottom [class^=completed-status]").first()) != null) {
                book.setStatus(status.attr("title"));
            } else {
                ex.add("[class^=completed-status]");
            }
            if (!ex.isEmpty()) {
                StringBuilder e = new StringBuilder("There is some fields missing in the input page (");
                for (int i = 0; i < ex.size(); i++) {
                    e.append(ex.get(i));
                    if (i < ex.size()-1) {
                        e.append(", ");
                    }
                }
                e.append("). You have to submit main page of the story, where description, tags and chapter list is.");
                throw new Exception(e.toString()); //TODO: more specific Exception class
            }

            if ((genres = doc.select("html body .body_container .content .content_background .inner " +
                    ".user_blog_post .left .story_container .story_content_box .no_padding .story .story_data " +
                    ".right .padding .description .story_category")) != null) {
                List<String> genresParsed = new ArrayList<>();
                for (Element e : genres) {
                    genresParsed.add(e.childNode(0).toString());
                }
                book.setGenres(genresParsed);
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

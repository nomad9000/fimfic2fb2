package org.anon;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.ParseError;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

public class Parser {

    private HashMap<String, String> cookies = new HashMap<>();
    private Book book = new Book();

    Parser() {}

    public Book parseBook(URL url) throws Exception {
        //addCookie("view_mature", "true");
        try {
            Document doc = Jsoup.connect(url.toString()).cookies(cookies).get();
            //TODO: fb2 required a closing pair for hr tag
            //TODO: remove first hr tag from description
            Elements genres;
            Element bookTitle, author, keywords, lastUpdated, coverpage, rating, wordcount, status;
            List<String> ex = new ArrayList<>();
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

    private void iterateThroughNodeList(List<? extends Node> nodes) {
        for (Node n : nodes) {
            checkForPicture(n);
            removeUnnecessaryAttributes(n);
            iterateThroughNodeList(n.childNodes());
        }
    }

    private void checkForPicture(Node node) {
        if (node.nodeName().equals("img")) {
            String pictureURL = node.attr("src");
            if (!book.isBinaryExist(pictureURL)) {
                try {
                    Connection.Response response = Jsoup.connect(node.attr("src")).cookies(cookies).
                            ignoreContentType(true).execute();
                    byte[] pictureAsBytes = response.bodyAsBytes();
                    String pictureAsBASE64 = Base64.getEncoder().encodeToString(pictureAsBytes);
                    book.addBinary(pictureURL, pictureAsBASE64);
                    Element image = new Element("image");
                    image.attr("xlink:href", "#" + pictureURL);
                    node.replaceWith(image);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void removeUnnecessaryAttributes(Node node) {
            node.removeAttr("rel");
            node.removeAttr("style");
            node.removeAttr("class");
            node.removeAttr("alt");
    }

    private void addCookie(String key, String value) {
        cookies.put(key, value);
    }
}

package org.anon;

import org.anon.Exceptions.TagNotFoundException;
import org.anon.Exceptions.TagsNotFoundException;
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
    private List<String> ex = new ArrayList<>();

    Parser() {}

    public Book parseBook(URL url) throws TagsNotFoundException {
        try {
            Document doc = Jsoup.connect(url.toString()).cookies(cookies).get();
            //TODO: fb2 required a closing pair for hr tag
            //TODO: remove first hr tag from description
            //TODO: make a specific exception class for parsing when tag not found
            Elements genres, annotation;
            Element bookTitle, author, keywords, lastUpdated, picture, rating, wordcount, status;

            try {
                book.setBookTitle(selectBookTitle(doc));
            } catch (TagNotFoundException e) {
                ex.add(e.getMessage());
            }
            try {
                book.setRating(selectRating(doc));
            } catch (TagNotFoundException e) {
                ex.add(e.getMessage());
            }
            try {
                book.setWordcount(selectWordcount(doc));
            } catch (TagNotFoundException e) {
                ex.add(e.getMessage());
            }
            try {
                book.setStatus(selectStatus(doc));
            } catch (TagNotFoundException e) {
                ex.add(e.getMessage());
            }

            if (!ex.isEmpty()) {
                StringBuilder e = new StringBuilder("There is some fields missing in the input page (");
                for (int i = 0; i < ex.size(); i++) {
                    e.append(ex.get(i));
                    if (i < ex.size() - 1) {
                        e.append(", ");
                    }
                }
                e.append("). You have to choose main page of the story, where description, tags and chapter list is.");
                throw new TagsNotFoundException(e.toString());
            }

            book.addGenres(selectGenres(doc));
            book.setAnnotation(selectAnnotation(doc));

            String coverPageURL = selectPictureURL(doc);
            if (!book.isBinaryExist(coverPageURL)) {
                    String coverPageAsBASE64 = downloadPicture(coverPageURL);
                    book.addBinary(coverPageURL, coverPageAsBASE64);
            }
            Element coverHTML = createXMLTagForImage(coverPageURL);
            book.setCoverpage(coverHTML.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return book;
    }

    private Element createXMLTagForImage(String imageURL) {
        Element coverHTML = new Element("image");
        coverHTML.attr("xlink:href", "#" + imageURL);
        return coverHTML;
    }

    private String selectPictureURL(Document doc) {
        Element picture;
        if ((picture = doc.select("html body .body_container .content .content_background .inner " +
                ".user_blog_post .left .story_container .story_content_box .no_padding .story .story_data " +
                ".right .padding .description .story_image a").first()) != null) {
            return picture.attr("href");
        } else {
            return null;
        }
    }

    private ArrayList<String> selectAnnotation(Document doc) {
        Elements annotation;
        if ((annotation = doc.select("html body .body_container .content .content_background .inner " +
                ".user_blog_post .left .story_container .story_content_box .no_padding .story .story_data .right " +
                ".padding .description p,hr")) != null) {
            trimHR(annotation);
            iterateThroughNodeList(annotation);
            ArrayList<String> annotationStrings = new ArrayList<>();
            for (Element e : annotation) {
                annotationStrings.add(e.toString());
            }
            return annotationStrings;
        } else {
            return null;
        }
    }

    private List<String> selectGenres(Document doc) {
        Elements genres;
        if ((genres = doc.select("html body .body_container .content .content_background .inner " +
                ".user_blog_post .left .story_container .story_content_box .no_padding .story .story_data " +
                ".right .padding .description .story_category")) != null) {
            List<String> genresParsed = new ArrayList<>();
            for (Element e : genres) {
                genresParsed.add(e.childNode(0).toString());
            }
            return genresParsed;
        } else {
            return null;
        }
    }

    private String selectStatus(Document doc) throws TagNotFoundException {
        Element status;
        if ((status = doc.select("html body .body_container .content .content_background .inner " +
                ".user_blog_post .left .story_container .story_content_box .no_padding .story .story_data .right " +
                ".padding [id^=form-chapter-list] .chapters .bottom [class^=completed-status]").first()) != null) {
            return status.attr("title");
        } else {
            throw new TagNotFoundException("[class^=completed-status]");
        }
    }

    private String selectWordcount(Document doc) throws TagNotFoundException {
        Element wordcount;
        if ((wordcount = doc.select("html body .body_container .content .content_background .inner " +
                ".user_blog_post .left .story_container .story_content_box .no_padding .story .story_data .right " +
                ".padding [id^=form-chapter-list] .chapters .bottom .word_count").first()) != null) {
            return wordcount.text();
        } else {
            throw new TagNotFoundException(".word_count");
        }
    }

    private String selectRating(Document doc) throws TagNotFoundException {
        Element rating;
        if ((rating = doc.select("html body .body_container .content .content_background .inner " +
                ".user_blog_post  .left .story_container .story_content_box .no_padding " +
                ".title [class^=content-rating]").first()) != null) {
            return rating.attr("title");
        } else {
            throw new TagNotFoundException("[class^=content-rating]");
        }
    }

    private String selectBookTitle(Document doc) throws TagNotFoundException {
        Element bookTitle;
        if ((bookTitle = doc.select("html body .body_container .content .content_background " +
                ".inner .user_blog_post .left .story_container .story_content_box .no_padding .title " +
                ".resize_text .story_name").first()) != null) {
            return bookTitle.text();
        } else {
            throw new TagNotFoundException(".story_name");
        }
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
            String pictureAsBASE64 = null;
            if (!book.isBinaryExist(pictureURL)) {
                pictureAsBASE64 = downloadPicture(pictureURL);
            }
            book.addBinary(pictureURL, pictureAsBASE64);
            Element image = createXMLTagForImage(pictureURL);
            node.replaceWith(image);
        }
    }

    private String downloadPicture(String pictureURL) {
        String result = null;
        try {
            Connection.Response response = Jsoup.connect(pictureURL).cookies(cookies).
                    ignoreContentType(true).execute();
            byte[] pictureAsBytes = response.bodyAsBytes();
            StringBuilder pictureAsBASE64 = new StringBuilder();
            String parsedBytes = Base64.getEncoder().encodeToString(pictureAsBytes);
            int shift = 0;
            while (parsedBytes.length() > shift + 77) {
                pictureAsBASE64.append(parsedBytes.substring(shift, shift + 77));
                shift += 77;
            }
            pictureAsBASE64.append(parsedBytes.substring(shift));
            result = pictureAsBASE64.toString();
        } catch (IOException e) { //TODO: Make a Exeption class for when picture can't be downloaded
            e.printStackTrace();
        }
        return result;
    }

    private void removeUnnecessaryAttributes(Node node) {
            node.removeAttr("rel");
            node.removeAttr("style");
            node.removeAttr("class");
            node.removeAttr("alt");
    }

    private void trimHR(Elements elements) {
        for (int i = 0; i < elements.size(); i++) {
            if (elements.get(i).nodeName().equals("hr")) {
                elements.remove(i);
                i--;
            } else {
                break;
            }
        }
        for (int i = elements.size()-1; i >= 0; i--) {
            if (elements.get(i).nodeName().equals("hr")) {
                elements.remove(i);
            } else {
                break;
            }
        }
    }

    private void addCookie(String key, String value) {
        cookies.put(key, value);
    }
}

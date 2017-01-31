package org.anon;

import org.anon.Exceptions.TagNotFoundException;
import org.anon.Exceptions.TagsNotFoundException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Parser {

    private HashMap<String, String> cookies = new HashMap<>();
    private Book book = new Book();
    private List<String> ex = new ArrayList<>();
    private String baseURI = null;
    HashSet<String> invalidTags = new HashSet<>(Arrays.asList("i", "b", "center", "hr"));

    Parser() {}

    public Book parseBook(URL url) throws TagsNotFoundException {
        try {
            baseURI = url.getHost();
            Document doc = Jsoup.connect(url.toString()).cookies(cookies).get();
            //TODO: fb2 required a closing pair for hr tag
            //TODO: remove first hr tag from description
            //TODO: make a specific exception class for parsing when tag not found
            Elements genres, annotation;
            Element bookTitle, author, keywords, lastUpdated, picture, rating, wordcount, status;

            /*try {
                book.setBookTitle(selectBookTitle(doc));
            } catch (TagNotFoundException e) {
                ex.add(e.getMessage());
            }*/
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

            //book.addGenres(selectGenres(doc));
            //book.setAnnotation(selectAnnotation(doc));

            /*String coverPageURL = selectPictureURL(doc);
            if (coverPageURL != null) {
                if (!book.isBinaryExist(coverPageURL)) {
                    String coverPageAsBASE64 = downloadPicture(coverPageURL, cookies);
                    book.addBinary(coverPageURL, coverPageAsBASE64);
                }
                Element coverHTML = createXMLTagForImage(coverPageURL);
                book.setCoverpage(coverHTML.toString());
            }*/

            //book.setAuthor(selectAuthor(doc, baseURI));
            book.setKeywords(selectKeywords(doc));
            book.setFirstPublished(getDateOfPublishing(doc, ".date_approved"));
            book.setLastUpdated(getDateOfPublishing(doc, ".last_modified"));
            LinkedHashMap<String, String> chapters = selectChapterList(doc, baseURI);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return book;
    }

    public static <T extends Node> Element createXMLTagForImage(T imgElement) {
        Element image = new Element("image");
        image.attr("xlink:href", "#" + imgElement.attr("src"));
        return image;
    }

    public static Element selectCoverpage(Document doc) {
        Element picture;
        Element result = new Element("coverpage");
        if ((picture = doc.select("html body .body_container .content .content_background .inner " +
                ".user_blog_post .left .story_container .story_content_box .no_padding .story .story_data " +
                ".right .padding .description .story_image a").first()) != null) {
            result.appendChild(new Element("img").attr("src", picture.attr("href")));
        }
        return result;
    }

    public static Element selectAnnotation(Document doc) {
        Elements annotation;
        if ((annotation = doc.select("html body .body_container .content .content_background .inner " +
                ".user_blog_post .left .story_container .story_content_box .no_padding .story .story_data .right " +
                ".padding .description").select("p,hr,center,blockquote")) != null) {
            trimHR(annotation);
            Element result = new Element("annotation");
            for (Element e : annotation) {
                result.appendChild(e);
            }
            return result;
        } else {
            return null;
        }
    }

    public static Elements selectGenres(Document doc) {
        Elements genres;
        if ((genres = doc.select("html body .body_container .content .content_background .inner " +
                ".user_blog_post .left .story_container .story_content_box .no_padding .story .story_data " +
                ".right .padding .description .story_category")) != null) {
            for (Element e : genres) {
                Element genre = new Element("genre");
                genre.text(e.text());
                genres.set(genres.indexOf(e), genre);
            }
            return genres;
        } else {
            return null;
        }
    }

    public static String selectStatus(Document doc) throws TagNotFoundException {
        Element status;
        if ((status = doc.select("html body .body_container .content .content_background .inner " +
                ".user_blog_post .left .story_container .story_content_box .no_padding .story .story_data .right " +
                ".padding [id^=form-chapter-list] .chapters .bottom [class^=completed-status]").first()) != null) {
            return status.attr("title");
        } else {
            throw new TagNotFoundException("[class^=completed-status]");
        }
    }

    public static String selectWordcount(Document doc) throws TagNotFoundException {
        Element wordcount;
        if ((wordcount = doc.select("html body .body_container .content .content_background .inner " +
                ".user_blog_post .left .story_container .story_content_box .no_padding .story .story_data .right " +
                ".padding [id^=form-chapter-list] .chapters .bottom .word_count").first()) != null) {
            return wordcount.text();
        } else {
            throw new TagNotFoundException(".word_count");
        }
    }

    public static String selectRating(Document doc) throws TagNotFoundException {
        Element rating;
        if ((rating = doc.select("html body .body_container .content .content_background .inner " +
                ".user_blog_post  .left .story_container .story_content_box .no_padding " +
                ".title [class^=content-rating]").first()) != null) {
            return rating.attr("title");
        } else {
            throw new TagNotFoundException("[class^=content-rating]");
        }
    }

    public static Element selectBookTitle(Document doc) throws TagNotFoundException {
        Element bookTitle;
        if ((bookTitle = doc.select("html body .body_container .content .content_background " +
                ".inner .user_blog_post .left .story_container .story_content_box .no_padding .title " +
                ".resize_text .story_name").first()) != null) {
            return new Element("book-title").text(bookTitle.text());
        } else {
            throw new TagNotFoundException(".story_name");
        }
    }

    public static Element selectAuthor(Document doc, String baseURI) {
        Element author;
        if ((author = doc.select("html body .body_container .content .content_background .inner " +
                ".user_blog_post  .left .story_container .story_content_box .no_padding .title .resize_text " +
                ".author a").first()) != null) {
            Element parsedAuthor = new Element("author");
            Element nickname = new Element("nickname").text(author.text());
            Element homepage = new Element("home-page").text(baseURI + author.attr("href"));
            parsedAuthor.appendChild(nickname).appendChild(homepage);
            return parsedAuthor;
            //return new Author(author.text(), baseURI + author.attr("href"));
        } else {
            return null;
        }
    }

    public static Element selectKeywords(Document doc) {
        Elements keywords;
        if ((keywords = doc.select("html body .body_container .content .content_background .inner " +
                ".user_blog_post .left .story_container .story_content_box .no_padding .story .extra_story_data " +
                ".inner_data .character_icon").select("a"))!= null) {
            StringBuilder sb = new StringBuilder();
            for (Element e : keywords) {
                sb.append(e.attr("title")).append(", ");
            }
            return new Element("keywords").text(sb.substring(0, sb.length()-2));
        } else {
            return null;
        }
    }

    public static LinkedHashMap<String, String> selectChapterList(Document doc, String baseURI) {
        Elements chapters;
        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        if ((chapters = doc.select("html body .body_container .content .content_background .inner " +
                ".user_blog_post .left .story_container .story_content_box .no_padding .story .story_data .right " +
                ".padding [id^=form-chapter-list] .chapters .chapter_container li .chapter_link")) != null) {
            for (Element chapter : chapters) {
                result.put(chapter.text(), baseURI + chapter.attr("href")); //TODO: add a possibility for user to change protocols
            }
        }
        return result;
    }

    public static Date getDateOfPublishing(Document doc, String classOfTagWithDate) {
        Elements date;
        Date parsedDate = null;
        if ((date = doc.select("html body .body_container .content .content_background .inner " +
                ".user_blog_post .left .story_container .story_content_box .no_padding .story .extra_story_data " +
                ".inner_data " + classOfTagWithDate + " div span")) != null) {
            for (Element e : date) {
                if (e.attr("class").isEmpty()) {
                    DateFormat format = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
                    try {
                        parsedDate = format.parse(e.text().replaceAll("(?:st|nd|rd|th)", ""));
                    } catch (ParseException e1) {
                        e1.printStackTrace(); //TODO: do something
                    }
                }
            }
            return parsedDate;
        } else {
            return null;
        }
    }

    public static Element selectDateOfPublishing(Document doc) {
        Date firstPublished = getDateOfPublishing(doc, ".date_approved");
        Date lastUpdated = getDateOfPublishing(doc, ".last_modified");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Element date = new Element("date");
        if (firstPublished != null && lastUpdated != null) {
            date.attr("value", format.format(firstPublished));
            if (firstPublished.equals(lastUpdated)) {
                date.text(format.format(firstPublished));
            } else {
                date.text(format.format(firstPublished) + " - " + format.format(lastUpdated));
            }
        } else if (firstPublished == null && lastUpdated == null) {
            date.attr("value", "0000-00-00");
        } else if (lastUpdated == null){
            date.attr("value", format.format(firstPublished));
            date.text(format.format(firstPublished));
        } else {
            date.attr("value", format.format(lastUpdated));
            date.text(format.format(lastUpdated));
        }
        return date;
    }

    //process node list. Download all pictures and remove non-standard attributes
    private static void iterateThroughNodeList(List<? extends Node> nodes) {
        for (Node n : nodes) {
            //checkForPicture(n);
            removeUnnecessaryAttributes(n);
            iterateThroughNodeList(n.childNodes());
        }
    }

    //check whether a node contains picture or not.
    // If it contains and this picture is not presented in the book yet - download and add to the book
    private static void checkForPicture(Node node, Book book, HashMap<String, String> cookies) {
        if (node.nodeName().equals("img")) {
            String pictureURL = node.attr("src");
            String pictureAsBASE64 = null;
            if (!book.isBinaryExist(pictureURL)) {
                pictureAsBASE64 = downloadPicture(pictureURL, cookies);
            }
            book.addBinary(pictureURL, pictureAsBASE64);
            Element image = createXMLTagForImage(node);
            node.replaceWith(image);
        }
    }

    //downloads a picture through individual connection and returns it already encoded and formatted in BASE64
    private static String downloadPicture(String pictureURL, HashMap<String, String> cookies) {
        String result = null;
        try {
            Connection.Response response = Jsoup.connect(pictureURL).cookies(cookies).
                    ignoreContentType(true).execute();
            byte[] pictureAsBytes = response.bodyAsBytes();
            StringBuilder pictureAsBASE64 = new StringBuilder();
            String parsedBytes = Base64.getEncoder().encodeToString(pictureAsBytes);
            int shift = 0;
            int chunkLength = 77;
            while (parsedBytes.length() > shift + chunkLength) {
                pictureAsBASE64.append(parsedBytes.substring(shift, shift + chunkLength));
                shift += chunkLength;
            }
            pictureAsBASE64.append(parsedBytes.substring(shift));
            result = pictureAsBASE64.toString();
        } catch (IOException e) { //TODO: Make a Exeption class for when picture can't be downloaded
            e.printStackTrace();
        }
        return result;
    }

    //removes attributes of particular node
    //made for the case when parsed node contains attributes that does not present in FB2 standard
    private static void removeUnnecessaryAttributes(Node node) {
        node.removeAttr("rel");
        node.removeAttr("style");
        node.removeAttr("class");
        node.removeAttr("alt");
        node.removeAttr("double");
    }

    //removes leading and closing whitespaces from Elements.
    //Point of removing is lines before and after main text has no use
    private static void trimHR(Elements elements) {
        while (true) {
            if (elements.get(0).nodeName().equals("hr")) {
                elements.remove(0);
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

    public static Element selectChapterText(Document doc) {
        Elements chapter;
        Element result = new Element("section");
        Element title;
        Element parsedTitle = new Element("title");
        Element p = new Element("p");
        if ((title = doc.select("html body .body_container .content .content_background .inner " +
                "div.user_blog_post.compressed .left .story_container div.story_content_box.chapter_content_box " +
                ".main .chapter #chapter_format .title div[style=\"display:table; width:100%;\"] #chapter_title")
                .first()) != null) {
            p.text(title.text());
            parsedTitle.appendChild(p);
            result.appendChild(parsedTitle);
        }
        if ((chapter = doc.select("html body .body_container .content .content_background .inner " +
                "div.user_blog_post.compressed .left .story_container div.story_content_box.chapter_content_box " +
                ".main .chapter #chapter_format .chapter_content .inner_margin #chapter_container")
                /*.select("p, hr, center, blockquote, span")*/) != null) {
            List<Node> chapterElements = chapter.get(0).childNodes();
            for (int i = 0; i < chapterElements.size(); i++) {
                result.appendChild(chapterElements.get(i));
                i--;
            }
        }
        return result;
    }

    public static <T extends Node> boolean processElement(T node, HashSet<String> imageLinks){
        boolean nodeWasRemoved = false;
        switch (node.nodeName()) {
            case "hr":          node.replaceWith(generateValidNode(node, "empty-line"));
                                nodeWasRemoved = true;
                                break;
            case "i":           node.replaceWith(generateValidNode(node, "emphasis"));
                                nodeWasRemoved = true;
                                break;
            case "b":           node.replaceWith(generateValidNode(node, "strong"));
                                nodeWasRemoved = true;
                                break;
            case "blockquote":  node.replaceWith(generateValidNode(node, "cite"));
                                nodeWasRemoved = true;
                                break;
            case "center":      node.replaceWith(generateValidNode(node, "subtitle"));
                                nodeWasRemoved = true;
                                break;
            case "img":         imageLinks.add(node.attr("src"));
                                node.replaceWith(createXMLTagForImage(node));
                                nodeWasRemoved = true;
                                break;
            case "span":        if (node.attr("style")
                                    .matches("^.*text-decoration: line-through.*$")) {
                                    node.replaceWith(generateValidNode(node, "strike"));
                                } else {
                                    node.unwrap();
                                }
                                nodeWasRemoved = true;
                                break;
            case "div":         node.remove();
                                nodeWasRemoved = true;
                                break;
            default:            removeUnnecessaryAttributes(node);
        }
        for (int i = 0; i < node.childNodes().size(); i++) {
            if (processElement(node.childNode(i), imageLinks)) {
                i--;
            };
        }
        return nodeWasRemoved;
    }

    private static <T extends Node> Node generateValidNode(T invalidNode, String validTag) {
        Element validNode = new Element(validTag);
        List<Node> children = invalidNode.childNodes();
        while (children.size() > 0) {
            validNode.appendChild(children.get(0));
        }
        return validNode;
    }

    private void addCookie(String key, String value) {
        cookies.put(key, value);
    }
}

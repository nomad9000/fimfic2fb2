import com.sun.xml.internal.bind.v2.model.core.EnumLeafInfo;
import org.anon.Author;
import org.anon.Parser;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.parser.Tag;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.junit.Test;

import javax.print.Doc;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;

public class ParserTest {
    @Test
    public void testParseTitle() {
        try {
            Document doc = Jsoup.connect("http://www.fimfiction.net/story/359300/lost-nights").get();
            String bookTitle;
            bookTitle = doc.select(".story_name").first().text();
            assertEquals(bookTitle, "Lost nights");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testParseAuthorName() {
        try {
            Document doc = Jsoup.connect("http://www.fimfiction.net/story/359300/lost-nights").get();
            Element element = doc.select("html body .body_container .content .content_background .inner .user_blog_post  .left .story_container .story_content_box .no_padding .title .resize_text .author a").first();
            String bookTitle;
            bookTitle = doc.select("html body .body_container .content .content_background .inner .user_blog_post  .left .story_container .story_content_box .no_padding .title .resize_text .author a").first().text();
            String authorName = doc.select("html body .body_container .content .content_background .inner .user_blog_post  .left .story_container .story_content_box .no_padding .title .resize_text .author a").first().attr("href");
            System.out.println(authorName);
            URL url = new URL(doc.baseUri());
            String host = url.getHost();
            Author author = new Author(element.text(), host + element.attr("href"));
            assertEquals(bookTitle, "Cherry delight");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void parseKeywords() {
        try {
            Document doc = Jsoup.connect("https://www.fimfiction.net/story/359300/lost-nights").get();
            Elements elements = doc.select("html body .body_container .content .content_background .inner .user_blog_post .left .story_container .story_content_box .no_padding .story .extra_story_data .inner_data .character_icon").next("a");
            StringBuilder sb = new StringBuilder();
            for (Element e : elements) {
                sb.append(e.attr("title")).append(", ");
            }
            String keywords = sb.substring(0, sb.length()-2);
            System.out.println(keywords);
            assertEquals(keywords, "Rainbow Dash, DJ P0N-3, Original Character, Main 6");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testParseGenreList() {
        try {
            Document doc = Jsoup.connect("http://www.fimfiction.net/story/359300/lost-nights").get();
            String bookTitle;
            bookTitle = "";
            Elements elements = doc.select("html body .body_container .content .content_background .inner .user_blog_post .left .story_container .story_content_box .no_padding .story .story_data .right .padding .description .story_category");
            for (Element e : elements) {
                System.out.println(e.childNode(0));
            }
            assertEquals(bookTitle, "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testParseRating() {
        try {
            Document doc = Jsoup.connect("http://www.fimfiction.net/story/359300/lost-nights").get();
            String rating;
            rating = doc.select("html body .body_container .content .content_background .inner .user_blog_post  .left .story_container .story_content_box .no_padding .title [class^=content-rating]").first().attr("title");
            System.out.println(rating);
            assertEquals(rating, "Rated for everyone");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testParseStatus() {
        try {
            Document doc = Jsoup.connect("http://www.fimfiction.net/story/359300/lost-nights").get();
            String rating;
            rating = doc.select("html body .body_container .content .content_background .inner .user_blog_post .left .story_container .story_content_box .no_padding .story .story_data .right .padding [id^=form-chapter-list] .chapters .bottom [class^=completed-status]").first().attr("title");
            System.out.println(rating);
            assertEquals(rating, "Incomplete");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testParseWordcount() {
        try {
            Document doc = Jsoup.connect("http://www.fimfiction.net/story/359300/lost-nights").get();
            String rating;
            rating = doc.select("html body .body_container .content .content_background .inner .user_blog_post .left .story_container .story_content_box .no_padding .story .story_data .right .padding [id^=form-chapter-list] .chapters .bottom .word_count").first().text();
            System.out.println(rating);
            assertEquals(rating, "2,960 words total");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testParseDescription() {
        try {
            //Document doc = Jsoup.connect("https://www.fimfiction.net/story/360070/going-native").cookie("view_mature", "true").get();
            //Document doc = Jsoup.connect("https://www.fimfiction.net/story/314264/hybrid").cookie("view_mature", "true").get();
            //Document doc = Jsoup.connect("https://www.fimfiction.net/story/359022/sunset-shimmer-discovers-bubble-wrap").cookie("view_mature", "true").get();
            //Document doc = Jsoup.connect("https://www.fimfiction.net/story/265629/the-last-pony-on-earth").cookie("view_mature", "true").get();
            Document doc = Jsoup.connect("https://www.fimfiction.net/story/360437/a-portal-to-equestria").cookie("view_mature", "true").get();
            doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);

 /*           File file = new File("C:\\InvisibleChapter.htm");
            StringBuilder sb = new StringBuilder();
            try {
                Scanner scanner = new Scanner(file);
                while (scanner.hasNext()) {
                    sb.append(scanner.next());
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Document doc = Jsoup.parse(sb.toString());*/

            Elements description;
            description = doc.select("html body .body_container .content .content_background .inner .user_blog_post .left .story_container .story_content_box .no_padding .story .story_data .right .padding .description p,hr");
            Element el = new Element("temp");
            for (Element e : description) {
                el.appendChild(e);
            }
            cleanNodeOfAttributes(el);
            trimHR(description);
            //cleanNodeOfAttributes(description);
            Document d = org.jsoup.parser.Parser.parse(el.html(), el.baseUri());
            d.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
            System.out.println(d.body().children());
            assertEquals(d.head().val(), "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

/*    private void cleanNodeOfAttributes(List<? extends Node> nodes) {
        for (Node n : nodes) {
            if (n.toString().equals("<hr>")) {
                n.replaceWith(new TextNode("<hr></hr>", n.baseUri()));
            }
        }*/

    @Test
    public void testCoverpageParsing() {
        try {
            Document doc = Jsoup.connect("https://www.fimfiction.net/story/360437/a-portal-to-equestria").cookie("view_mature", "true").get();
            Elements coverpage = doc.select("html body .body_container .content .content_background .inner .user_blog_post .left .story_container .story_content_box .no_padding .story .story_data .right .padding .description .story_image a");
            String image = getPictureAsBASE64(coverpage.attr("href"));
            int shift = 0;
            while (image.length() > shift + 77) {
                System.out.println(image.substring(shift, shift + 77));
                shift += 77;
            }
            System.out.println(image.substring(shift));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private <T extends Node> void cleanNodeOfAttributes(T node) {
        if (node.nodeName().equals("img")) {
            String parsedImg = getPictureAsBASE64(node.attr("src"));
            System.out.println(parsedImg);
        }
        if (node.hasAttr("href")) {
            node.attr("xlink:href", node.attr("href"));
            node.removeAttr("href");
        }
        node.removeAttr("rel");
        node.removeAttr("style");
        node.removeAttr("class");
        node.removeAttr("double");
        List<Node> childNodes = node.childNodes();
        for (Node n : childNodes) {
            cleanNodeOfAttributes(n);
        }
    }

    private String getPictureAsBASE64(String imageLocation) {
        String result = null;
        try {
            Connection.Response resultImageResponse = Jsoup.connect(imageLocation).cookie("view_mature", "true").ignoreContentType(true).execute();
            byte[] bytesResult = resultImageResponse.bodyAsBytes();
            result = Base64.getEncoder().encodeToString(bytesResult);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
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
}

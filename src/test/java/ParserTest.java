import com.sun.xml.internal.bind.v2.model.core.EnumLeafInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;

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
            String bookTitle;
            bookTitle = doc.select("html body .body_container .content .content_background .inner .user_blog_post  .left .story_container .story_content_box .no_padding .title .resize_text .author a").first().text();
            String authorName = doc.select("html body .body_container .content .content_background .inner .user_blog_post  .left .story_container .story_content_box .no_padding .title .resize_text .author a").first().attr("href");
            System.out.println(authorName);
            assertEquals(bookTitle, "Cherry delight");
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
}

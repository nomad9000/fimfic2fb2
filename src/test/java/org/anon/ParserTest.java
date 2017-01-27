package org.anon;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.BeforeClass;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.*;

public class ParserTest {

    private static Document doc = null;
    private static String baseURI = "www.fimfiction.net";

    @BeforeClass
    public static void createEnvironment() throws Exception {
        doc = Jsoup.connect("https://www.fimfiction.net/story/68356/pegasus-device").cookie("view_mature", "true").get();
    }

    @Test
    public void createXMLTagForImage() throws Exception {

    }

    @Test
    public void selectPictureURL() throws Exception {

    }

    @Test
    public void selectAnnotation() throws Exception {

    }

    @Test
    public void selectGenres() throws Exception {
        Elements genres = Parser.selectGenres(doc);
        ArrayList<String> actualGenres = new ArrayList<>(Arrays.asList("Gore", "Alternate Universe", "Dark", "Slice of Life"));
        ArrayList<String> parsedGenres = new ArrayList<>();
        for (Element e : genres) {
            parsedGenres.add(e.text());
        }
        assertEquals("Genres are wrong", parsedGenres, actualGenres);
    }

    @Test
    public void selectStatus() throws Exception {
        assertEquals("Status is wrong", Parser.selectStatus(doc), "Complete");
    }

    @Test
    public void selectWordcount() throws Exception {
        assertEquals("Wordcount is wrong", Parser.selectWordcount(doc), "53,500 words total");
    }

    @Test
    public void selectRating() throws Exception {
        assertEquals("Rating is wrong", Parser.selectRating(doc), "Rated for teens ( 13+ )");
    }

    @Test
    public void selectBookTitle() throws Exception {
        assertEquals("Title is wrong", Parser.selectBookTitle(doc).text(), "Pegasus Device");
    }

    @Test
    public void selectAuthor() throws Exception {
        Element author = Parser.selectAuthor(doc, baseURI);
        assertEquals("Author nickname is wrong", author.child(0).text(),"AuroraDawn");
        assertEquals("Author homepage is wrong", author.child(1).text(),"www.fimfiction.net/user/AuroraDawn");
    }

    @Test
    public void selectKeywords() throws Exception {
        assertEquals("Keywords are wrong", Parser.selectKeywords(doc).text(), "Rainbow Dash, Original Character");
    }

    @Test
    public void selectChapterList() throws Exception {

    }

    @Test
    public void selectDateOfPublishing() throws Exception {
        assertEquals("Date value is wrong", Parser.selectDateOfPublishing(doc).attr("value"), "2012-12-07");
        assertEquals("Date text is wrong", Parser.selectDateOfPublishing(doc).text(), "2012-12-07 - 2013-02-17");
    }

    @Test
    public void parseBook() throws Exception {

    }

}
package org.anon;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class ParserTest {

    private static Document pegasusDevice = null;
    private static Document pegasusDeviceCh01 = null;
    private static String baseURI = "www.fimfiction.net";

    @BeforeClass
    public static void createEnvironment() throws Exception {
        //pegasusDevice = Jsoup.connect("https://www.fimfiction.net/story/68356/pegasus-device").cookie("view_mature", "true").get();
        pegasusDeviceCh01 = Jsoup.connect("https://www.fimfiction.net/story/68356/1/pegasus-device/chapter-one").cookie("view_mature", "true").get();
    }

    @Test
    public void createXMLTagForImage() throws Exception {
        Element picture = Parser.selectCoverpage(pegasusDevice);
        picture = Parser.createXMLTagForImage(picture.child(0));
        assertEquals("Image XML tag is wrong", picture.attr("xlink:href"), "#https://cdn-img.fimfiction.net/story/o7d7-1432459327-68356-full");
    }

    @Test
    public void selectCoverpage() throws Exception {
        Element picture = Parser.selectCoverpage(pegasusDevice);
        assertEquals("Coverpage is wrong", picture.child(0).attr("src"), "https://cdn-img.fimfiction.net/story/o7d7-1432459327-68356-full");
    }

    @Test
    public void selectAnnotation() throws Exception {
        Element annotation = Parser.selectAnnotation(pegasusDevice);
        assertEquals("Annotation is wrong", annotation.child(0).text().split(" ")[0], "Cloudsdale");
    }

    @Test
    public void selectGenres() throws Exception {
        Elements genres = Parser.selectGenres(pegasusDevice);
        ArrayList<String> actualGenres = new ArrayList<>(Arrays.asList("Gore", "Alternate Universe", "Dark", "Slice of Life"));
        ArrayList<String> parsedGenres = new ArrayList<>();
        for (Element e : genres) {
            parsedGenres.add(e.text());
        }
        assertEquals("Genres are wrong", parsedGenres, actualGenres);
    }

    @Test
    public void selectStatus() throws Exception {
        assertEquals("Status is wrong", Parser.selectStatus(pegasusDevice), "Complete");
    }

    @Test
    public void selectWordcount() throws Exception {
        assertEquals("Wordcount is wrong", Parser.selectWordcount(pegasusDevice), "53,500 words total");
    }

    @Test
    public void selectRating() throws Exception {
        assertEquals("Rating is wrong", Parser.selectRating(pegasusDevice), "Rated for teens ( 13+ )");
    }

    @Test
    public void selectBookTitle() throws Exception {
        assertEquals("Title is wrong", Parser.selectBookTitle(pegasusDevice).text(), "Pegasus Device");
    }

    @Test
    public void selectAuthor() throws Exception {
        Element author = Parser.selectAuthor(pegasusDevice, baseURI);
        assertEquals("Author nickname is wrong", author.child(0).text(),"AuroraDawn");
        assertEquals("Author homepage is wrong", author.child(1).text(),"www.fimfiction.net/user/AuroraDawn");
    }

    @Test
    public void selectKeywords() throws Exception {
        assertEquals("Keywords are wrong", Parser.selectKeywords(pegasusDevice).text(), "Rainbow Dash, Original Character");
    }

    @Test
    public void selectChapterList() throws Exception {

    }

    @Test
    public void selectDateOfPublishing() throws Exception {
        assertEquals("Date value is wrong", Parser.selectDateOfPublishing(pegasusDevice).attr("value"), "2012-12-07");
        assertEquals("Date text is wrong", Parser.selectDateOfPublishing(pegasusDevice).text(), "2012-12-07 - 2013-02-17");
    }

    @Test
    public void parseBook() throws Exception {

    }

    @Test
    public void selectChapterText() throws Exception {
        Element chapter = Parser.selectChapterText(pegasusDeviceCh01);
        System.out.println(chapter);
    }

}
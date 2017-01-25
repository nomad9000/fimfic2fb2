package org.anon;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Book {

    //content of title-info tag
    private List<String> genres;
    private Author author;
    private String bookTitle;
    private ArrayList<String> annotation; //any real text blocks which is able to contain tags <p>, <title>, <image> and other should be collections with this lines already formatted in FB2
    private String keywords;
    private Date firstPublished;
    private Date lastUpdated; //content of tag <date>. Parameter "value" will be parsed from here
    private String coverpage; //FB2 tag with link to binary with coverpage. Page itself should be already parsed and stored within binaries hashmap
    private String lang = "en"; //Fanfics in any onther language is not allowed on FimFiction

    //content of document-info tag
    private String programmUsed;
    private String rippedDate; //date when book was ripped. Taken from current date
    private URL srcURL;

    //content of custom-info tag
    private String rating;
    private String wordcount;
    private String status;

    //content of body tag (chapters)
    private ArrayList<Chapter> chapters = new ArrayList<>();

    //content of binaries
    private HashMap<String, String> binaries = new HashMap<>(); //pictures in BASE64 encoding. Key - picture name, value - picture itself

    Book() {
        programmUsed = Context.getInstance().getNameAndVersion();
        genres = new ArrayList<>();
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public void addGenres(List<String> genres) {
        this.genres.addAll(genres);
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public ArrayList<String> getAnnotation() {
        return annotation;
    }

    public void setAnnotation(ArrayList<String> annotation) {
        this.annotation = annotation;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getCoverpage() {
        return coverpage;
    }

    public void setCoverpage(String coverpage) {
        this.coverpage = coverpage;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getProgrammUsed() {
        return programmUsed;
    }

    public void setProgrammUsed(String programmUsed) {
        this.programmUsed = programmUsed;
    }

    public String getRippedDate() {
        return rippedDate;
    }

    public void setRippedDate(String rippedDate) {
        this.rippedDate = rippedDate;
    }

    public URL getSrcURL() {
        return srcURL;
    }

    public void setSrcURL(URL srcURL) {
        this.srcURL = srcURL;
    }

    public ArrayList<Chapter> getChapters() {
        return chapters;
    }

    public void setChapters(ArrayList<Chapter> chapters) {
        this.chapters = chapters;
    }

    public HashMap<String, String> getBinaries() {
        return binaries;
    }

    public void setBinaries(HashMap<String, String> binaries) {
        this.binaries = binaries;
    }

    public void addBinary(String pictureURL, String pictureAsBASE64) {
        binaries.put(pictureURL, pictureAsBASE64);
    }

    public boolean isBinaryExist(String URL) {
        return binaries.containsKey(URL);
    }

    public String getWordcount() {
        return wordcount;
    }

    public void setWordcount(String wordcount) {
        this.wordcount = wordcount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public Date getFirstPublished() {
        return firstPublished;
    }

    public void setFirstPublished(Date firstPublished) {
        this.firstPublished = firstPublished;
    }
}

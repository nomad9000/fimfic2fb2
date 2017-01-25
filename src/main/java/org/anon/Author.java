package org.anon;

import java.net.URL;

public class Author {

    private String nickname;
    private String homePage;

    public Author() {}

    public Author(String nickname, String homePage) {
        this.nickname = nickname;
        this.homePage = homePage;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }
}

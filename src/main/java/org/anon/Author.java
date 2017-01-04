package org.anon;

import java.net.URL;

public class Author {

    private String nickname;
    private URL homePage;

    Author() {}

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public URL getHomePage() {
        return homePage;
    }

    public void setHomePage(URL homePage) {
        this.homePage = homePage;
    }
}

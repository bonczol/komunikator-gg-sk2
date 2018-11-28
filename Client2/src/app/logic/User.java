package app.logic;

import javafx.scene.image.Image;

import java.util.ArrayList;

public class User {
    private String nickname;
    private String login;
    private String description;
    private boolean online;
    Image avatar;

    public User(String nickname, String login, String description, boolean online, Image avatar) {
        this.nickname = nickname;
        this.login = login;
        this.description = description;
        this.online = online;
        this.avatar = avatar;
    }
}

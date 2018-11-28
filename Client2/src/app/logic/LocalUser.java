package app.logic;

import javafx.scene.image.Image;

import java.util.ArrayList;

public class LocalUser extends User {
    private ArrayList<User> friends;

    public LocalUser(String nickname, String login, String description, boolean online, Image avatar, ArrayList<User> friends) {
        super(nickname, login, description, online, avatar);
        this.friends = friends;
    }
}

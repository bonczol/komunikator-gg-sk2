package app.logic;


import java.util.ArrayList;

public class LocalUser extends User {
    private ArrayList<User> friends;

    public LocalUser(String nickname, String login, String description, boolean online, ArrayList<User> friends) {
        super(nickname, login, description, online);
        this.friends = friends;
    }

    public ArrayList<User> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<User> friends) {
        this.friends = friends;
    }
}

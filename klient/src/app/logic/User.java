package app.logic;


public class User {
    private String nickname;
    private String login;
    private String description;
    private boolean online;

    public User(String nickname, String login, String description, boolean online) {
        this.nickname = nickname;
        this.login = login;
        this.description = description;
        this.online = online;
    }
}

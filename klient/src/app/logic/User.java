package app.logic;


public class User {
    protected String nickname;
    protected String login;
    protected boolean online;
    protected String description;

    public User(String nickname, String login, String description, boolean online) {
        this.nickname = nickname;
        this.login = login;
        this.description = description;
        this.online = online;
    }

    public String getNickname() {
        return nickname;
    }

    public String getLogin() {
        return login;
    }

    public String getDescription() {
        return description;
    }

    public boolean isOnline() {
        return online;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

package app.logic;


import java.time.LocalDate;
import java.time.LocalTime;

public class Message {
    private User author;
    private String date;
    private String time;
    private String text;

    public Message(User author, String date, String time, String text) {
        this.author = author;
        this.date = date;
        this.time = time;
        this.text = text;
    }

    public Message(String text) {
        this.author = Client.getClient().getUser();
        this.date = LocalDate.now().toString();
        this.time = LocalTime.now().toString();
        this.text = text;
    }

    public User getAuthor() {
        return author;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getText() {
        return text;
    }
}

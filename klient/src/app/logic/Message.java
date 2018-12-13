package app.logic;


import java.time.LocalDate;
import java.time.LocalTime;

public class Message {
    private User author;
    private String time;
    private String date;
    private String text;

    public Message(User author, String time, String date, String text) {
        this.author = author;
        this.time = time;
        this.date = date;
        this.text = text;
    }

    public Message(String text) {
        this.author = Client.getClient().getUser();
        this.text = text;
        this.date = LocalDate.now().toString();
        this.date = LocalTime.now().toString();
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

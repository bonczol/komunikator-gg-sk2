package app.logic;

import java.time.LocalDateTime;
import java.util.Date;

public class Message {
    private User author;
    private LocalDateTime date;
    private String text;

    public Message(String text) {
        this.date = LocalDateTime.now();
        this.text = text;
    }

}

package app.logic;

import java.util.ArrayList;


public class Conversation {
    private String id;
    private ArrayList<User> members;
    private ArrayList<Message> messages;

    public Conversation(String id, ArrayList<User> members, ArrayList<Message> messages) {
        this.id = id;
        this.members = members;
        this.messages = messages;
    }

    public Conversation(ArrayList<User> members){
        this.id = null;
        this.members = members;
        this.messages = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public ArrayList<User> getMembers() {
        return members;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

}

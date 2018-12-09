package app.logic;

import java.util.ArrayList;
import java.util.HashSet;

public class Conversation {
    private String id;
    private HashSet<User> members;
    private ArrayList<Message> messages;

    private Conversation(String id) {
        this.id = id;
        messages = new ArrayList<>();
    }

    public HashSet<User> getMembers() {
        return members;
    }

    public static class ConversationBuilder{
        //required
        private String id;

        //optional
        private HashSet<User> members;
        private ArrayList<Message> messages;

        public ConversationBuilder(String id){
            this.id = id;
        }

        public ConversationBuilder setMembers(HashSet<User> members) {
            this.members = members;
            return this;
        }

        public ConversationBuilder setMessages(ArrayList<Message> messages) {
            this.messages = messages;
            return this;
        }
    }
}

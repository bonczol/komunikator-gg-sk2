package app.logic;


import java.util.ArrayList;

public class LocalUser extends User {
    private ArrayList<User> friends;
    private ArrayList<Conversation> conversations;

    public LocalUser(String nickname, String login, String description, boolean online, ArrayList<User> friends) {
        super(nickname, login, description, online);
        this.friends = friends;
    }

    public void addFriend(User friend){
        friends.add(friend);
    }

    public void addConversation(Conversation conversation){
        conversations.add(conversation);
    }

    public ArrayList<Conversation> getConversations() {
        return conversations;
    }

    public ArrayList<User> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<User> friends) {
        this.friends = friends;
    }


}

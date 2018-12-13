package app.logic;


import app.view.ChatController;

import java.util.ArrayList;

public class LocalUser extends User {
    private ArrayList<User> friends;
    private ArrayList<Conversation> conversations;

    public LocalUser(String nickname, String login, String description, boolean online, ArrayList<User> friends) {
        super(nickname, login, description, online);
        this.friends = friends;
    }

    public void addConversation(Conversation conversation){
        Client.getClient().getSender().sendGetHistoryMessage(conversation.getId());
        conversations.add(conversation);
        ViewMenager.menuController.showChatWindow(conversation);
    }

    public ArrayList<User> getAllFriendsByLogin(String[] logins){
        ArrayList<User> friends = new ArrayList<>();
        for(String l : logins){
            for(User u : this.friends){
                if(l.equals(u.login)){
                    friends.add(u);
                }
            }
        }
        return friends;
    }

    public Conversation getConversationById(String id_conv){
        for (Conversation c: conversations){
            if(c.getId().equals(id_conv))
                return c;
        }
        return null;
    }

    public void addFriend(User friend){
        friends.add(friend);
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

    public User getFriendByLogin(String login) {
        for (User u : this.friends) {
            if (login.equals(u.login)) {
                return u;
            }
        }
        return null;
    }





}

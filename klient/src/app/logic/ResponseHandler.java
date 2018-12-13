package app.logic;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResponseHandler {
    private String[] response;
    private static final Logger LOG = Logger.getLogger(ResponseHandler.class.getName());

    public ResponseHandler(String[] response) {
        this.response = response;
    }

    public void handleResponse() {
        switch (Integer.valueOf(response[0])) {
            case 400:
                catchSendMessageResp();
                break;
            case 401:
                catchSignInResp();
                break;
            case 402:
                catchSignOutResp();
                break;
            case 403:
                catchSignUpResp();
                break;
            case 404:
                catchSearchForUserResp();
                break;
            case 405:
                catchAddFriendResp();
                break;
            case 407:
                catchGetMessageHistoryResp();
                break;
            case 408:
                catchNewConversationResp();
                break;
            case 409:
                catchChangeDescResp();
                break;
            case 410:
                catchDeleteFriendResp();
                break;
        }
    }

    // response: 400|0 or 400|1
    private void catchSendMessageResp(){
        if(response[1].equals("0"))
            LOG.log(Level.INFO, "Server: fail - send message");
        else
            LOG.log(Level.INFO, "Server: success - send message");
    }

    // response: 401|0 or 401|nick|description|nick1,login1,description1,logged_in1,nick2,login2,description2,logged_in2...
    private void catchSignInResp(){
        if(response[1].equals("0")){
            ViewMenager.logInController.accessDenied();
            LOG.log(Level.INFO, "Server: fail - sign in");
        }
        else {
            ArrayList<User> friends = new ArrayList<>();

            if(!response[3].equals(" ")){
                String []sFriends = response[3].split(",");
                for(int i = 0; i < sFriends.length; i = i+4)
                    friends.add(new User(sFriends[i],sFriends[i+1], sFriends[i+2], sFriends[i + 3].equals("1")));
            }
            ViewMenager.logInController.accessGranted(response[1],response[2], friends);
            LOG.log(Level.INFO, "Server: success - sign in");
        }
    }

    // response: 402|0 lub 402|1
    private void catchSignOutResp(){
        if(response[1].equals("0"))
            LOG.log(Level.INFO, "Server: fail - sign out");
        else
            LOG.log(Level.INFO, "Server: success - sign out");
    }

    // response: 403|0 lub 403|1
    private void catchSignUpResp(){
        if(response[1].equals("0"))
            LOG.log(Level.INFO, "Server: fail - sign up");
        else
            LOG.log(Level.INFO, "Server: success - sign up");
    }

    // response: 404|0 lub 404|nick1,login1,nick2,login2...
    private void catchSearchForUserResp(){
        switch (response[1]) {
            case "0":
                LOG.log(Level.INFO, "Server: fail - search for user - error");
                break;
            case " ":
                LOG.log(Level.INFO, "Server: fail- search for user - user not found");
                break;
            default:
                ViewMenager.addFriendController.showSearchResults(response[1].split(","));
                LOG.log(Level.INFO, "Server: success - search for user - error");
                break;
        }
    }

    // response: 405|0 lub 405|nick|login|description|logged_in
    private void catchAddFriendResp(){
        if(response[1].equals("0"))
            LOG.log(Level.INFO, "Server: fail - add friend");
        else{
            Client.getClient().getUser().addFriend(new User(response[1], response[2], response[3], response[4].equals("1")));
            ViewMenager.menuController.refreshFriendsList();
            LOG.log(Level.INFO, "Server: success - add friend");
        }
    }

    // response: 407|0 lub 407|ID_conv|login1,data1,godzina1,wiadomosc1,login2,...
    private void catchGetMessageHistoryResp(){
        if(response[1].equals("0"))
            LOG.log(Level.INFO, "Server: fail - get message history");
        else{
            String[] m =  response[2].split(",");
            ArrayList<Message> messages = new ArrayList<>();
            User user;

            for(int i = 0; i < m.length; i += 4){
                if(m[i].equals(Client.getClient().getUser().getLogin()))
                    user = Client.getClient().getUser();
                else
                    user = Client.getClient().getUser().getFriendByLogin(m[i]);

                messages.add(new Message(user, m[i+1],m[i+2],m[i+3]));
            }
            Client.getClient().getUser().getConversationById(response[1]).setMessages(messages);
            LOG.log(Level.INFO, "Server: success - get message history");
        }
    }

    // response: 408|0 lub 408|ID_conv
    private void catchNewConversationResp(){
        if(response[1].equals("0"))
            LOG.log(Level.INFO, "Server: fail - new conversation");
        else{
            Client.getClient().getUser().addConversation(
                    new Conversation(response[1],
                            Client.getClient().getUser().getAllFriendsByLogin(response[2].split(","))));
            LOG.log(Level.INFO, "Server: success - new conversation");
        }
    }

    // response: 409|0 lub 409|1
    private void catchChangeDescResp(){
        if(response[1].equals("0"))
            LOG.log(Level.INFO, "Server: fail - change description");
        else{
            LOG.log(Level.INFO, "Server: success - change description");
        }
    }

    // response: 410|0 lub 410|1
    private void catchDeleteFriendResp(){
        if(response[1].equals("0"))
            LOG.log(Level.INFO, "Server: fail - delete friend");
        else{
            LOG.log(Level.INFO, "Server: success - delete friend");
        }
    }

}

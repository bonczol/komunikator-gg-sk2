package app.logic;

import app.view.ChatController;
import javafx.application.Platform;

import javax.swing.text.View;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResponseHandler {
    private String[] response;
    private LocalUser localUser;
    private static final Logger LOG = Logger.getLogger(ResponseHandler.class.getName());

    public ResponseHandler(String[] response, LocalUser localUser) {
        this.response = response;
        this.localUser = localUser;
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
            case 500:
                catchReceivedMassage();
                break;
            case 501:
                catchChangedStatus();
                break;
            case 502:
                catchConvCreated();
                break;
            case 600:
                catchServerOffline();
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
                ViewMenager.addFriendController.clearUsersList();
                break;
            default:
                ViewMenager.addFriendController.showSearchResults(response[1].split(","));
                LOG.log(Level.INFO, "Server: success - user found");
                break;
        }
    }

    // response: 405|0 lub 405|nick|login|description|logged_in
    private void catchAddFriendResp(){
        if(response[1].equals("0"))
            LOG.log(Level.INFO, "Server: fail - add friend");
        else{
            localUser.addFriend(new User(response[1], response[2], response[3], response[4].equals("1")));
            ViewMenager.menuController.refreshFriendsList();
            LOG.log(Level.INFO, "Server: success - add friend");
        }
    }

    // response: 407|0 lub 407|ID_conv|login1,login2|login1,data1,godzina1,wiadomosc1,login2,...
    private void catchGetMessageHistoryResp() {
        if (response[1].equals("0"))
            LOG.log(Level.INFO, "Server: fail - get message history");
        else {
            ArrayList<Message> messages = new ArrayList<>();
            if (!response[3].equals(" "))
                for (int j = 3; j < response.length; j++)
                    messages.add(readMessage(response[j]));

            localUser.getConversations().add(createNewConv(response[1], response[2].split(","), messages));
        }
        LOG.log(Level.INFO, "Server: success - get message history");
    }

    // response: 408|0 lub 408|ID_conv
    private void catchNewConversationResp(){
        if(response[1].equals("0"))
            LOG.log(Level.INFO, "Server: fail - new conversation");
        else{
            Conversation newConversation = createNewConv(response[1], response[2].split(","), new ArrayList<>());
            localUser.getConversations().add(newConversation);
            ViewMenager.menuController.showChatWindow(newConversation);
            LOG.log(Level.INFO, "Server: success - new conversation");
        }
    }

    //  create new conversation when you have only string logins, not objects
    private Conversation createNewConv(String convId, String[] logins, ArrayList<Message> messages){
        ArrayList<User> users = new ArrayList<>(localUser.getAllFriendsByLogin(response[2].split(",")));
        users.add(localUser);
        return new Conversation(convId, users, messages);
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
        else
            LOG.log(Level.INFO, "Server: success - delete friend");
    }

    // response: 500|ID_conv|login,data,godzina,wiadomosc
    private void catchReceivedMassage(){
        Conversation conv =  localUser.getConversationById(response[1]);
        conv.getMessages().add(readMessage(response[2]));
        ChatController controller = ViewMenager.getChatContr(conv);
        if(controller != null)
            controller.refreshConversation();
    }

    // response: 501|login|description|logged_in
    private void catchChangedStatus(){
        User user = localUser.getFriendByLogin(response[1]);
        user.setDescription(response[2]);
        user.setOnline(response[3].equals("1"));
        ViewMenager.menuController.refreshFriendsList();
        LOG.log(Level.INFO, "Server: success - user changed status");
    }


    // response: 502|login|nick|description|logged_in|ID_conv
    private void catchConvCreated(){
        Conversation newConversation = createNewConv(response[1], response[2].split(","), new ArrayList<>());
        localUser.getConversations().add(newConversation);
        LOG.log(Level.INFO, "Server: friend created conversation with you");
    }


    // response: 600|FIN
    private void catchServerOffline(){
        Client.getClient().setConnected(false);
        Platform.runLater(()->ViewMenager.menuController.connectionLost());
        LOG.log(Level.INFO, "Server: offline");
    }


    // Read message in format "login,data,godzina,wiadomosc"
    private Message readMessage(String message){
        String[] m = message.split(",");
        User user;

        if (m[0].equals(Client.getClient().getUser().getLogin()))
            user = localUser;
        else
            user = localUser.getFriendByLogin(m[0]);

        return new Message(user, m[1], m[2], m[3]);
    }

}

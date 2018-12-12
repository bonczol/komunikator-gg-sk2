package app.logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Receiver implements Runnable{
    private BufferedReader reader;
    private volatile boolean running;

    public Receiver(Socket socket) throws IOException {
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.running = true;
    }

    @Override
    public void run() {
        String message;
        String[] sMessage;

        while(running){
            try {
                message = reader.readLine();
                System.out.println("---> Recived: " + message);
                sMessage = message.split(Pattern.quote("|"));

                switch (Integer.valueOf(sMessage[0])) {
                    case 400:
                            catchSendMessageResp(sMessage);
                    case 401:
                            catchSignInResp(sMessage);
                            break;
                    case 402:
                        catchSignOutResp(sMessage);
                    case 403:
                        if(sMessage[1].equals("0"))
                            System.out.println("x");
                        else
                            System.out.println("x");
                        break;
                    case 404:
                            catchSearchForUserResp(sMessage);
                        break;
                    case 405:
                            catchAddFriendResp(sMessage);
                        break;
                    case 406:
                        if(sMessage[1].equals("0"))
                            System.out.println("x");
                        else
                            System.out.println("x");
                        break;
                    case 407:
                        catchGetMessageHistoryResp(sMessage);
                        break;
                    case 408:
                        catchNewConversationResp(sMessage);
                        break;
                    case 409:
                        catchNewConversationResp(sMessage);
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // response: 400|0 or 400|1
    public void catchSendMessageResp(String[] sMessage){
        if(sMessage[1].equals("0"))
            System.out.println("Fail: send message error");
        else
            System.out.println("Succes: Message sent!");
    }

    // response: 401|0 or 401|nick|description|nick1,login1,description1,logged_in1,nick2,login2,description2,logged_in2...
    private void catchSignInResp(String[] sMessage){
        if(sMessage[1].equals("0"))
            ViewMenager.loggInController.accessDenied();
        else {
            ArrayList<User> friends = new ArrayList<>();

            if(!sMessage[3].equals(" ")){
                String []sFriends = sMessage[3].split(",");
                for(int i = 0; i < sFriends.length; i = i+4)
                    friends.add(new User(sFriends[i],sFriends[i+1], sFriends[i+2], sFriends[i + 3].equals("1")));
            }
            ViewMenager.loggInController.accessGranted(sMessage[1],sMessage[2], friends);
        }
    }

    // response: 402|0 lub 402|1
    public void catchSignOutResp(String[] sMessage){
        if(sMessage[1].equals("0"))
            System.out.println("Fail: sign out error");
        else
            System.out.println("Succes: your!");
    }

    // response: 403|0 lub 403|1
    public void catchSignUpResp(String[] sMessage){
        if(sMessage[1].equals("0"))
            System.out.println("Fail: unable to create account");
        else
            System.out.println("Succes: Account created!");
    }

    // response: 404|0 lub 404|nick1,login1,nick2,login2...
    private void catchSearchForUserResp(String[] sMessage){
        switch (sMessage[1]) {
            case "0":
                System.out.println("Fail: User not logged in");
                break;
            case " ":
                System.out.println("Fail: User not found");
                break;
            default:
                ViewMenager.addFriendController.showSearchResults(sMessage[1].split(","));
                System.out.println("Success: User " + sMessage[1] + " found!");
                break;
        }
    }

    // response: 405|0 lub 405|nick|login|description|logged_in
    private void catchAddFriendResp(String[] sMessage){
        if(sMessage[1].equals("0"))
            System.out.println("Fail: User not added");
        else{
            Client.getClient().getUser().addFriend(new User(sMessage[1], sMessage[2], sMessage[3], sMessage[4].equals("1")));
            ViewMenager.menuController.refreshFriendsList();
        }
    }

    // response: 407|0 lub 407|ID_conv|login1,data1,godzina1,wiadomosc1,login2,...
    private void catchGetMessageHistoryResp(String[] sMessage){
        if(sMessage[1].equals("0"))
            System.out.println("Fail: can't get message history");
        else{
            String[] m =  sMessage[2].split(",");
            ArrayList<Message> messages = new ArrayList<>();
            User user;

            for(int i = 0; i < m.length; i += 4){
                if(m[i].equals(Client.getClient().getUser().getLogin()))
                    user = Client.getClient().getUser();
                else
                    user = Client.getClient().getUser().getFriendByLogin(m[i]);

                messages.add(new Message(user, m[i+1],m[i+2],m[i+3]));
            }
            Client.getClient().getUser().getConversationById(sMessage[1]).setMessages(messages);
        }
    }

    // response: 408|0 lub 408|ID_conv
    private void catchNewConversationResp(String[] sMessage){
        if(sMessage[1].equals("0"))
            System.out.println("Fail: can't create new conversation");
        else{
            Client.getClient().getUser().addConversation(
                    new Conversation(sMessage[1],
                            Client.getClient().getUser().getAllFriendsByLogin(sMessage[2].split(","))));
        }
    }

    // response: 409|0 lub 409|1
    private void catchChangeDescResp(String[] sMessage){
        if(sMessage[1].equals("0"))
            System.out.println("Fail: unable to change description");
        else{
            System.out.println("Succes: description changed!");
        }
    }

    private void stop(){
        this.running = false;
    }
}
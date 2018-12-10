package app.logic;

import javax.swing.text.View;
import javax.swing.text.html.ListView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
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
                System.out.println(message);
                sMessage = message.split(Pattern.quote("|"));

                switch (Integer.valueOf(sMessage[0])) {
                    case 400:
                        if(sMessage[1].equals("0"))
                            System.out.println("x");
                        else
                            System.out.println("x");
                        break;
                    case 401:
                            signIn(sMessage);
                            break;
                    case 402:
                        if(sMessage[1].equals("0"))
                            System.out.println("x");
                        else
                            System.out.println("x");
                        break;
                    case 403:
                        if(sMessage[1].equals("0"))
                            System.out.println("x");
                        else
                            System.out.println("x");
                        break;
                    case 404:
                            searchForUser(sMessage);
                        break;
                    case 405:
                            addFriend(sMessage);
                        break;
                    case 406:
                        if(sMessage[1].equals("0"))
                            System.out.println("x");
                        else
                            System.out.println("x");
                        break;
                    case 408:
                        newConversation(sMessage);
                        break;
                    case 500:
                        if(sMessage[1].equals("0"))
                            System.out.println("x");
                        else
                            System.out.println("x");
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void signIn(String[] sMessage){
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

    private void searchForUser(String[] sMessage){
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

    private void addFriend(String[] sMessage){
        if(sMessage[1].equals("0"))
            System.out.println("Fail: User not added");
        else{
            Client.getClient().getUser().addFriend(new User(sMessage[1], sMessage[2], sMessage[3], sMessage[4].equals("1")));
            ViewMenager.menuController.refreshFriendsList();
        }
    }

    private void newConversation(String[] sMessage){
        if(sMessage[1].equals("0"))
            System.out.println("Fail: can't create new conversation");
        else{
            System.out.println("xd");
            //Client.getClient().getUser().addConversation(new Conversation(sMessage[1]));
        }

    }

    private void stop(){
        this.running = false;
    }
}
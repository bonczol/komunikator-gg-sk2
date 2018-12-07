package app.logic;

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
                        if(sMessage[1].equals("0"))
                            System.out.println("x");
                        else
                            System.out.println("x");
                        break;
                    case 406:
                        if(sMessage[1].equals("0"))
                            System.out.println("x");
                        else
                            System.out.println("x");
                        break;
                    case 408:
                        if(sMessage[1].equals("0"))
                            System.out.println("x");
                        else
                            System.out.println("x");
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
        if(sMessage[1].equals("0")){
            System.out.println("Fail: User not found");
        }
        else{
            ViewMenager.addFriendController.showSearchResults(sMessage[1].split(","));
            System.out.println("Success: User " + sMessage[1] + " found!");
        }
    }

    private void stop(){
        this.running = false;
    }
}
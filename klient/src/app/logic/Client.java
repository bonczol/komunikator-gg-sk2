package app.logic;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

public class Client {
    private Socket clientSocket;
    public LocalUser user;
    private Receiver receiver;
    private Sender sender;

    public Client(String ipAddress, int port) {
        try {
            this.clientSocket = new Socket(ipAddress, port);
            this.sender = new Sender(this.clientSocket);
            this.receiver= new Receiver(this.clientSocket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.user = null;
    }

    public Sender getSender() {
        return sender;
    }

    private class Receiver implements Runnable{
        private BufferedReader reader;
        private volatile boolean running;

        private Receiver(Socket socket) throws IOException {
            this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }

        @Override
        public void run() {
            String message;
            ArrayList<String> sMessage;
            while(running){
                try {
                    message = reader.readLine();
                    sMessage = new ArrayList<>(Arrays.asList(message.split(Pattern.quote("|"))));
                    switch (Integer.valueOf(sMessage.get(0))) {
                        case 400:
                            if(sMessage.get(0).equals("0"))
                                System.out.println("x");
                            else
                                System.out.println("x");
                            break;
                        case 401:
                            if(sMessage.get(0).equals("0"))
                                Main.getLoggInController().accessDenied();
                            else
                                Main.getLoggInController().accessGranted("Janusz","Lubie chodzi do domu ", new ArrayList<User>());
                            break;
                        case 402:
                            if(sMessage.get(0).equals("0"))
                                System.out.println("x");
                            else
                                System.out.println("x");
                            break;
                        case 403:
                            if(sMessage.get(0).equals("0"))
                                System.out.println("x");
                            else
                                System.out.println("x");
                            break;
                        case 404:
                            if(sMessage.get(0).equals("0"))
                                System.out.println("x");
                            else
                                System.out.println("x");
                            break;
                        case 405:
                            if(sMessage.get(0).equals("0"))
                                System.out.println("x");
                            else
                                System.out.println("x");
                            break;
                        case 406:
                            if(sMessage.get(0).equals("0"))
                                System.out.println("x");
                            else
                                System.out.println("x");
                            break;
                        case 408:
                            if(sMessage.get(0).equals("0"))
                                System.out.println("x");
                            else
                                System.out.println("x");
                            break;
                        case 500:
                            if(sMessage.get(0).equals("0"))
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

        private void stop(){
            this.running = false;
        }
    }


    public class Sender{
        private PrintWriter writer;
        private char sep;

        private Sender(Socket socket) throws IOException {
            this.writer = new PrintWriter(clientSocket.getOutputStream(), true);
            this.sep = '|';
        }

        public void sendTextMessage(String convID, String message){
            writer.println("100" + this.sep + convID + this.sep + message);
        }

        public void sendSignInMessage(String login, String password){
            writer.println("101" + this.sep + login + this.sep + password);
        }

        public void sendSignOutMessage(){
            writer.println("102" + this.sep);
        }

        public void sendSignUpMessage(String nick, String login, String password){
            writer.println("103" + this.sep + nick + this.sep + login + this.sep + password);
        }

        public void sendSearchMessage(String login){
            writer.println("104" + this.sep + login);
        }

        public void sendAddFriendMessage(String login){
            writer.println("105" + this.sep + login);
        }

        public void sendGetFriendsMessage(String login){
            writer.println("106" + this.sep + login);
        }

        public void sendGetHistoryMessage(String convID){
            writer.println("107" + this.sep + convID);
        }

        public void sendNewConvMessage(ArrayList<String> chatMembers){
            writer.println(transformToMessage("108", chatMembers));
        }

        public void sendChangeDescMessage(String description){
            writer.println("108" + this.sep + description);
        }

        private String transformToMessage(String code, ArrayList<String> message){
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(String.format("%s%s", code, this.sep));
            for(int i = 0; i  < message.size(); i++){
                stringBuilder.append(message.get(i));
                if(i < message.size() - 1 )
                    stringBuilder.append(this.sep);
            }
            return stringBuilder.toString();
        }


    }




}
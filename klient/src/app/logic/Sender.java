package app.logic;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class Sender{
    private PrintWriter writer;
    private char sep;
    private String eof;

    public Sender(Socket socket) throws IOException {
        this.writer = new PrintWriter(socket.getOutputStream(), true);
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
        System.out.println("104" + this.sep + login);
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

    public void sendNewConvMessage(String[] chatMembers){
        System.out.println(transformToMessage("108", chatMembers));
        writer.println(transformToMessage("108", chatMembers));
    }

    public void sendChangeDescMessage(String description){
        writer.println("108" + this.sep + description);
    }

    private String transformToMessage(String code, String[] message){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("%s%s", code, this.sep));

        for(int i = 0; i  < message.length; i++){
            stringBuilder.append(message[i]);
            if(i < message.length - 1 )
                stringBuilder.append(",");
        }
        return stringBuilder.toString();
    }


}
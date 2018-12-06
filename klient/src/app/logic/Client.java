package app.logic;


import java.net.Socket;
import java.util.ArrayList;

public class Client {
    private Socket clientSocket;
    private Receiver receiver;
    private Sender sender;
    public LocalUser user;

    public Client(String ipAddress, int port) {
//        try {
//
////            this.clientSocket = new Socket(ipAddress, port);
////            this.sender = new Sender(this.clientSocket);
////            this.receiver= new Receiver(this.clientSocket);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        this.clientSocket = null;
        this.sender = null;
        this.receiver = null;
        ArrayList<User> list = new ArrayList<>();


        this.user = new LocalUser("Pudzian", "Mariusz", "ale dzisiaj dojebalem", true, null);
        list.add(this.user);
        list.add(new User("Tomasz", "Lis", "kupie opla corse 1998", true));
        list.add(new User("Januszek", "Kot", "pomocy bija", true));
        this.user.setFriends(list);
    }


    public Sender getSender() {
        return sender;
    }

    public LocalUser getUser() {
        return user;
    }

}
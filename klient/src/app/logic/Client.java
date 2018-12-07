package app.logic;


import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Client {
    private static Client client;
    private Socket clientSocket;
    private Receiver receiver;
    private Sender sender;
    public LocalUser user;


    private Client(String ipAddress, int port) {
        try {

            this.clientSocket = new Socket(ipAddress, port);
            this.sender = new Sender(this.clientSocket);
            this.receiver = new Receiver(this.clientSocket);
            Thread thread = new Thread(receiver);
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<User> list = new ArrayList<>();


        this.user = new LocalUser("Pudzian", "Mariusz", "ale dzisiaj dojebalem", true, null);
        list.add(this.user);
        list.add(new User("Tomasz", "Lis", "kupie opla corse 1998", true));
        list.add(new User("Januszek", "Kot", "pomocy bija", true));
        this.user.setFriends(list);
    }

    public static synchronized Client getClient() {
        if(client == null){
            synchronized (Client.class){
                if(client == null)
                    client = new Client("192.168.0.19", 1338);
            }
        }

        return client;
    }

    public Sender getSender() {
        return sender;
    }

    public LocalUser getUser() {
        return user;
    }

}
package app.logic;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Client {
    private static Client client;
    private Socket clientSocket;
    private Receiver receiver;
    private Sender sender;
    public LocalUser user;


    private Client() {
        try {
            this.clientSocket = new Socket();
            this.sender = new Sender(this.clientSocket);
            this.receiver = new Receiver(this.clientSocket);
            Thread thread = new Thread(receiver);
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.user = null;
    }

    public void connectToServer(String  serverIP, int port) throws IOException {
        clientSocket.connect(new InetSocketAddress(serverIP, port), 5000);
    }

    public static synchronized Client getClient() {
        if(client == null){
            synchronized (Client.class){
                if(client == null)
                    client = new Client();
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
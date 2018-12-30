package app.logic;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Logger;


public class Client {
    private boolean connected;
    private String serverIP;
    private int port;
    private static Client client;
    private Socket clientSocket;
    private Receiver receiver;
    private Sender sender;
    public LocalUser user;
    private static final Logger LOG = Logger.getLogger(ResponseHandler.class.getName());


    private Client() {
        this.clientSocket = new Socket();
        this.connected = false;
    }

    public void connectToServer(String  serverIP, int port) {
        try {
            clientSocket.connect(new InetSocketAddress(serverIP, port), 5000);
            this.sender = new Sender(this.clientSocket);
            this.receiver = new Receiver(this.clientSocket);
            Thread thread = new Thread(receiver);
            thread.start();
            connected = true;
        } catch (IOException e) {
            LOG.info("Server unreachable");
            connected = false;
            clientSocket = null;
            clientSocket = new Socket();
        }
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public String getServerIP() {
        return serverIP;
    }

    public int getPort() {
        return port;
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

    public boolean isConnected() {
        return connected;
    }
}
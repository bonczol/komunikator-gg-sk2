package app.logic;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class Client implements Runnable{
    private Socket clientSocket;
    private PrintWriter writer;
    private BufferedReader reader;
    private LocalUser user;
    private volatile boolean running;


    public Client(String ipAddress, int port) {
//        try {
//            this.clientSocket = new Socket(ipAddress, port);
//            this.writer = new PrintWriter(clientSocket.getOutputStream(), true);
//            this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        this.clientSocket = null;
        this.writer = null;
        this.reader = null;
        //this.user = new LocalUser();
        this.user = null;
    }

    public void sendMessage(ArrayList<String> message) {
        writer.println(message);
    }

    @Override
    public void run() {
        while(running){
            try {
                String message = reader.readLine();
                ArrayList<String> sMessage = new ArrayList<>(Arrays.asList(message.split(Pattern.quote("|"))));
                switch (Integer.valueOf(sMessage.get(0))) {
                    case 400:
                        if(sMessage.get(0).equals("0"))
                            System.out.println("Błąd logowania");
                        else
                            Main.getLoggInController().accessGranted();
                        break;
                    case 401:
                        if(sMessage.get(0).equals("0"))
                            System.out.println("x");
                        else
                            System.out.println("x");
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


    public void readMessage() throws IOException {
        reader.readLine();
    }




}
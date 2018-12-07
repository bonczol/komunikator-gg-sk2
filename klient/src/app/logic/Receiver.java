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
                            ViewMenager.loggInController.accessDenied();
                        else
                            //Main.getLoggInController().accessGranted("Janusz","Lubie chodzi do domu ", new ArrayList<User>());
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
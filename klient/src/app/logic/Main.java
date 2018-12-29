package app.logic;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/app/view/connect.fxml"));

        primaryStage.setOnHiding( event -> {
            if(Client.getClient().isConnected())
                Client.getClient().getSender().sendSignOutMessage();
        } );

        primaryStage.getIcons().add(new Image("/resources/img/icons8-sms-48.png"));
        primaryStage.setTitle("Bajdu");
        primaryStage.setScene(new Scene(root));
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    public static void main(String[] args) {
        System.setProperty("line.separator", "\n"); // Unix line separator
        //Client.getClient().connectToServer("192.168.0.20", 1337);
        launch(args);
    }

}

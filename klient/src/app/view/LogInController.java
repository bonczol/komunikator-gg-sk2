package app.view;

import app.logic.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class LogInController {
    @FXML
    private TextField textFieldLogin;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button buttonSignIn;
    @FXML
    private Button buttonSignUp;
    @FXML
    private Label labelInfo;


    public void singIn() {
        labelInfo.setText("Conecting...");
//        Client.getClient().getSender().sendSignInMessage(textFieldLogin.getText(), passwordField.getText());
        if(!Client.getClient().isConnected()){
            CompletableFuture.runAsync(() -> Client.getClient().connectToServer("192.168.0.20", 1337))
                    .handle((res, ex) -> {
                        if (Client.getClient().isConnected())
                            Client.getClient().getSender().sendSignInMessage(textFieldLogin.getText(), passwordField.getText());
                        else
                            Platform.runLater(() -> labelInfo.setText("Can't reach server"));
                        return res;
                    });
        }else
            Client.getClient().getSender().sendSignInMessage(textFieldLogin.getText(), passwordField.getText());
    }

    public void signUp(){
        Scene scene = textFieldLogin.getScene();
        FXMLLoader registerLoader = new FXMLLoader(getClass().getResource("register.fxml"));
        Parent registerRoot = null;
        try {
            registerRoot = registerLoader.load();
            scene.setRoot(registerRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
        labelInfo.setText("");
    }

    public void accessGranted(String nickname, String description, ArrayList<User> friends) {
        Client.getClient().user = new LocalUser(nickname, textFieldLogin.getText(), description, true, friends );
        Platform.runLater(() -> showMenuWindow());
    }

    public void accessDenied(){
        Platform.runLater(() ->{
            labelInfo.setText("Wrong username or password");
            textFieldLogin.clear();
            passwordField.clear();
        });
    }

    public void showMenuWindow(){
        Stage stage = (Stage) textFieldLogin.getScene().getWindow();
        FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("Menu.fxml"));
        try {
            ViewMenager.menuRoot = menuLoader.load();
            ViewMenager.menuController = menuLoader.getController();
            stage.setScene(new Scene(ViewMenager.menuRoot));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setOnHiding( event -> {Client.getClient().getSender().sendSignOutMessage();} );
    }

}

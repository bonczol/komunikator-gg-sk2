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

    private boolean connecting = false;


    public void singIn(){
        new Thread(() -> {
            Platform.runLater(()->{
                setInfoText("Connecting");
            });
            try {
                Client.getClient().connectToServer("192.168.0.20", 1338);
                Client.getClient().getSender().sendSignInMessage(textFieldLogin.getText(), passwordField.getText());
            } catch (IOException e) {
                Platform.runLater(()->{
                    setInfoText("Can't reach server");
                });
            }
        }).start();
    }

    public void signUp(){
        Scene scene = textFieldLogin.getScene();
        FXMLLoader registerLoader = new FXMLLoader(getClass().getResource("../view/register.fxml"));
        Parent registerRoot = null;
        try {
            registerRoot = registerLoader.load();
            scene.setRoot(registerRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("../view/Menu.fxml"));
        try {
            ViewMenager.menuRoot = menuLoader.load();
            ViewMenager.menuController = menuLoader.getController();
            stage.setScene(new Scene(ViewMenager.menuRoot));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setOnHiding( event -> {Client.getClient().getSender().sendSignOutMessage();} );
    }

    public void setInfoText(String text){
        labelInfo.setText(text);
    }



    public void connectAnimation(){
        connecting = true;
        Platform.runLater(()->{
            while (connecting){
                try {
                    setInfoText("Connecting ");
                    TimeUnit.MILLISECONDS.sleep(400);
                    setInfoText("Connecting. ");
                    TimeUnit.MILLISECONDS.sleep(400);
                    setInfoText("Connecting.. ");
                    TimeUnit.MILLISECONDS.sleep(400);
                    setInfoText("Connecting... ");
                    TimeUnit.MILLISECONDS.sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void stopConnectAnimation(){
        connecting = false;
    }
}

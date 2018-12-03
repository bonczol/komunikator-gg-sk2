package app.view;

import app.logic.Client;
import app.logic.LocalUser;
import app.logic.Main;
import app.logic.User;
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

public class LoggInController implements Initializable {
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


    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void singIn(){
       Main.getClient().getSender().sendSignInMessage(textFieldLogin.getText(), passwordField.getText());
    }


    public void accessGranted(String nickname, String description, ArrayList<User> friends) {
        Stage stage = (Stage) textFieldLogin.getScene().getWindow();
        stage.setScene(new Scene(Main.getMenuRoot()));
        stage.setOnHiding( event -> {Main.getClient().getSender().sendSignOutMessage();} );
        Main.getClient().user = new LocalUser(nickname, textFieldLogin.getText(), description, true, friends );
    }

    public void accessDenied(){
        labelInfo.setText("Wrong username or password");
        textFieldLogin.clear();
        passwordField.clear();
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


}
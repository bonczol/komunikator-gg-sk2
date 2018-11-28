package app.view;

import app.logic.Main;
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
    private Button buttonSingIn;
    @FXML
    private Button buttonSingUp;
    @FXML
    private ImageView logoView;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Image logo = new Image("/resources/img/logo.png");
        logoView.setImage(logo);
    }

    public void singIn(){
       Main.getClient().sendMessage(new ArrayList<>(Arrays.asList("101", textFieldLogin.getText(), passwordField.getText())));
    }

    public void accessGranted() {
        Stage stage = (Stage) textFieldLogin.getScene().getWindow();
        stage.setScene(new Scene(Main.getMenuRoot()));
    }

    public void signUp(){
        Stage stage = (Stage) textFieldLogin.getScene().getWindow();
        FXMLLoader registerLoader = new FXMLLoader(getClass().getResource("../view/register.fxml"));
        Parent registerRoot = null;
        try {
            registerRoot = registerLoader.load();
            stage.setScene(new Scene(registerRoot));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

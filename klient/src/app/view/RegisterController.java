package app.view;

import app.logic.Client;
import app.logic.Main;
import app.logic.ViewMenager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javax.swing.text.View;
import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController {
    @FXML
    private TextField textFieldNick;
    @FXML
    private TextField textFieldLogin;
    @FXML
    private PasswordField textFieldPassword;
    @FXML
    private PasswordField textFieldRepeatPassword;
    @FXML
    private Label labelInfo;
    @FXML
    private Button buttonSignUpReg;
    @FXML
    private Button buttonBack;


    public void signUp(){
        if(textFieldPassword.getText().equals(textFieldRepeatPassword.getText())){
            Client.getClient().getSender().sendSignUpMessage(textFieldNick.getText(), textFieldLogin.getText(), textFieldPassword.getText());
            labelInfo.setText("Connecting ...");
        }
        else
            labelInfo.setText("Passwords do not match ");
    }

    public void backToLoggIn(){
        Scene scene = textFieldLogin.getScene();
        scene.setRoot(ViewMenager.loggInRoot);
    }


}

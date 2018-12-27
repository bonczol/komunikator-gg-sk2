package app.view;

import app.logic.Client;
import app.logic.Main;
import app.logic.ViewMenager;
import javafx.application.Platform;
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
import java.util.concurrent.CompletableFuture;

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
        if(!Client.getClient().isConnected()){
            CompletableFuture.runAsync(() -> Client.getClient().connectToServer("192.168.0.19", 1337))
                    .handle((res, ex) -> {
                        if (Client.getClient().isConnected())
                            sendSingUpMes();
                        else
                            Platform.runLater(() -> labelInfo.setText("Can't reach server"));
                        return res;
                    });
        }else
            sendSingUpMes();
    }

    private void sendSingUpMes(){
        if(textFieldPassword.getText().equals(textFieldRepeatPassword.getText()))
            Client.getClient().getSender().sendSignUpMessage(textFieldNick.getText(), textFieldLogin.getText(), textFieldPassword.getText());
        else
            labelInfo.setText("Passwords do not match ");
    }

    public void backToLoggIn(){
        Scene scene = textFieldLogin.getScene();
        scene.setRoot(ViewMenager.loggInRoot);
    }


}

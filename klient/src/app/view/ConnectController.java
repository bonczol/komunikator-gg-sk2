package app.view;

import app.logic.Client;
import app.logic.ViewMenager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class ConnectController {
    @FXML
    private TextField fieldServerIP;
    @FXML
    private TextField fieldPort;
    @FXML
    private Label labelInfo;
    @FXML
    private Button buttonConnect;

    public void connect(){
        labelInfo.setText("Connecting to server...");
        CompletableFuture.runAsync(() -> Client.getClient().connectToServer(
                fieldServerIP.getText(), Integer.valueOf(fieldPort.getText())))
                    .handle((res, ex) -> {
                        if (Client.getClient().isConnected())
                            showLogInWindow();
                        else
                            Platform.runLater(() -> labelInfo.setText("Can't reach server"));
                        return res;
                    });
    }

    private void showLogInWindow(){
        FXMLLoader logInLoader = new FXMLLoader(getClass().getResource("logIn.fxml"));
        try {
            ViewMenager.loggInRoot = logInLoader.load();
            ViewMenager.logInController = logInLoader.getController();
            labelInfo.getScene().setRoot(ViewMenager.loggInRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

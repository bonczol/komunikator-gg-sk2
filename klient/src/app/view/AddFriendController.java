package app.view;

import app.logic.Client;
import app.logic.Main;
import app.logic.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class AddFriendController implements Initializable {

    @FXML
    private TextField textFieldLogin;
    @FXML
    private ListView<String[]> listViewUsers;
    @FXML
    private Button buttonAdd;

    private ObservableList<String[]> users;

    public AddFriendController(){
        users = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listViewUsers.setItems(users);
        textFieldLogin.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("kiki do you love me");
            Client.getClient().getSender().sendSearchMessage(textFieldLogin.getText());
        });
    }

    public void showSearchResults(String[] users_data){
        for(int i = 0; i < users_data.length; i = i + 2){
            String[] s = {users_data[i], users_data[i+1]};
            users.add(s);
        }

    }

    public void addUserToFriends(){
        Client.getClient().getSender().sendAddFriendMessage(listViewUsers.getSelectionModel().getSelectedItem()[1]);
    }


}

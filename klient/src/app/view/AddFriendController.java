package app.view;

import app.logic.Client;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import java.net.URL;
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
        textFieldLogin.textProperty().addListener((observable, oldValue, newValue) ->{
            if(!textFieldLogin.getText().isEmpty())
                Client.getClient().getSender().sendSearchMessage(textFieldLogin.getText());
            else
                users.clear();
        });
        listViewSetUp();
    }

    public void showSearchResults(String[] users_data){
        Platform.runLater(() -> {
            users.clear();
            for(int i = 0; i < users_data.length; i = i + 2){
                String[] s = {users_data[i], users_data[i+1]};
                users.add(s);
            }
        });
    }

    public void clearUsersList(){
        Platform.runLater(()->users.clear());
    }

    public void addUserToFriends(){
        Client.getClient().getSender().sendAddFriendMessage(listViewUsers.getSelectionModel().getSelectedItem()[1]);
    }

    private void listViewSetUp(){
        listViewUsers.setItems(users);
        listViewUsers.setCellFactory(param -> new ListCell<String[]>() {
            @Override
            protected void updateItem(String[] userInfo, boolean empty) {
                super.updateItem(userInfo, empty);

                if (empty || userInfo == null) {
                    setText(null);
                } else {
                     setText(userInfo[1] + " (" + userInfo[0] + ")");
                }
            }
        });
    }


}

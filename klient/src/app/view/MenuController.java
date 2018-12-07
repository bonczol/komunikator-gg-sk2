package app.view;

import app.logic.Client;
import app.logic.Main;
import app.logic.User;
import app.logic.ViewMenager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class MenuController implements Initializable {

    @FXML
    private TextArea textAreaDescription;
    @FXML
    private Button buttonAdd;
    @FXML
    private Button buttonDelete;
    @FXML
    private ToggleButton buttonFriends;
    @FXML
    private ToggleButton buttonGroups;
    @FXML
    private ListView<User> listViewUsers;
    @FXML
    private Label labelUserName;
    @FXML
    private Label labelDescription;

    private ObservableList<User> observableListUsers;

    public MenuController(){
        observableListUsers = FXCollections.observableArrayList();
        if(!Client.getClient().getUser().getFriends().isEmpty())
            observableListUsers.addAll(Client.getClient().getUser().getFriends());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        buttonAdd.getStyleClass().add("menuButton");
        buttonDelete.getStyleClass().add("menuButton");
        buttonFriends.getStyleClass().add("menuButton");
        buttonGroups.getStyleClass().add("menuButton");
        labelDescription.getStyleClass().add("labelDescription");
        labelUserName.getStyleClass().add("labelUserName");

        if(!observableListUsers.isEmpty())
            listViewUsers.setItems(observableListUsers);
        listViewUsers.setCellFactory(observableListUsers -> new UserCellController() );
    }

    public void showAddFriendWindow(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("addFriend.fxml"));
        try {
            Parent root = loader.load();
            ViewMenager.addFriendController = loader.getController();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

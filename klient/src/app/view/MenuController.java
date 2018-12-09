package app.view;

import app.logic.Client;
import app.logic.Main;
import app.logic.User;
import app.logic.ViewMenager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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

        listViewUsers.setItems(observableListUsers);
        listViewUsers.setCellFactory(observableListUsers -> new UserCellController() );
        createNewConversationListener();
    }

    public void showAddFriendWindow(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("addFriend.fxml"));
        try {
            Parent root = loader.load();
            ViewMenager.addFriendController = loader.getController();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Add friend");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void refreshFriendsList(){
        Platform.runLater(()->{
            ObservableList<User> tmp_obsv = FXCollections.observableArrayList(Client.getClient().getUser().getFriends());
            listViewUsers.setItems(null);
            observableListUsers = tmp_obsv;
            listViewUsers.setItems(observableListUsers);});
    }

    private void createNewConversationListener(){
        listViewUsers.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2){
                Client.getClient().getSender().sendNewConvMessage(new String[]{Client.getClient().user.getLogin(),
                        listViewUsers.getSelectionModel().getSelectedItem().getLogin()});
                showChatWindow();
            }

            });
    }

    private void showChatWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("chat.fxml"));
        try {
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}

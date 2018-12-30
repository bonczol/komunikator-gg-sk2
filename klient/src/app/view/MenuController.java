package app.view;

import app.logic.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;


public class MenuController implements Initializable {

    @FXML
    private TextArea textAreaDescription;
    @FXML
    private Button buttonAdd;
    @FXML
    private Button buttonDelete;
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
        // Change description when ENTER pressed
        textAreaDescription.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                String description = textAreaDescription.getText();
                Client.getClient().getUser().setDescription(description);
                Client.getClient().getSender().sendChangeDescMessage(description);
                Platform.runLater(() -> textAreaDescription.getParent().requestFocus());
                ke.consume();
            }
        });

        // Set local user nick and description
        labelUserName.setText(Client.getClient().getUser().getNickname());
        textAreaDescription.setText(Client.getClient().getUser().getDescription());

        listViewUsers.setItems(observableListUsers);
        listViewUsers.setCellFactory(observableListUsers -> new UserCellController() );
        createShowConversationListener();
    }


    public void deleteFriend(){
        User user = listViewUsers.getSelectionModel().getSelectedItem();
        if(user != null){
            Client.getClient().getSender().sendDeleteFriendMessage(user.getLogin());
            Client.getClient().getUser().getFriends().remove(user);
            refreshFriendsList();
        }
    }

    public void refreshFriendsList(){
        Platform.runLater(()->{
            ObservableList<User> tmp_obsv = FXCollections.observableArrayList(Client.getClient().getUser().getFriends());
            listViewUsers.setItems(null);
            observableListUsers = tmp_obsv;
            listViewUsers.setItems(observableListUsers);});
    }

    private void createShowConversationListener(){
        listViewUsers.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2){
                User user = listViewUsers.getSelectionModel().getSelectedItem();
                Conversation conversation =  Client.getClient().getUser().getConvWithUser(user);

                if(conversation == null)
                    Client.getClient().getSender().sendNewConvMessage(new String[]{Client.getClient().user.getLogin(),
                            user.getLogin()});
                else
                    showChatWindow(conversation);
            }
        });
    }

    public void showChatWindow(Conversation conversation) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("chat.fxml"));
        try {
            Parent root = loader.load();
            ChatController chatController = loader.getController();
            chatController.setConversation(conversation);
            ViewMenager.chatControllers.add(chatController);

            Platform.runLater(()-> {
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.getIcons().add(new Image("/resources/img/icons8-sms-48.png"));
                stage.show();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showAddFriendWindow(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("addFriend.fxml"));
        try {
            Parent root = loader.load();
            ViewMenager.addFriendController = loader.getController();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Add friend");
            stage.getIcons().add(new Image("/resources/img/icons8-sms-48.png"));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connectionLost(){
        labelDescription.getStyleClass().add("labelConLost");
        labelDescription.setText("Connection lost - try to sign in later");
    }



}

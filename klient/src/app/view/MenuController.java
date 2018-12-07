package app.view;

import app.logic.Main;
import app.logic.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

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
        if(!Main.getClient().getUser().getFriends().isEmpty())
            observableListUsers.addAll(Main.getClient().getUser().getFriends());
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
}

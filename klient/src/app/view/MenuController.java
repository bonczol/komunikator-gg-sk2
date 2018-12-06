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
    private ListView<User> listViewUsers;

    private ObservableList<User> observableListUsers;

    public MenuController(){
        observableListUsers = FXCollections.observableArrayList();
        if(!Main.getClient().getUser().getFriends().isEmpty())
            observableListUsers.addAll(Main.getClient().getUser().getFriends());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(!observableListUsers.isEmpty())
            listViewUsers.setItems(observableListUsers);
        listViewUsers.setCellFactory(observableListUsers -> new UserCellController() );
    }
}

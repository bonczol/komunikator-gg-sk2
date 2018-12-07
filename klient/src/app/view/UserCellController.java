package app.view;

import app.logic.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.IOException;


public class UserCellController extends ListCell<User> {
    @FXML
    private Label labelNick;
    @FXML
    private Label labelDescription;
    @FXML
    private Circle circle;
    @FXML
    private HBox hbox;

    private FXMLLoader fxmlLoader;

    public UserCellController(){
        fxmlLoader = new FXMLLoader(getClass().getResource("userCell.fxml"));
        fxmlLoader.setController(this);
    }

    @Override
    protected void updateItem(User user, boolean empty){
        super.updateItem(user, empty);

        if(empty || user == null){
            setPrefHeight(50);
            setText(null);
            setGraphic(null);
        }else{
            try {
                fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            labelNick.setText(user.getNickname());
            labelDescription.setText(user.getDescription());
            if(user.isOnline())
                circle.setFill(Color.GREEN);
            else
                circle.setFill(Color.RED);
            setText(null);
            setGraphic(hbox);
        }
    }


}


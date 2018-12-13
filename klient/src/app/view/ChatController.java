package app.view;


import app.logic.Client;
import app.logic.Conversation;
import app.logic.Message;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import java.net.URL;
import java.util.ResourceBundle;


public class ChatController implements Initializable {
    @FXML
    private TextArea textAreaMessage;
    @FXML
    private ListView<Message> listViewConversation;
    @FXML
    private Button buttonSend;

    private Conversation conversation;
    private ObservableList<Message> obsvListMessages;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Send message when ENTER is pressed
        textAreaMessage.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER))
                sendMessage();
        });
    }

    public void sendMessage(){
        String stringMessage = textAreaMessage.getText();

        Client.getClient().getSender().sendTextMessage(conversation.getId(),stringMessage);
        conversation.getMessages().add(new Message(stringMessage));
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
        obsvListMessages = FXCollections.observableArrayList(conversation.getMessages());
        setUpListView();
    }

    public void setUpListView(){
        listViewConversation.setItems(obsvListMessages);
        listViewConversation.setCellFactory(param -> new ListCell<Message>() {
            @Override
            protected void updateItem(Message message, boolean empty) {
                super.updateItem(message, empty);

                if (empty || message == null) {
                    setText(null);
                } else {
                    setText(message.getTime() + " " + message.getAuthor() + ": " + message.getText());
                }
            }
        });
    }
}

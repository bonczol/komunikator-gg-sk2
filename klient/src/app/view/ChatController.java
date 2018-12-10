package app.view;


import app.logic.Conversation;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;


public class ChatController {
    @FXML
    private TextArea textAreaMessage;
    @FXML
    private ListView<String> listViewConversation;
    @FXML
    private Button buttonSend;

    private Conversation conversation;

//    public ChatController(){
//        this.conversation = Client.getClient().getUser().getConversations().
//    }


    public void sendMessage(){
        String message = textAreaMessage.getText();
        //Client.getClient().getSender().sendTextMessage();
    }


}

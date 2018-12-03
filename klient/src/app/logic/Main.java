package app.logic;

import app.view.ChatController;
import app.view.LoggInController;
import app.view.MenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
    private final static Client client = new Client("192.168.0.1", 1234);
    private static LoggInController loggInController;
    private static MenuController menuController;
    private static ChatController chatController;
    private static Parent loggInRoot;
    private static Parent menuRoot;
    private static Parent chatRoot;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loggInLoader = new FXMLLoader(getClass().getResource("../view/loggIn.fxml"));
        FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("../view/Menu.fxml"));
        FXMLLoader chatLoader = new FXMLLoader(getClass().getResource("../view/chat.fxml"));

        loggInRoot = loggInLoader.load();
        menuRoot = menuLoader.load();
        chatRoot = chatLoader.load();

        loggInController = loggInLoader.getController();
        menuController = menuLoader.getController();
        chatController = chatLoader.getController();


        primaryStage.setTitle("Bajdu bajdu");
        primaryStage.setScene(new Scene(loggInRoot));
        primaryStage.sizeToScene();
        primaryStage.show();
    }
    public static void main(String[] args) {

        System.out.println(javafx.scene.text.Font.getFamilies());
        launch(args);
    }

    public static LoggInController getLoggInController() {
        return loggInController;
    }

    public static MenuController getMenuController() {
        return menuController;
    }

    public static ChatController getChatController() {
        return chatController;
    }

    public static Parent getLoggInRoot() {
        return loggInRoot;
    }

    public static Parent getMenuRoot() {
        return menuRoot;
    }

    public static Parent getChatRoot() {
        return chatRoot;
    }

    public static Client getClient() {
        return client;
    }
}

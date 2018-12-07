package app.logic;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
    private final static Client client = new Client("192.168.0.1", 1234);

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loggInLoader = new FXMLLoader(getClass().getResource("../view/loggIn.fxml"));
        ViewMenager.loggInRoot = loggInLoader.load();
        ViewMenager.loggInController = loggInLoader.getController();

        primaryStage.setTitle("Bajdu bajdu");
        primaryStage.setScene(new Scene(ViewMenager.loggInRoot));
        primaryStage.sizeToScene();
        primaryStage.show();
    }
    public static void main(String[] args) { launch(args);
    }

    public static Client getClient() {
        return client;
    }
}

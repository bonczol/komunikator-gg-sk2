package app.logic;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loggInLoader = new FXMLLoader(getClass().getResource("../view/loggIn.fxml"));
        ViewMenager.loggInRoot = loggInLoader.load();
        ViewMenager.loggInController = loggInLoader.getController();
        primaryStage.setOnHiding( event -> {Client.getClient().getSender().sendSignOutMessage();} );

        primaryStage.setTitle("Bajdu bajdu");
        primaryStage.setScene(new Scene(ViewMenager.loggInRoot));
        primaryStage.sizeToScene();
        primaryStage.show();
    }
    public static void main(String[] args) {
        System.setProperty("line.separator", "\n"); // Unix line separator
        launch(args);
    }

}

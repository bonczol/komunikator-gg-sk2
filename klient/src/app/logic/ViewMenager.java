package app.logic;

import app.view.AddFriendController;
import app.view.ChatController;
import app.view.LogInController;
import app.view.MenuController;
import javafx.scene.Parent;

import java.util.ArrayList;

public class ViewMenager {
    public static LogInController logInController;
    public static MenuController menuController;
    public static ArrayList<ChatController> chatControllers;
    public static AddFriendController addFriendController;
    public static Parent loggInRoot;
    public static Parent menuRoot;
}

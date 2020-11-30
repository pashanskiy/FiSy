package FiSy;

import FiSy.StoreData.StoreLogin;
import javafx.animation.Animation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;


public class mainfx {
    @FXML
    Pane pane;
    @FXML
    AnchorPane mainPane;
    @FXML
    Button backButton;
    @FXML
    public static AnchorPane mainpane;
    public static String oldScene;
    public static String currentScene;
    public static Button backbutton;
    public static boolean firstsearch = true;
    public static Key passKey;
    public static Key currentdatabaseKey;
    public static Key databaseKey;
    public static String databaseUUID;
    public static ArrayList<StoreLogin.SUser> lowUsers= new ArrayList<>();
    public static String userPower;
    public static int lowuserNum;
    public void initialize(){
        backbutton=backButton;
        mainpane = mainPane;
        currentScene="mainfx.fxml";
        try {
            Pane pane2 = (Pane)FXMLLoader.load(getClass().getResource("ServerList.fxml"));
            pane.getChildren().setAll(pane2);
        } catch (IOException e) {
            e.printStackTrace();

        }

    }

    @FXML
    public void top(ActionEvent event){
        if(animation.timeline2.getStatus() != Animation.Status.RUNNING) {
            if(currentScene.equals("Register.fxml")||currentScene.equals("ControlDialog.fxml")) animation.SlideUp(getClass(),mainPane,oldScene);
            else animation.SlideRight(getClass(), mainPane, oldScene);
        }
    }


}

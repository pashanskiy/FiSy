package FiSy;

import FiSy.StoreData.SendData;
import FiSy.StoreData.StoreLogin;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;

public class UsersControllerDialog {
    @FXML TableColumn name;
    @FXML TableColumn surname;
    @FXML TableColumn name3;
    @FXML TableColumn group;
    @FXML TableColumn login;

    @FXML private TableView<tablePerson.UserDialogPerson> userTableView = new TableView<>();
    static public ObservableList<tablePerson.UserDialogPerson> data = FXCollections.observableArrayList();
    public static TableView<tablePerson.UserDialogPerson> usertableview;
    public static tablePerson.UserDialogPerson selectedItem;
    public static StoreLogin storeLogin = new StoreLogin();
    public static boolean differentFolder=false;
    public void initialize(){
        mainfx.currentScene="UsersControlDialog.fxml";
        mainfx.oldScene="FileDialog.fxml";
        usertableview=userTableView;
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        surname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        name3.setCellValueFactory(new PropertyValueFactory<>("middlename"));
        group.setCellValueFactory(new PropertyValueFactory<>("group"));
        login.setCellValueFactory(new PropertyValueFactory<>("login"));

        userTableView.setRowFactory( tv -> {
            TableRow<tablePerson.UserDialogPerson> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    doubleClickToRow(row.getItem());
                }
            });
           return row;
        });

        SendData sendData = new SendData();
        sendData.setType("GetFullLowUsers");
        SocketClient.SendAesStream(SendData.toByte(sendData));
    }

    public void doubleClickToRow(tablePerson.UserDialogPerson row){
        selectedItem = row;
        if(row.getName().equals("В свою папку")&&row.getLogin().equals("В свою папку")){
            differentFolder=false;
            SendData sendData = new SendData();
            sendData.setType("GoToMainFolder");
            SocketClient.SendAesStream(SendData.toByte(sendData));
            mainfx.databaseKey=mainfx.currentdatabaseKey;
            FileDialog.inputStoreFile=null;
            animation.SlideUp(getClass(), mainfx.mainpane, "FileDialog.fxml");
        }else {
            SendData sendData = new SendData();
            Integer uid = storeLogin.getSUser().get(userTableView.getSelectionModel().getFocusedIndex()).getUserID();
            sendData.setType("GetUserDBKeyLikePublic");
            sendData.setObject(uid);
            differentFolder=true;
            SocketClient.SendAesStream(SendData.toByte(sendData));
        }
    }

    public static void setToTable(){
        data.clear();
        for(StoreLogin.SUser sUser:storeLogin.getSUser()){
            data.add(new tablePerson.UserDialogPerson(sUser.getName(),sUser.getSurname(),sUser.getMiddlename(),sUser.getGroup(),sUser.getLogin()));
        }
        if(differentFolder) data.add(new tablePerson.UserDialogPerson("В свою папку","","","","В свою папку"));

        usertableview.setItems(data);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if(data.size()>0) {
                    usertableview.getColumns().get(0).setVisible(false);
                    usertableview.getColumns().get(0).setVisible(true);
                }
            }
        });

    }
}

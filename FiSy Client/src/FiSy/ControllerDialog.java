package FiSy;

import FiSy.StoreData.SendData;
import FiSy.StoreData.StoreLogin;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;

public class ControllerDialog {
    @FXML Button upButton;
    @FXML Button okButton;
    @FXML Button downButton;
    @FXML TableColumn name;
    @FXML TableColumn usrid;
    @FXML TableColumn surname;
    @FXML TableColumn name3;
    @FXML TableColumn group;
    @FXML TableColumn login;
    @FXML TableColumn dir;
    @FXML TableColumn type;

    @FXML private TableView<tablePerson.AdminDialogPerson> userTableView = new TableView<>();
    static public ObservableList<tablePerson.AdminDialogPerson> data = FXCollections.observableArrayList();
    static ObservableList<tablePerson.AdminDialogPerson> selectData= FXCollections.observableArrayList();
    public static TableView<tablePerson.AdminDialogPerson> usertableview;
    public static tablePerson.AdminDialogPerson selectedItem;
    public static StoreLogin storeLogin;
    ObservableList<tablePerson.AdminDialogPerson> selectedItems;
    boolean enableOK = false;
    int highUser=0;
    public void initialize(){
        mainfx.currentScene="ControlDialog.fxml";
        mainfx.oldScene="FileDialog.fxml";
        usertableview=userTableView;
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        usrid.setCellValueFactory(new PropertyValueFactory<>("usrid"));
        surname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        name3.setCellValueFactory(new PropertyValueFactory<>("middlename"));
        group.setCellValueFactory(new PropertyValueFactory<>("group"));
        dir.setCellValueFactory(new PropertyValueFactory<>("dir"));
        login.setCellValueFactory(new PropertyValueFactory<>("login"));
        type.setCellValueFactory(new PropertyValueFactory<>("type"));

        userTableView.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                selectedItem = userTableView.getSelectionModel().getSelectedItem();
                switch (selectedItem.getType()){
                    case "Off":{
                        animation.toogleNode(upButton,1);
                        animation.toogleNode(downButton,0.5);
                        break;
                    }

                    case "User":{
                        animation.toogleNode(upButton,1);
                        animation.toogleNode(downButton,1);
                        break;
                    }

                    case "Administrator":{
                        animation.toogleNode(upButton,0.5);
                        animation.toogleNode(downButton,1);
                        break;
                    }
                }

            }
        });

        userTableView.setRowFactory( tv -> {
            TableRow<tablePerson.AdminDialogPerson> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    doubleClickToRow(row.getItem());
                }
            });
           return row;
        });

        SendData sendData = new SendData();
        sendData.setType("GetUsersForAdmin");
        SocketClient.SendAesStream(SendData.toByte(sendData));
    }

    public void doubleClickToRow(tablePerson.AdminDialogPerson row){
        selectedItem = row;
        animation.SlideLeft(getClass(),mainfx.mainpane,"ControlDialog2.fxml");
    }

    @FXML
    public void okButtonAction(ActionEvent event){
        ArrayList<String[]> newUsersRight = new ArrayList<>();
        if(data.size()>0){
            for (tablePerson.AdminDialogPerson user: data){
                String[] right=new String[2];
                right[0]=user.getUsrid();
                right[1]=user.getType();
                newUsersRight.add(right);
            }
            SendData sendData = new SendData();
            sendData.setObject(newUsersRight);
            sendData.setType("SetNewUsersRight");
            SocketClient.SendAesStream(SendData.toByte(sendData));
        }

    }

    @FXML
    public void upButtonAction(ActionEvent event){
        switch (selectedItem.getType()){
            case "Off":{
                data.get(userTableView.getSelectionModel().getFocusedIndex()).setType("User");
                userTableView.setItems(data);
                animation.toogleNode(upButton,1);
                animation.toogleNode(downButton,0);
                break;
            }

            case "User":{
                data.get(userTableView.getSelectionModel().getFocusedIndex()).setType("Administrator");
                userTableView.setItems(data);
                animation.toogleNode(upButton,1);
                animation.toogleNode(downButton,1);
                break;
            }
        }
        enableOK=true;
        if(enableOK)animation.toogleNode(okButton,1);

        userTableView.getColumns().get(0).setVisible(false);
        userTableView.getColumns().get(0).setVisible(true);
    }

    @FXML
    public void downButtonAction(ActionEvent event){
        switch (selectedItem.getType()){

            case "User":{
                data.get(userTableView.getSelectionModel().getFocusedIndex()).setType("Off");
                userTableView.setItems(data);
                animation.toogleNode(upButton,1);
                animation.toogleNode(downButton,0.5);
                break;
            }

            case "Administrator":{
                data.get(userTableView.getSelectionModel().getFocusedIndex()).setType("User");
                userTableView.setItems(data);
                animation.toogleNode(upButton,1);
                animation.toogleNode(downButton,1);
                break;
            }
        }
        enableOK=true;
        if(enableOK)animation.toogleNode(okButton,1);

        userTableView.getColumns().get(0).setVisible(false);
        userTableView.getColumns().get(0).setVisible(true);
    }
}

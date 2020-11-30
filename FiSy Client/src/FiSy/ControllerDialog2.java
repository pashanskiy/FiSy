package FiSy;

import FiSy.StoreData.SendData;
import FiSy.StoreData.StoreLogin;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;

public class ControllerDialog2 {
    @FXML
    Button okButton;
    @FXML
    TableColumn name;
    @FXML TableColumn usrid;
    @FXML TableColumn surname;
    @FXML TableColumn name3;
    @FXML TableColumn group;
    @FXML TableColumn type;
    @FXML public TableView<tablePerson.AdminSetRightDialogPerson> userRightTableView = new TableView<>();
    static ObservableList<tablePerson.AdminSetRightDialogPerson> rightData = FXCollections.observableArrayList();
    public static TableView<tablePerson.AdminSetRightDialogPerson> userrighttableview;
    boolean enableOK = false;
    public void initialize(){
        mainfx.currentScene="ControlDialog2.fxml";
        mainfx.oldScene="ControlDialog.fxml";
        userrighttableview=userRightTableView;
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        usrid.setCellValueFactory(new PropertyValueFactory<>("usrid"));
        surname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        name3.setCellValueFactory(new PropertyValueFactory<>("middlename"));
        group.setCellValueFactory(new PropertyValueFactory<>("group"));
        type.setCellValueFactory(new PropertyValueFactory<>("type"));

        userRightTableView.setRowFactory( tv -> {
            TableRow<tablePerson.AdminSetRightDialogPerson> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    doubleClickToRow(row.getItem());
                }
            });
            return row;
        });

        SendData sendData = new SendData();
        sendData.setType("GetLowUsersToAdminMenu");
        StoreLogin storeLogin = new StoreLogin();
        storeLogin.setUserId(Integer.parseInt(ControllerDialog.selectedItem.getUsrid()));
        sendData.setObject(storeLogin);
        SocketClient.SendAesStream(SendData.toByte(sendData));
    }

    @FXML
    public void okButtonAction(ActionEvent event){
        if(rightData.size()>0){
            Integer[] userRight = new Integer[3];
            ArrayList<Integer[]> uri = new ArrayList<>();
            for(tablePerson.AdminSetRightDialogPerson data:rightData){
                userRight[0]=Integer.parseInt(ControllerDialog.selectedItem.getUsrid());
                userRight[1]=Integer.parseInt(data.getUsrid());
                if(data.getType().equals("Да")){
                    userRight[2]=1;
                }
                if(data.getType().equals("Нет")){
                    userRight[2]=0;
                }
                uri.add(userRight.clone());
            }
            SendData sendData = new SendData();
            sendData.setType("SetNewUserRules");
            sendData.setObject(uri);
            SocketClient.SendAesStream(SendData.toByte(sendData));
        }
    }

    private void doubleClickToRow(tablePerson.AdminSetRightDialogPerson row){
        if(row.getType().equals("Да")){
            rightData.get(userRightTableView.getSelectionModel().getFocusedIndex()).setType("Нет");
        }
        else{
            rightData.get(userRightTableView.getSelectionModel().getFocusedIndex()).setType("Да");
        }
        userRightTableView.getColumns().get(0).setVisible(false);
        userRightTableView.getColumns().get(0).setVisible(true);
        enableOK=true;
        if(enableOK)
            animation.toogleNode(okButton,1);
    }

    public static void toUserRight(ArrayList<Integer> lu){
        rightData.clear();
        boolean lowuser = false;
        for(tablePerson.AdminDialogPerson allUsers: ControllerDialog.data){
            if(!allUsers.getUsrid().equals(ControllerDialog.selectedItem.getUsrid())){
                for(Integer lowUser:lu){
                    if(Integer.parseInt(allUsers.getUsrid())==lowUser){
                        lowuser = true;
                    }
                }
                if(lowuser){
                    lowuser=false;
                    rightData.add(new tablePerson.AdminSetRightDialogPerson(allUsers.getUsrid(),allUsers.getName(),allUsers.getSurname(),allUsers.getMiddlename(),allUsers.getGroup(),allUsers.getLogin(),"Да"));
                }else
                { rightData.add(new tablePerson.AdminSetRightDialogPerson(allUsers.getUsrid(),allUsers.getName(),allUsers.getSurname(),allUsers.getMiddlename(),allUsers.getGroup(),allUsers.getLogin(),"Нет"));
                }
            }}
        //rightData.add(new tablePerson.AdminSetRightDialogPerson("kek","kek","kek","kek","kek","kek","kek"));
        userrighttableview.setItems(rightData);
    }
}

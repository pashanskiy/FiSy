package FiSy;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.               ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.util.*;
import java.util.function.UnaryOperator;

public class ServerList {

    @FXML Pane pane;
    @FXML TableColumn info;
    @FXML TableColumn address;
    @FXML TableColumn port;
    @FXML ProgressBar progressBar;
    @FXML Button researchButton;
    @FXML private TableView<tablePerson.LoginPerson> tableView = new TableView<>();
    @FXML Button nextButton;
    @FXML TextField portField;
    @FXML
    TextField ipAddress;
    @FXML Button addButton;
    public static Button resButton;
    static ObservableList<tablePerson.LoginPerson> data = FXCollections.observableArrayList();
    public static boolean resButtonEnabled=true;
    public static boolean autotologin=true;

    public boolean ipA=false;
    public boolean ipP=false;
    private static final String IPADDRESS_PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    public void initialize(){
        mainfx.currentScene="ServerList.fxml";
        resButton=researchButton;
        autotologin=true;
        animation.toogleNode(mainfx.backbutton, 0.5);
        Random r = new Random();
        int rPort = 49152 + r.nextInt(65535 - 49152 + 1);
        //rPort = 60000;
        if(mainfx.firstsearch) {
            mainfx.firstsearch=false;
            UDPSearchServer.SearchServer(rPort,tableView, progressBar);
        }else animation.toogleNode(researchButton,1);
        info.setCellValueFactory(new PropertyValueFactory<>("info"));
        address.setCellValueFactory(new PropertyValueFactory<>("address"));
        port.setCellValueFactory(new PropertyValueFactory<>("port"));
        //progressBar.setProgress(0);
        tableView.setItems(data);

        tableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(tableView.getSelectionModel().getSelectedItem()!=null) {
                    Login.serverAP[0] = tableView.getSelectionModel().getSelectedItem().getAddress();
                    Login.serverAP[1] = tableView.getSelectionModel().getSelectedItem().getPort();
                    animation.toogleNode(nextButton, 1);
                }else{animation.toogleNode(nextButton,0.50);}
            }
        });

        ipAddress.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$")) {
                    animation.toogleNode(addButton,0.5);
                }else{
                    ipA=true;
                    if(ipA&&ipP) animation.toogleNode(addButton,1);
                }
            }
        });

        String regex = makePartialIPRegex();
        final UnaryOperator<TextFormatter.Change> ipAddressFilter = c -> {
            String text = c.getControlNewText();
            if  (text.matches(regex)) {
                return c ;
            } else {
                return null ;
            }
        };
        ipAddress.setTextFormatter(new TextFormatter<>(ipAddressFilter));

        portField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    portField.setText(newValue.replaceAll("[^\\d]", ""));
                    animation.toogleNode(addButton,0.5);
                    ipP=false;
                }else{
                    ipP=true;
                    if(ipA&&ipP&&Integer.parseInt(portField.getText())>0&&Integer.parseInt(portField.getText())<65536) animation.toogleNode(addButton,1);
                }
            }
        });
    }

    private String makePartialIPRegex() {
        String partialBlock = "(([01]?[0-9]{0,2})|(2[0-4][0-9])|(25[0-5]))" ;
        String subsequentPartialBlock = "(\\."+partialBlock+")" ;
        String ipAddress = partialBlock+"?"+subsequentPartialBlock+"{0,3}";
        return "^"+ipAddress ;
    }

    @FXML
    public void toAddButton(ActionEvent event){
        ServerList.toTableView(tableView, portField.getText(), ipAddress.getText());
    }

    @FXML
    public void research(ActionEvent event){
        if(true) {
            resButtonEnabled=false;
            animation.toogleNode(researchButton,0.50);
            data.clear();
            Random r = new Random();
            int rPort = 49152 + r.nextInt(65535 - 49152 + 1);
            rPort = 60000;
            UDPSearchServer.SearchServer(rPort,tableView, progressBar);
        }
    }
    @FXML
    public void toLogin(ActionEvent event){
        if(Login.serverAP[0].length()!=0&& Login.serverAP[1].length()!=0) {
            animation.toogleNode(nextButton, 0.50);
            animation.toogleNode(mainfx.backbutton,1);
            autotologin=false;
            animation.SlideLeft(getClass(), mainfx.mainpane, "Login.fxml");
        }
    }

    public static void chooseOneOption(ObservableList data2, Pane pane, Class c){
        if(data.size()==1){

            //Animation.OpenParent(pane.getScene(), FXMLLoader.load(c.getResource("Login.fxml")));
            //Animation.start(ServerList.class,pane.getScene(),FXMLLoader.load(getClass().getResource("ServerList.fxml")),FXMLLoader.load(getClass().getResource("Login.fxml")));
        }
    }

    public static void toTableView(TableView tableView, String msg, String address){
        msg = msg.replace("FiSy Server:","");
       data.add( new tablePerson.LoginPerson("",address,msg));
       tableView.setItems(data);
    }
}

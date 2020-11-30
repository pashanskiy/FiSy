package FiSy;

import FiSy.StoreData.SendData;
import FiSy.StoreData.StoreFile;
import FiSy.StoreData.StoreLogin;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.DirectoryChooser;

import javax.crypto.Cipher;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CountDownLatch;

public class FileDialog {
    @FXML TableColumn<tablePerson.FileDialogPerson,String> name;
    @FXML TableColumn<tablePerson.FileDialogPerson,String> size;
    @FXML TableColumn<tablePerson.FileDialogPerson,String> type;
    @FXML TableColumn<tablePerson.FileDialogPerson,String> datedate;
    @FXML Button folderBackButton;
    @FXML Button folderDownloadButton;
    @FXML Button deleteButton;
    @FXML Button adminMenu;
    @FXML Button lowUserMenu;
    public static Button lowusermenu;
    public static Button adminmenu;
    @FXML private TableView<tablePerson.FileDialogPerson> fileTableView = new TableView<>();
    static ObservableList<tablePerson.FileDialogPerson> data = FXCollections.observableArrayList();
    public static TableView filetableview;
    public static StoreFile inputStoreFile;
    public static ArrayList<String> currentDirectory = new ArrayList<>();
    public static ArrayList<String> directories= new ArrayList<>();
    public static Button folderbackbutton;
    public static StoreLogin.SUser subordinatedUsers;

    public void initialize(){
        filetableview=fileTableView;
        folderbackbutton=folderBackButton;
        lowusermenu=lowUserMenu;
        adminmenu=adminMenu;
        mainfx.oldScene="Login.fxml";
        mainfx.currentScene="FileDialog.fxml";
        currentDirectory = new ArrayList<>();
        directories= new ArrayList<>();
        name.setCellValueFactory(new PropertyValueFactory<tablePerson.FileDialogPerson,String>("name"));
        size.setCellValueFactory(new PropertyValueFactory<tablePerson.FileDialogPerson,String>("size"));
        type.setCellValueFactory(new PropertyValueFactory<tablePerson.FileDialogPerson,String>("type"));
        datedate.setCellValueFactory(new PropertyValueFactory<tablePerson.FileDialogPerson,String>("datedate"));
        fileTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        if(mainfx.lowuserNum>0){
            animation.toogleNode(lowUserMenu,1);
        }
        fileTableView.setOnDragOver(evt -> {
            if (evt.getDragboard().hasFiles()) {
                evt.acceptTransferModes(TransferMode.LINK);
            }
        });
        fileTableView.setOnDragDropped(evt -> {
            //text.setText(evt.getDragboard().getFiles().stream().map(File::getAbsolutePath).collect(Collectors.joining("\n")));
            List<File> filelist = evt.getDragboard().getFiles();
                //new Thread(()->EncryptFiles.enc(filelist,"")).start();
            animation.toogleNode(filetableview,0.8);
            animation.toogleNode(folderBackButton,0.8);
            animation.toogleNode(mainfx.backbutton,0.8);

            if(mainfx.userPower.equals("Administrator")){
                animation.toogleNode(adminMenu,0.8);
            }
            if(mainfx.lowuserNum>0){
                animation.toogleNode(lowUserMenu,0.8);
            }
            new Thread(()->EncryptFiles.enc(filelist, (ArrayList<String>) currentDirectory.clone())).start();
            evt.setDropCompleted(true);
        });

        fileTableView.setRowFactory( tv -> {
            TableRow<tablePerson.FileDialogPerson> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    intoFolder(row.getItem());
                }
            });
            return row ;
        });

        fileTableView.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ObservableList<tablePerson.FileDialogPerson> selectedItems = fileTableView.getSelectionModel().getSelectedItems();
                if (selectedItems.size()>0) {
                    animation.toogleNode(folderDownloadButton,1);
                    animation.toogleNode(deleteButton,1);
                }
                else {
                    animation.toogleNode(deleteButton,0.5);
                }
            }
        });
        if(mainfx.userPower.equals("Administrator")){
        animation.toogleNode(adminMenu,1);
        }
        DecryptFiles.init();
        getStoreFile();
        }

    @FXML
    public void adminMenuAction(ActionEvent event){
        animation.toogleNode(folderDownloadButton,0.5);
        animation.toogleNode(fileTableView,0.5);
        animation.toogleNode(deleteButton,0.5);
        animation.SlideDown(getClass(),mainfx.mainpane,"ControlDialog.fxml");
    }
        @FXML
    public void folderDownloadButtonAction(ActionEvent event){

        ObservableList<tablePerson.FileDialogPerson> selectedItems = fileTableView.getSelectionModel().getSelectedItems();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(Main.pStage);
        DecryptFiles.decryptAndSave(selectedItems,selectedDirectory,currentDirectory);
        if(selectedDirectory == null){
            //No Directory selected
        }else{
            System.out.println(selectedDirectory.getAbsolutePath());

            DecryptFiles.decryptAndSave(selectedItems,selectedDirectory,currentDirectory);
        }
    }

    @FXML public void lowUserMenuAction(ActionEvent event){
        animation.SlideDown(getClass(), mainfx.mainpane, "UsersControlDialog.fxml");
    }

        public static void getStoreFile(){
        if(inputStoreFile==null){
            SendData sendData = new SendData();
            sendData.setType("GetStoreFile");
            SocketClient.SendAesStream(SendData.toByte(sendData));}

            FileDialog.setStoreFileTableView(inputStoreFile);
        }

        public static void setStoreFileTableView(StoreFile storeFile){
        if(storeFile!=null){
        inputStoreFile = storeFile;
        toTableView(storeFile);
        } else inputStoreFile = new StoreFile();

        }

        private void intoFolder (tablePerson.FileDialogPerson rowData){
            if(rowData.getType().equals("Папка")){
                currentDirectory.add(rowData.getName());
                displayFolder();
                animation.toogleNode(folderBackButton,1);
            }
                else{animation.fadeShake(filetableview);}
        }
        public static void displayFolder(){
            data.clear();
            directories.clear();
            for(int i = 0; i<inputStoreFile.getSFile().size();i++) {
                String[] nametype = inputStoreFile.getSFile().get(i).getName().split("\\.(?=[^\\.]+$)");
                    if(checkFamilyFolder(currentDirectory,inputStoreFile.getSFile().get(i).getFilepath())){
                    if (nametype.length != 2) {
                        String name = nametype[0];
                        nametype = new String[2];
                        nametype[0] = name;
                        nametype[1] = "Без типа";
                    }
                if (isTwoArrayListsWithSameValues(currentDirectory, inputStoreFile.getSFile().get(i).getFilepath())) {
                    data.add(new tablePerson.FileDialogPerson(nametype[0], Long.toString(inputStoreFile.getSFile().get(i).getSize()), nametype[1], inputStoreFile.getSFile().get(i).getDate()));
                } else {
                    if (isTwoArrayListsWithSameValues(currentDirectory, inputStoreFile.getSFile().get(i).getFilepath())) {
                        data.add(new tablePerson.FileDialogPerson(nametype[0], Long.toString(inputStoreFile.getSFile().get(i).getSize()), nametype[1], inputStoreFile.getSFile().get(i).getDate()));
                    } else {
                        boolean folder = true;
                        for (String dirs : directories) {
                            if (dirs.equals(inputStoreFile.getSFile().get(i).getFilepath().get(currentDirectory.size())))
                                folder = false;
                        }
                        if (folder) {
                            directories.add(inputStoreFile.getSFile().get(i).getFilepath().get(currentDirectory.size()));
                            data.add(new tablePerson.FileDialogPerson(inputStoreFile.getSFile().get(i).getFilepath().get(currentDirectory.size()), "", "Папка", ""));
                        }

                    }
                }
            }
            }
            filetableview.setItems(data);
        }

        public static void toTableView(StoreFile storeFile){
            data.clear();
            directories.clear();
            for(int i = 0; i<storeFile.getSFile().size();i++){
                String[] nametype = storeFile.getSFile().get(i).getName().split("\\.(?=[^\\.]+$)");
                if(nametype.length!=2){
                    String name=nametype[0];
                    nametype = new String[2];
                    nametype[0]=name;
                    nametype[1]="Без типа";
                }
                if (storeFile.getSFile().get(i).getFilepath().size()==0) {
                    data.add(new tablePerson.FileDialogPerson(nametype[0], Long.toString(storeFile.getSFile().get(i).getSize()), nametype[1], storeFile.getSFile().get(i).getDate()));
                }else{
                    if (isTwoArrayListsWithSameValues(currentDirectory,storeFile.getSFile().get(i).getFilepath())){
                        data.add(new tablePerson.FileDialogPerson(nametype[0], Long.toString(storeFile.getSFile().get(i).getSize()), nametype[1], storeFile.getSFile().get(i).getDate()));
                    }else{
                        boolean folder = true;
                        if(storeFile.getSFile().get(i).getFilepath().size()>currentDirectory.size()) {
                            for (String dirs : directories) {
                                if (dirs.equals(storeFile.getSFile().get(i).getFilepath().get(currentDirectory.size())))
                                    folder = false;
                            }
                            if (folder) {
                                directories.add(storeFile.getSFile().get(i).getFilepath().get(currentDirectory.size()));
                                data.add(new tablePerson.FileDialogPerson(storeFile.getSFile().get(i).getFilepath().get(currentDirectory.size()), "", "Папка", ""));
                            }
                        }
                        }
                }
            }
            filetableview.setItems(data);
        }

        @FXML
        public void deleteButtonAction(ActionEvent event){
            ObservableList<tablePerson.FileDialogPerson> selectedItems = filetableview.getSelectionModel().getSelectedItems();
            ArrayList<StoreFile.SFile> ALsFile = new ArrayList<>();
            for (tablePerson.FileDialogPerson fileDialogPeople : selectedItems) {
                String nameAndType = fileDialogPeople.getName() + "." + fileDialogPeople.getType();
                for (StoreFile.SFile sFile : FileDialog.inputStoreFile.getSFile()) {
                    ArrayList<String> checkFile = new ArrayList<>();
                    checkFile = (ArrayList<String>) currentDirectory.clone();
                    checkFile.add(fileDialogPeople.getName());
                    if (nameAndType.equals(sFile.getName()) && FileDialog.isTwoArrayListsWithSameValues(sFile.getFilepath(), currentDirectory)) {
                        ALsFile.add(sFile);
                    } else if (fileDialogPeople.getType().equals("Папка") && FileDialog.checkFamilyFolder(checkFile, sFile.getFilepath())) {
                        ALsFile.add(sFile);
                    }
                }
            }
            StoreFile newStoreFile = new StoreFile();
            for(StoreFile.SFile inputsFile:inputStoreFile.getSFile()){
                boolean check=true;
                for(StoreFile.SFile alsFile:ALsFile){
                    if(inputsFile.equals(alsFile)){
                        check=false;
                    }
                }
                if(check)
                    newStoreFile.getSFile().add(inputsFile);

            }
            inputStoreFile=newStoreFile;
            SendData sendData = new SendData();
            sendData.setType("PrepareToReceiveFile");
            SocketClient.SendAesStream(SendData.toByte(sendData));
            EncryptFiles.sendStoreFile(inputStoreFile);
            ArrayList<String> UUIDtoDelete = new ArrayList<>();
            for(StoreFile.SFile sFile:ALsFile){
                UUIDtoDelete.add(sFile.getNameencryptedfile());
            }
            sendData = new SendData();
            sendData.setType("DeleteFiles");
            sendData.setObject(UUIDtoDelete);
            SocketClient.SendAesStream(SendData.toByte(sendData));
            displayFolder();
        }

    public static boolean isTwoArrayListsWithSameValues(ArrayList<String> list1, ArrayList<String> list2)
    {
        //null checking
        if(list1==null && list2==null)
            return true;
        if((list1 == null && list2 != null) || (list1 != null && list2 == null))
            return false;

        if(list1.size()!=list2.size())
            return false;
        for(Object itemList1: list1)
        {
            if(!list2.contains(itemList1))
                return false;
        }

        return true;
    }

    public void folderBackButtonAction(ActionEvent event){
        directories.clear();
        currentDirectory.remove(currentDirectory.size()-1);
        if(currentDirectory.size()==0){
        animation.toogleNode(folderBackButton,0.5);
        toTableView(inputStoreFile);
        } else{displayFolder();}
    }

    public static boolean checkFamilyFolder(ArrayList<String> list1, ArrayList<String> list2){
        {
            boolean check=true;
            if(list2.size()>=list1.size()){
                for(int i=0;i<list1.size();i++){
                    if(!list1.get(i).equals(list2.get(i)))
                        check=false;
                }
            } else return false;


            return check;
        }
    }
}

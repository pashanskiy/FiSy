package FiSy;

import FiSy.StoreData.StoreLogin;
import FiSy.StoreData.StoreRegister;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.net.*;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;

public class mainserver {

    @FXML
    Button toAdd;
    @FXML
    PasswordField passwordField;
    @FXML
    PasswordField passwordField2;
    @FXML TextField portField;

    DatagramSocket dsocket;
    double opacityFields=0.8;
    boolean ipP = false;
    int port = 10101;

    public void initialize(){

        checkAllFields(false);
        passwordField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                checkPassField(passwordField,newValue);
            }
        });
        passwordField2.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                checkPassField(passwordField2,newValue);
            }
        });
        portField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("^([0-9]{1,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$")) {
                    portField.setText(newValue.replaceAll("[^\\d]", ""));
                }
                    checkAllFields(false);

            }
        });
        try {
            dsocket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        monitoring(port);
    }

    private void checkPassField(TextField textField, String newValue){
        if (textField.getText().length() > 64) {
            String s = textField.getText().substring(0, 64);
            textField.setText(s);
            animation.fadeShake(textField);
        }
        checkAllFields(false);
    }

    private void checkAllFields(boolean effects){
        int checkFields=0;
        try{
        if (Integer.parseInt(portField.getText())>0&&Integer.parseInt(portField.getText())<65536) {
            if (effects) animation.fadeShake(portField);
            checkFields++;
            port=Integer.parseInt(portField.getText());
            portField.setOpacity(1);
        }else {portField.setOpacity(opacityFields);
        port=10101;
        };
        }catch (Exception e){portField.setOpacity(opacityFields);}

        if (!passwordField.getText().equals(passwordField2.getText())||passwordField.getText().length()<8||passwordField2.getText().length()<8){
            if (effects)animation.fadeShake(passwordField);
            if (effects) animation.fadeShake(passwordField2);
            passwordField.setOpacity(opacityFields);
            passwordField2.setOpacity(opacityFields);
        }else {
            checkFields++;
            passwordField.setOpacity(1);
            passwordField2.setOpacity(1);
        }
            if (checkFields>0){ animation.toogleNode(toAdd,1);}
            else{ toAdd.setOpacity(opacityFields);}
    }

    public void server(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("mainserver.fxml"));
        primaryStage.setTitle("");
        primaryStage.setResizable(false);
        primaryStage.setMaximized(false);
        primaryStage.initStyle(StageStyle.UNIFIED);
        primaryStage.setScene(new Scene(root, Color.rgb(23,23,25)));
        primaryStage.show();

        new DataBase();
        //for(int i =0;i<100;i++){
         //   Random r = new Random();
         //   int rPort = 40000 + r.nextInt(65535 - 40000 + 1);

    }//}

    @FXML
    void onClick(ActionEvent event){
    if(passwordField.getText().length()>7&&passwordField2.getText().length()>7&&passwordField.getText().length()<65&&passwordField2.getText().length()<65){
     setNewOwner();
    passwordField.setText("");
    passwordField2.setText("");
    passwordField.setOpacity(opacityFields);
    passwordField2.setOpacity(opacityFields);
    }
        try{
            if (Integer.parseInt(portField.getText())>0&&Integer.parseInt(portField.getText())<65536) {
            dsocket.close();
            dsocket = new DatagramSocket(port);
            port=Integer.parseInt(portField.getText());
            monitoring(port);
            }
    }
        catch (Exception e){e.printStackTrace();}
        animation.toogleNode(toAdd,0.5);
    }

    public void monitoring(int port) {
        new Thread(() -> {
            try {
                byte[] buffer = new byte[2048];
                DatagramPacket packet = new DatagramPacket(buffer,
                        buffer.length);
                while (true) {
                    System.out.println("Receiving...");
                    dsocket.receive(packet);
                    String msg = new String(buffer, 0, packet.getLength());
                    System.out.println(packet.getAddress().getHostName()
                            + ": " + msg);
                    sendServer(Integer.parseInt(msg.replace("FiSy Client:", "")), port);
                    // packet.setLength(buffer.length);
                }
            } catch (Exception e) {
                System.err.println("SOCKET CLOSE");
            }
        }).start();

        new  Thread(()-> {
            int clientcount=0;
            try {
                ServerSocket server= new ServerSocket(port);
                while (true) {
                    Socket p = server.accept();
                    try {
                        new Thread(()-> new ThreadSocket(p)).start();
                        clientcount++;
                        System.out.println(clientcount);
                    } catch (Exception e) {
                        System.err.println(e);
                    } finally {
                        System.out.println(clientcount);
                    }
                }}catch (Exception e){System.err.println(e);}
        }).start();
    }


    private void sendServer(int port, int port2) {
        try {
            byte[] message = ("FiSy Server:"+port2).getBytes();
            InetAddress address = InetAddress.getByName("255.255.255.255");
            DatagramSocket dsocket = new DatagramSocket();
            //dsocket.setBroadcast(true);
            DatagramPacket packet = new DatagramPacket(message, message.length,
                    address, port);
            dsocket.send(packet);
            dsocket.close();
        }catch (Exception e){
            System.err.println(e);
        }
    }

    public static String get_SHA_512_SecurePassword(String passwordToHash, byte[] salt)
    {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            byte[] bytes = md.digest(passwordToHash.getBytes());
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return generatedPassword;
    }
    public static byte[] getSalt()
    {
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            byte[] salt = new byte[16];
            sr.nextBytes(salt);
            return salt;
        } catch (Exception e){
            e.printStackTrace();
            return null;}

    }
    void setNewOwner(){
        StoreLogin storeLogin = new StoreLogin();

        try {
            byte[] salt = new byte[]{0, 1, 0, 9, 2, 0, 1, 4, 3, 0, 0, 6, 2, 0, 1, 8};
            Key key = new SecretKeySpec(Arrays.copyOf(get_SHA_512_SecurePassword(passwordField.getText(), salt).getBytes(), 32), "AES");
            Cipher aesCipher = Cipher.getInstance("AES/CFB8/NoPadding");
            aesCipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(salt));
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(4096);
            KeyPair kp = kpg.generateKeyPair();
            AESandRSA.PrivateKey = Base64.getEncoder().encodeToString(kp.getPrivate().getEncoded());
            AESandRSA.PublicKey = Base64.getEncoder().encodeToString(kp.getPublic().getEncoded());
            SecureRandom sr = new SecureRandom();
            byte[] DBkey = new byte[32];
            sr.nextBytes(DBkey);
            storeLogin.setRsaPubK(AESandRSA.PublicKey);
            storeLogin.setRsaPrivK(Base64.getEncoder().encodeToString(aesCipher.doFinal(Base64.getDecoder().decode(AESandRSA.PrivateKey))));
            storeLogin.setDatabaseKey(aesCipher.doFinal(DBkey));
            storeLogin.setPasswordsalt(getSalt());
            storeLogin.setPasswordhash(get_SHA_512_SecurePassword(passwordField.getText(),storeLogin.getPasswordsalt()));
            DataBase.ownerClear(storeLogin);
        } catch (Exception e){e.printStackTrace();}
    }
}

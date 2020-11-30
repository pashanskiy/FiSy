package FiSy;

import FiSy.StoreData.SendData;
import FiSy.StoreData.StoreLogin;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;

public class Login {
    @FXML public Pane pane;
    @FXML public Button loginButton;
    @FXML public TextField loginField;
    @FXML public PasswordField passwordField;
    @FXML public Button registerButton;
    @FXML public Label offLogin;
    public static Label offlogin;
    public static javafx.scene.control.Button loginbutton;
    public static javafx.scene.control.Button registerbutton;
    public static String serverAP[] = {"0","0"};
    public static PasswordField passwordfield;
    public static TextField loginfield;
    public static boolean onLoginButtonSocket;
    double opacityFields=0.8;

    public void initialize(){
        offlogin=offLogin;
        onLoginButtonSocket=false;
        checkAllFields(false);
        checkFields();
        FileDialog.inputStoreFile=null;
        loginfield=loginField;
        passwordfield=passwordField;
        loginbutton=loginButton;
        registerbutton=registerButton;
        mainfx.currentScene="Login.fxml";
        mainfx.oldScene="ServerList.fxml";
        loginField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("^[A-Z0-9a-z_@.]+$")) {
                    loginField.setText(newValue.replaceAll("[^\\aA-Z0-9a-z_@.]", ""));
                    animation.fadeShake(loginField);
                }
            }
        });
       new SocketClient();
    }

    @FXML
    public void toLogin(javafx.event.ActionEvent event){
        try {
            animation.toogleNode(loginField,0.5);
            animation.toogleNode(passwordField,0.5);
            animation.toogleNode(loginButton,0.5);
            animation.toogleNode(registerButton,0.5);

            byte[] salt = new byte[]{0, 1, 0, 9, 2, 0, 1, 4, 3, 0, 0, 6, 2, 0, 1, 8};
            mainfx.passKey = new SecretKeySpec(Arrays.copyOf(Login.get_SHA_512_SecurePassword(passwordField.getText(), salt).getBytes(),32), "AES");


            StoreLogin storeLogin = new StoreLogin();
            storeLogin.setData("GetPasswordSalt");
            storeLogin.setLogin(loginField.getText());
            SendData sendData = new SendData("UserLogin", storeLogin);
            SocketClient.SendAesStream(SendData.toByte(sendData));
        } catch (Exception e) {
            e.printStackTrace();}
    }

    @FXML
    public void toRegister(javafx.event.ActionEvent event){
        animation.toogleNode(loginField,0.5);
        animation.toogleNode(passwordField,0.5);
        animation.toogleNode(loginButton,0.5);
        animation.toogleNode(registerButton,0.5);
        animation.SlideDown(getClass(),mainfx.mainpane,"Register.fxml");
    }

    public void checkLoginButton(javafx.event.ActionEvent event){

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
    private void checkFields(){
        loginField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("^[A-Z0-9a-z_@.-]+$")) {
                    loginField.setText(newValue.replaceAll("[^\\aA-Z0-9a-z_@.-]", ""));
                    animation.fadeShake(loginField);
                }
                if (loginField.getText().length() > 32) {
                    String s = loginField.getText().substring(0, 32);
                    loginField.setText(s);
                    animation.fadeShake(loginField);
                }
                checkAllFields(false);
            }
        });

        passwordField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (passwordField.getText().length() > 64) {
                    String s = passwordField.getText().substring(0, 64);
                    passwordField.setText(s);
                    animation.fadeShake(passwordField);
                }
                checkAllFields(false);
            }
        });
    }

    private void checkAllFields(boolean effects) {
        int check=0;
        if (loginField.getText().length() < 4) {
            loginField.setOpacity(opacityFields);
            check++;
            if (effects) animation.fadeShake(loginField);
            animation.toogleNode(loginField,0.8, false);}
                else {animation.toogleNode(loginField,1);}

        if (passwordField.getText().length() < 8) {
            if (effects) animation.fadeShake(passwordField);
            check++;
                animation.toogleNode(passwordField,0.8, false);}
                else {animation.toogleNode(passwordField,1);}

                if(check==0){
                    animation.toogleNode(Login.loginbutton,1);
                }
                else{
                    animation.toogleNode(loginButton,0.5);
                }

    }
}

package FiSy;

import FiSy.StoreData.SendData;
import FiSy.StoreData.StoreRegister;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;

public class Register {
    @FXML
    TextField loginField;
    @FXML
    PasswordField passwordField;
    @FXML
    PasswordField passwordField2;
    @FXML
    TextField nameField;
    @FXML
    TextField surnameField;
    @FXML
    TextField middlenameField;
    @FXML
    TextField groupField;
    @FXML
    private Button registerButton;
    double opacityFields=0.8;
    public static TextField loginfield;

    public static Button registerbutton;

    public void initialize() {
        loginfield=loginField;
        registerbutton=registerButton;
        checkAllFields(false);
        mainfx.currentScene = "Register.fxml";
        mainfx.oldScene = "Login.fxml";

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
        nameField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                checkField(nameField,newValue);
            }
        });
        surnameField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
               checkField(surnameField,newValue);
            }
        });
        middlenameField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                checkField(middlenameField,newValue);
            }
        });
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
        groupField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("^[A-Z0-9a-z_@.-]+")) {
                    groupField.setText(newValue.replaceAll("[^\\aA-Z0-9a-z_@.-]", ""));
                    animation.fadeShake(groupField);
                }
                if (groupField.getText().length() > 32) {
                    String s = groupField.getText().substring(0, 32);
                    groupField.setText(s);
                    animation.fadeShake(groupField);
                }
                checkAllFields(false);
            }
        });
    }
    private void checkField(TextField textField, String newValue){
        if (!newValue.matches("^[A-Za-zА-Яа-яЁё]+$")) {
            textField.setText(newValue.replaceAll("[^\\aA-Za-zА-Яа-яЁё]", ""));
            animation.fadeShake(textField);
        }
        if (textField.getText().length() > 32) {
            String s = textField.getText().substring(0, 32);
            textField.setText(s);
            animation.fadeShake(textField);
        }
        checkAllFields(false);
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
        if (loginField.getText().length()<4) {
            loginField.setOpacity(opacityFields);
           if (effects) animation.fadeShake(loginField);
            checkFields++;
        } else loginField.setOpacity(1);

        if (nameField.getText().length()<2) {
            if (effects) animation.fadeShake(nameField);
            checkFields++;
            nameField.setOpacity(opacityFields);
        }else nameField.setOpacity(1);

        if (surnameField.getText().length()<2) {
            if (effects) animation.fadeShake(surnameField);
            checkFields++;
            surnameField.setOpacity(opacityFields);
        }else surnameField.setOpacity(1);

        if (middlenameField.getText().length()<2) {
            if (effects) animation.fadeShake(middlenameField);
            middlenameField.setOpacity(opacityFields);
        }else middlenameField.setOpacity(1);

        if (groupField.getText().length()<2) {
            if (effects) animation.fadeShake(groupField);
            groupField.setOpacity(opacityFields);
        }else groupField.setOpacity(1);

        if (!passwordField.getText().equals(passwordField2.getText())||passwordField.getText().length()<8||passwordField2.getText().length()<8){
            if (effects)animation.fadeShake(passwordField);
            if (effects) animation.fadeShake(passwordField2);
            checkFields++;
            passwordField.setOpacity(opacityFields);
            passwordField2.setOpacity(opacityFields);
        }else {
            passwordField.setOpacity(1);
            passwordField2.setOpacity(1);}

        if(checkFields==0)animation.toogleNode(registerButton,1);
        else animation.toogleNode(registerButton,0.5);
    }
    @FXML
    public void toRegister(ActionEvent event){
        try {
            animation.toogleNode(registerButton, 0.5);
            checkAllFields(true);
            String midname = null, group = null;
            if (middlenameField.getText().length() == 0) midname = "0";
            else midname = middlenameField.getText();
            if (groupField.getText().length() == 0) group = "0";
            else group = groupField.getText();
            StoreRegister storeRegister = new StoreRegister();
            storeRegister.setLogin(loginField.getText());
            storeRegister.setPasswordsalt(Login.getSalt());
            storeRegister.setPasswordhash(Login.get_SHA_512_SecurePassword(passwordField.getText(), storeRegister.getPasswordsalt()));
            storeRegister.setName(nameField.getText());
            storeRegister.setSurname(surnameField.getText());
            storeRegister.setMiddlename(midname);
            storeRegister.setGroup(group);
            byte[] salt = new byte[]{0, 1, 0, 9, 2, 0, 1, 4, 3, 0, 0, 6, 2, 0, 1, 8};
            Key key = new SecretKeySpec(Arrays.copyOf(Login.get_SHA_512_SecurePassword(passwordField.getText(), salt).getBytes(),32), "AES");
            Cipher aesCipher = Cipher.getInstance("AES/CFB8/NoPadding");
            aesCipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(salt));
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(4096); KeyPair kp = kpg.generateKeyPair();
            AESandRSA.PrivateKey= Base64.getEncoder().encodeToString(kp.getPrivate().getEncoded());
            AESandRSA.PublicKey=Base64.getEncoder().encodeToString(kp.getPublic().getEncoded());
            SecureRandom sr = new SecureRandom();
            byte[] DBkey = new byte[32];
            sr.nextBytes(DBkey);
            storeRegister.setRsaPubK(AESandRSA.PublicKey);
            storeRegister.setRsaPrivK(Base64.getEncoder().encodeToString(aesCipher.doFinal(Base64.getDecoder().decode(AESandRSA.PrivateKey))));
            storeRegister.setDatabaseKey(aesCipher.doFinal(DBkey));
            storeRegister.setData("ToRegister");
            SendData sendData = new SendData("UserRegister", storeRegister);
            SocketClient.SendAesStream(SendData.toByte(sendData));
            animation.SlideUp(getClass(), mainfx.mainpane, "Login.fxml");
        }catch (Exception e){e.printStackTrace();}
    }

}

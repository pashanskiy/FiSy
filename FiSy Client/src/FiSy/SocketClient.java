package FiSy;

import FiSy.StoreData.SendData;
import FiSy.StoreData.StoreLogin;
import FiSy.StoreData.StoreRegister;
import javafx.application.Platform;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.CountDownLatch;

public class SocketClient {
    public static Socket socket;
    public static DataInputStream dataInputStream;
    public static DataOutputStream dataOutputStream;
    public static CipherOutputStream cipherOutputStream;
    public static CipherInputStream cipherInputStream;
    public static Key key;
    public static Key SessionKey;
    public static CountDownLatch latch = new CountDownLatch(0);
    byte[] IV = new byte[16];
    public SocketClient(){
        try {
            socket = new Socket(Login.serverAP[0], Integer.parseInt(Login.serverAP[1]));
            socket.setReceiveBufferSize(socket.getReceiveBufferSize()*2);
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            CreateDHKey();
            Cipher aesInputCipher = Cipher.getInstance("AES/CFB8/NoPadding");
            aesInputCipher.init(Cipher.DECRYPT_MODE, key,new IvParameterSpec(IV));
            cipherInputStream = new CipherInputStream(socket.getInputStream(), aesInputCipher);

            Cipher aesOutputCipher = Cipher.getInstance("AES/CFB8/NoPadding");
            aesOutputCipher.init(Cipher.ENCRYPT_MODE, key,new IvParameterSpec(IV));
            cipherOutputStream = new CipherOutputStream(socket.getOutputStream(), aesOutputCipher);

           // new Thread(()->{ listenServer();}).start();
            new Thread(()->{ listenStreamServer();}).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void CreateDHKey() {
        try {
            BigInteger p = new BigInteger(dataInputStream.readUTF());
            BigInteger g = new BigInteger(dataInputStream.readUTF());
            BigInteger A = new BigInteger(dataInputStream.readUTF());
            SecureRandom r = new SecureRandom();
            BigInteger b = new BigInteger(512, r);
            BigInteger B = g.modPow(b, p);
            dataOutputStream.writeUTF(B.toString());
            BigInteger decryptionKeyClient = A.modPow(b, p);
            System.out.println("Calculated key: " + decryptionKeyClient);
            key = new SecretKeySpec(Arrays.copyOf(decryptionKeyClient.toByteArray(),32),"AES");
            IV=Arrays.copyOfRange(decryptionKeyClient.toByteArray(),32,48);
            System.out.println("key: " + key.getEncoded().toString());
            Login.onLoginButtonSocket=true;
            animation.toogleNode(Login.registerbutton,1);
        } catch (Exception e) { e.printStackTrace();}
    }

    public void listenStreamServer () {
        try {
            while (socket.isConnected()) {
                latch.await();
                byte[] buff = new byte[512];
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                boolean getsize=true;
                int bytesRead=0;
                Long size = Long.valueOf(1);
                Long size2 = Long.valueOf(0);
                while (size2<size) {
                    bytesRead = cipherInputStream.read(buff, 0, buff.length);
                    if(getsize){
                        size=ByteUtils.byteArrayToLong(Arrays.copyOfRange(buff,0,8));
                        getsize=false;
                    }
                    buffer.write(buff, 0, bytesRead);
                    size2= size2+bytesRead;
                }
                byte[] data = buffer.toByteArray();
                receiveCommand(Arrays.copyOfRange(data,8,data.length));
                System.out.println(new String("Success receive"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean SendAesStream(byte[] data){
        try {
            byte[] data2 = new byte[data.length+8];
            long size = data2.length;
            copyOfRangeToIndex(data2,0, ByteUtils.longToByteArray(size),0,8);
            copyOfRangeToIndex(data2,8, data,0,data.length);
            cipherOutputStream.write(data2, 0, data2.length);
            cipherOutputStream.flush();
            System.out.println(new String("Success send"));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean checkSendFile(String uuid){
        try {
        byte[] buff = new byte[512];
            int bytesRead = cipherInputStream.read(buff, 0, buff.length);
            if(uuid.equals(new String(Arrays.copyOf(buff,bytesRead)))){
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

        public static byte[] copyOfRangeToIndex(byte[] array1, int index, byte[] array2, int index2, int count2){
        for(int i=0;i<count2;i++)
            array1[index+i]=array2[index2+i];
        return array1;}

        private boolean receiveCommand(byte[] data) {
            SendData sendData = SendData.fromByte(data);
            switch (sendData.getType()) {
                case "UserRegister": {
                    StoreRegister storeRegister = (StoreRegister) sendData.getObject();

                    switch (storeRegister.getData()) {
                        case "SuccefulRegister": {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                            animation.toogleNode(Register.registerbutton, 0.5);
                            animation.SlideUp(getClass(), mainfx.mainpane, "Login.fxml");
                                }
                            });
                            break;
                        }

                        case "UnsuccessfullyRegister": {
                            animation wshake = new animation(mainfx.mainpane, event -> mainfx.mainpane.getScene());
                            wshake.starting();
                            animation.fadeShake(Register.loginfield);
                            animation.toogleNode(Register.registerbutton, 1);
                            //animation.SlideUp(getClass(),mainfx.mainpane,"Login.fxml");
                            break;
                        }
                    }
                    break;
                }
                case "UserLogin": {
                    StoreLogin storeLogin = (StoreLogin) sendData.getObject();

                    switch (storeLogin.getData()) {
                        case "SuccessfulTakeSalt": {
                            mainfx.userPower=storeLogin.getUserPower();
                            storeLogin.setLogin(Login.loginfield.getText());
                            storeLogin.setPasswordhash(Login.get_SHA_512_SecurePassword(Login.passwordfield.getText(), storeLogin.getPasswordsalt()));
                            storeLogin.setPasswordsalt(null);
                            storeLogin.setData("ToCheckHash");
                            sendData.setType("UserLogin");
                            sendData.setObject(storeLogin);
                            SendAesStream(SendData.toByte(sendData));
                            break;
                        }
                        case "SuccessfullyLogin": {
                            SessionKey = sendData.getKey();
                            mainfx.lowuserNum=Integer.parseInt(storeLogin.getType());
                            UsersControllerDialog.differentFolder=false;
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    animation.SlideLeft(getClass(), mainfx.mainpane, "FileDialog.fxml");
                                }
                            });
                            break;
                        }

                        case "GetSubordinatedUsers": {
                            storeLogin = (StoreLogin) sendData.getObject();
                            if(storeLogin.getSUser().size()>0){
                            for (StoreLogin.SUser sUser : storeLogin.getSUser()) {
                                byte[] bdkey = AESandRSA.decryptAES(mainfx.passKey, storeLogin.getDatabaseKey());
                                sUser.setUserDBkey(AESandRSA.encryptRSA(sUser.getPublickey(), bdkey));
                            }
                            }
                            AESandRSA.PublicKey = storeLogin.getRsaPubK();
                            AESandRSA.PrivateKey = Base64.getEncoder().encodeToString(AESandRSA.decryptAES(mainfx.passKey, Base64.getDecoder().decode(storeLogin.getRsaPrivK())));
                            mainfx.databaseUUID = storeLogin.getDirectory();
                            mainfx.databaseKey = new SecretKeySpec(AESandRSA.decryptAES(mainfx.passKey, storeLogin.getDatabaseKey()), "AES");
                            mainfx.currentdatabaseKey = mainfx.databaseKey;
                            sendData = new SendData();
                            sendData.setType("UserLogin");
                            storeLogin.setData("SetSubordinatedUsers");
                            sendData.setObject(storeLogin);
                            SendAesStream(SendData.toByte(sendData));
                            break;
                        }

                        case "UnsuccessfullyLogin": {
                            SendData finalSendData = sendData;
                            StoreLogin finalStoreLogin = storeLogin;
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    animation wshake = new animation(mainfx.mainpane, event -> mainfx.mainpane.getScene());
                                    wshake.playFromStart();
                                    Login.loginfield.clear();
                                    Login.passwordfield.clear();
                                    animation.fadeShake(Login.loginfield);
                                    animation.fadeShake(Login.passwordfield);
                                    Login.loginfield.setDisable(false);
                                    animation.toogleNode(Login.loginfield, 0.8, false);
                                    Login.passwordfield.setDisable(false);
                                    animation.toogleNode(Login.passwordfield, 0.8, false);
                                    animation.toogleNode(Login.loginbutton, 0.5);
                                    animation.toogleNode(Login.registerbutton, 1);
                                }
                            });
                            break;
                        }
                    }
                    break;
                }

                case "UserStoreFile": {
                    FileDialog.setStoreFileTableView(EncryptFiles.decryptStoreFile(sendData.getBytes()));
                    break;
                }

                case "CheckSendFile": {
                   EncryptFiles.latch.countDown();
                    break;
                }

                case "DisableListenerThread": {
                    latch = new CountDownLatch(1);
                    DecryptFiles.latch.countDown();
                    break;
                }

                case "ToAdminMenu": {
                    ControllerDialog.storeLogin = (StoreLogin)sendData.getObject();
                    ControllerDialog.data.clear();
                    for(StoreLogin.SUser sUser: ControllerDialog.storeLogin.getSUser()){
                        ControllerDialog.data.add(new tablePerson.AdminDialogPerson(Integer.toString(sUser.getUserID()),sUser.getName(),sUser.getSurname(),sUser.getMiddlename(),sUser.getGroup(),sUser.getLogin(),sUser.getDirectory(),sUser.getType()));
                    }
                    ControllerDialog.usertableview.setItems(ControllerDialog.data);
                    break;
                }

                case "SetLowUsersToAdminMenu": {
                   ArrayList<Integer> lu =(ArrayList<Integer>) sendData.getObject();
                   ControllerDialog2.toUserRight(lu);
                    break;
                }

                case "SetFullLowUsers": {
                UsersControllerDialog.storeLogin=(StoreLogin)sendData.getObject();
                UsersControllerDialog.setToTable();
                    break;
                }

                case "SetUserPublicBDKey":{
                StoreLogin.SUser sUser = (StoreLogin.SUser)sendData.getObject();
                mainfx.databaseKey= new SecretKeySpec(AESandRSA.decryptRSA(AESandRSA.PrivateKey,sUser.getUserDBKey()),"AES");
                FileDialog.inputStoreFile=null;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            animation.SlideUp(getClass(), mainfx.mainpane, "FileDialog.fxml");
                        }
                    });
                    break;
                }
                case "UserOff":{
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            animation wshake = new animation(mainfx.mainpane, event -> mainfx.mainpane.getScene());
                            wshake.playFromStart();
                            Login.loginfield.clear();
                            Login.passwordfield.clear();
                            animation.fadeShake(Login.loginfield);
                            animation.fadeShake(Login.passwordfield);
                            Login.loginfield.setDisable(false);
                            animation.toogleNode(Login.loginfield, 0.8, false);
                            Login.passwordfield.setDisable(false);
                            animation.toogleNode(Login.passwordfield, 0.8, false);
                            animation.toogleNode(Login.loginbutton, 0.5);
                            animation.toogleNode(Login.registerbutton, 1);
                            animation.toogleNode(Login.offlogin,1);
                        }
                    });
                }

            }
                return false;

        }
}

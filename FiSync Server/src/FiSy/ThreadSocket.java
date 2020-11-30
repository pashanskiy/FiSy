package FiSy;

import FiSy.StoreData.SendData;
import FiSy.StoreData.StoreFile;
import FiSy.StoreData.StoreLogin;
import FiSy.StoreData.StoreRegister;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.crypto.Data;
import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.file.FileStore;
import java.security.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

public class ThreadSocket {
    Socket socket;
    private Key key;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private InputStream cipherInputStream;
    private OutputStream cipherOutputStream;
    Key SessionKey;
    boolean checkKey = false;
    private String userL;
    private String userP;
    private String currentUserDirectory;
    private String userDirectory;
    private String userRSApubKey;
    private String userRSAprivKey;
    private String userPower;
    private int userid;
    private byte[] databaseKey;
    byte[] IV = new byte[16];

    public ThreadSocket(Socket socket) {
    this.socket = socket;
    SessionKey=getSessionKey();

try {
    socket.setReceiveBufferSize(socket.getReceiveBufferSize()*2);
    dataOutputStream = new DataOutputStream(socket.getOutputStream());
    dataInputStream = new DataInputStream(socket.getInputStream());

    diffieHellman();

    Cipher aesInputCipher = Cipher.getInstance("AES/CFB8/NoPadding");
    aesInputCipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(IV));
    cipherInputStream = new CipherInputStream(socket.getInputStream(), aesInputCipher);

    Cipher aesOutputCipher = Cipher.getInstance("AES/CFB8/NoPadding");
    aesOutputCipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(IV));
    cipherOutputStream = new CipherOutputStream(socket.getOutputStream(), aesOutputCipher);
    DataOutputStream dos = new DataOutputStream(cipherOutputStream);
    listenStreamClient();
}catch (Exception e){e.printStackTrace();}}

    private void listenStreamClient () {
        try {
                while (socket.isConnected()) {
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
                        clientCode(Arrays.copyOfRange(data,8,data.length));
                        System.out.println(new String("Success receive"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean SendStreamAes(byte[] data){
        try {
            byte[] data2 = new byte[data.length+8];
            long size = data2.length;
            copyOfRangeToIndex(data2,0, ByteUtils.longToByteArray(size),0,8);
            copyOfRangeToIndex(data2,8, data,0,data.length);
            cipherOutputStream.write(data2, 0, data2.length);
            System.out.println(new String("Success send"));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    private Key getSessionKey(){
        return new SecretKeySpec(new BigInteger(128, new SecureRandom()).toByteArray(),"AES");
    }

    private void diffieHellman(){
        try {
        Random r = new SecureRandom();
        AlgorithmParameterGenerator paramGen = null;
        paramGen = AlgorithmParameterGenerator.getInstance("DH");
        paramGen.init(512, new SecureRandom());
        AlgorithmParameters params = paramGen.generateParameters();
        DHParameterSpec dhSpec = (DHParameterSpec) params.getParameterSpec(DHParameterSpec.class);
        BigInteger a = new BigInteger(512, r); // secret key a (private) (on server)
        BigInteger p = dhSpec.getP(); // prime number (public) (generated on server)
        BigInteger g = dhSpec.getG(); // primer number generator (public) (generated on server)
        BigInteger A = g.modPow(a, p); // calculated public server key (A=g^a(modp))
        dataOutputStream.writeUTF(p.toString());
        dataOutputStream.writeUTF(g.toString());
        dataOutputStream.writeUTF(A.toString());
        BigInteger B = new BigInteger(dataInputStream.readUTF());
        BigInteger encryptionKeyServer = B.modPow(a, p);
        System.out.println("Calculated key: " + encryptionKeyServer);
        key = new SecretKeySpec(Arrays.copyOf(encryptionKeyServer.toByteArray(),32),"AES");
        IV=Arrays.copyOfRange(encryptionKeyServer.toByteArray(),32,48);
        System.out.println("key: " + key.getEncoded().toString());
    } catch (Exception e) {
        e.printStackTrace();
    }
    }

    public byte[] copyOfRangeToIndex(byte[] array1, int index, byte[] array2, int index2, int count2){
        for(int i=0;i<count2;i++)
            array1[index+i]=array2[index2+i];
        return array1;}

    private boolean createDirectory(String uuid){
        try {
            File file = new File(DataBase.mainDirectory + File.separator + uuid+File.separator+"00000000-0000-0000-0000-000000000000");
            if(!file.exists()){
            file.getParentFile().mkdirs();
            return true;}
        }catch (Exception e){e.printStackTrace();}
        return false;
    }

    private void clientCode(byte[] data){
        SendData sendData = SendData.fromByte(data);
    try{

    switch (sendData.getType()){
        case "UserRegister": {
            StoreRegister storeRegister = (StoreRegister)sendData.getObject();
            switch (storeRegister.getData()){
                case "ToRegister":{
            if(DataBase.userRegister(storeRegister)){
                storeRegister = new StoreRegister();
                storeRegister.setData("SuccefulRegister");
                sendData.setType("UserRegister");
                sendData.setObject(storeRegister);
                SendStreamAes(SendData.toByte(sendData));
                } break;
                }
             }
            break;
        }

        case "UserLogin":{
            StoreLogin storeLogin = (StoreLogin)sendData.getObject();

            switch (storeLogin.getData()){
                case "GetPasswordSalt":{
                    if(DataBase.userLoginGetPasswordSalt(storeLogin)){
                        userid=storeLogin.getUserId();
                        userL=storeLogin.getLogin();
                        userP=storeLogin.getPasswordhash();
                        userPower=storeLogin.getUserPower();
                        userDirectory=storeLogin.getDirectory();
                        currentUserDirectory=storeLogin.getDirectory();
                        userRSApubKey=storeLogin.getRsaPubK();
                        userRSAprivKey=storeLogin.getRsaPrivK();
                        databaseKey=storeLogin.getDatabaseKey();
                        storeLogin.setDirectory(null);
                        storeLogin.setPasswordhash(null);
                        storeLogin.setRsaPubK(null);
                        storeLogin.setRsaPrivK(null);
                        storeLogin.setDatabaseKey(null);
                        sendData = new SendData();

                            storeLogin.setData("SuccessfulTakeSalt");
                            sendData.setType("UserLogin");
                        if(storeLogin.getUserPower().equals("Off")){
                            sendData.setType("UserOff");}
                            sendData.setObject(storeLogin);

                        SendStreamAes(SendData.toByte(sendData));
                    }
                    else{
                        storeLogin = new StoreLogin();
                        storeLogin.setData("UnsuccessfullyLogin");
                        sendData = new SendData();
                        sendData.setType("UserLogin");
                        sendData.setObject(storeLogin);
                        SendStreamAes(SendData.toByte(sendData));
                    }
                    break;
                }

                case "ToCheckHash":{
                    if (userL.equals(storeLogin.getLogin())&&userP.equals(storeLogin.getPasswordhash())){
                        storeLogin.setData("GetSubordinatedUsers");
                        storeLogin.setPasswordhash(null);
                        storeLogin.setPasswordsalt(null);
                        createDirectory(userDirectory);
                        DataBase.getHighUsers(storeLogin);
                        storeLogin.setDirectory(userDirectory);
                        storeLogin.setDatabaseKey(databaseKey);
                        storeLogin.setRsaPubK(userRSApubKey);
                        storeLogin.setRsaPrivK(userRSAprivKey);
                        sendData.setObject(storeLogin);
                        sendData.setType("UserLogin");
                        sendData.setKey(SessionKey);
                        SendStreamAes(SendData.toByte(sendData));
                    }
                    else {
                        storeLogin = new StoreLogin();
                        storeLogin.setData("UnsuccessfullyLogin");
                        sendData = new SendData();
                        sendData.setType("UserLogin");
                        sendData.setObject(storeLogin);
                        SendStreamAes(SendData.toByte(sendData));
                    }
                    break;
                }

                case "SetSubordinatedUsers":{
                    sendData=new SendData();
                    sendData.setType("UserLogin");
                    storeLogin.setData("SuccessfullyLogin");
                    DataBase.setKeyToHighUsers(storeLogin);
                    ArrayList<Integer> integers = DataBase.getLowUsers(storeLogin);
                    storeLogin.setType(Integer.toString(integers.size()));
                    sendData.setObject(storeLogin);
                    SendStreamAes(SendData.toByte(sendData));
                    break;
                }

            }
            break;
        }

        case "GetStoreFile":{
            sendData = new SendData();
            sendData.setType("UserStoreFile");
            sendData.setBytes(FileEngine.getStoreFile(DataBase.mainDirectory,userDirectory));
            SendStreamAes(SendData.toByte(sendData));
            break;
        }

        case "PrepareToReceiveFile":{
            FileEngine.receiveAdndSaveFile(cipherInputStream,cipherOutputStream,userDirectory);
            //byte[] c=sFile.getNameencryptedfile().getBytes();
            //Thread.sleep(500);
            //cipherOutputStream.write(c,0,c.length);
            break;
        }

        case "PrepareToSendFile":{
            StoreFile.SFile sFile = (StoreFile.SFile)sendData.getObject();
            File file = new File(DataBase.mainDirectory+File.separator+userDirectory+File.separator+sFile.getNameencryptedfile());
            FileEngine.sendFiles(cipherOutputStream,file);
            break;
        }
        case "DisableGlobalThread":{
            sendData = new SendData();
            sendData.setType("DisableListenerThread");
            SendStreamAes(SendData.toByte(sendData));
            break;
        }
        case "DeleteFiles":{
            FileEngine.fileDelete(userDirectory,(ArrayList<String>)sendData.getObject());
            break;
        }

        case "GetUsersForAdmin":{
            if(userPower.equals("Administrator")) {
                StoreLogin storeLogin = new StoreLogin();
                DataBase.getAllUsers(storeLogin);
                sendData = new SendData();
                sendData.setType("ToAdminMenu");
                sendData.setObject(storeLogin);
                SendStreamAes(SendData.toByte(sendData));
            }
            break;
        }

        case "GetLowUsersToAdminMenu":{
            if(userPower.equals("Administrator")) {
                StoreLogin storeLogin = (StoreLogin) sendData.getObject();
                ArrayList<Integer> lu = DataBase.getLowUsers(storeLogin);
                sendData.setType("SetLowUsersToAdminMenu");
                sendData.setObject(lu);
                SendStreamAes(SendData.toByte(sendData));
            }
            break;
        }

        case "SetNewUserRules":{
            ArrayList<Integer[]> numtoNewLowUsers = new ArrayList<>();
            numtoNewLowUsers = (ArrayList<Integer[]>)sendData.getObject();
            DataBase.setNewUsersRules(numtoNewLowUsers);
            break;
        }

        case "SetNewUsersRight":{
            ArrayList<String[]> newRightToUsers = new ArrayList<>();
            newRightToUsers = (ArrayList<String[]>)sendData.getObject();
            if(newRightToUsers.size()>0) DataBase.setNewUsersRights(newRightToUsers);
            break;
        }

        case "GetFullLowUsers":{
            StoreLogin storeLogin = new StoreLogin();
            storeLogin.setUserId(userid);
            ArrayList<Integer>lu=DataBase.getLowUsersWithPass(storeLogin);
            storeLogin=DataBase.getUsers(lu);
            sendData=new SendData();
            sendData.setType("SetFullLowUsers");
            sendData.setObject(storeLogin);
            SendStreamAes(SendData.toByte(sendData));
            break;
        }

        case "GetUserDBKeyLikePublic":{
            Integer[] user =new Integer[2];
            user[0]=(Integer)sendData.getObject();
            user[1]=userid;
            StoreLogin.SUser sUser = DataBase.getLowUserPass(user);
            sendData=new SendData();
            sendData.setType("SetUserPublicBDKey");
            userDirectory=sUser.getDirectory();
            createDirectory(userDirectory);
            sUser.setDirectory(null);
            sendData.setObject(sUser);
            SendStreamAes(SendData.toByte(sendData));
            break;
        }

        case "GoToMainFolder":{
            userDirectory=currentUserDirectory;
        }
    }

} catch (Exception e) {
        e.printStackTrace();}
    }
}
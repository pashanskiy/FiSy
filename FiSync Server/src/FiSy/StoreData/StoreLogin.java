package FiSy.StoreData;

import java.io.*;
import java.util.ArrayList;

public class StoreLogin implements Serializable {
    private int userid;
    private String type;
    private String login;
    private String passwordhash;
    private byte[] passwordsalt;
    private String ddata;
    private String userpower;
    private String directory;
    private String rsaPubK;
    private String rsaPrivK;
    private byte[] databaseKey;
    private ArrayList<SUser> sUsers = new ArrayList<>();

    public ArrayList<SUser> getSUser(){
        return sUsers;
    }

    public StoreLogin(String Login,String PasswordHash, byte[] PasswordSalt, String data, String Directory){
        this.type=new String("UserLogin");
        this.login=Login;
        this.passwordhash=PasswordHash;
        this.passwordsalt=PasswordSalt;
        this.ddata=data;
        this.directory=Directory;
    }

    public StoreLogin(){
        this.type=new String("UserLogin");
        this.login=null;
        this.passwordhash=null;
        this.passwordsalt=null;
        this.ddata=null;
        this.directory=null;
        this.rsaPubK =null;
        this.rsaPrivK=null;
        this.databaseKey=null;
        this.userid=0;
        this.userpower=null;
    }

    public String getType() {
        return type;
    }

    public void setType(String Type) {
        type = Type;
    }

    public String getData() {
        return ddata;
    }

    public void setData(String data) {
        ddata = data;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String Login) {
        login = Login;
    }

    public String getPasswordhash() {
        return passwordhash;
    }

    public void setPasswordhash(String PasswordHash) {
        passwordhash = PasswordHash;
    }

    public byte[] getPasswordsalt() {
        return passwordsalt;
    }

    public void setPasswordsalt(byte[] PasswordSalt) {
        passwordsalt = PasswordSalt;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String Directory) {
        directory = Directory;
    }

    public String getRsaPubK() {
        return rsaPubK;
    }

    public void setRsaPubK(String rsaPublicKey) {
        rsaPubK = rsaPublicKey;
    }

    public String getRsaPrivK() {
        return rsaPrivK;
    }

    public void setRsaPrivK(String rsaPrivateKey) {
        rsaPrivK = rsaPrivateKey;
    }

    public byte[] getDatabaseKey() {
        return databaseKey;
    }

    public void setDatabaseKey(byte[] DataBaseKey) {
        databaseKey = DataBaseKey;
    }

    public int getUserId() {
        return this.userid;
    }

    public void setUserId(int UserId) {
        userid = UserId;
    }

    public String getUserPower() {
        return this.userpower;
    }

    public void setUserPower(String userPower) {
        userpower = userPower;
    }

    public static byte[] toByte(StoreLogin message){
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(message);
            oos.flush();
            return bos.toByteArray();
        } catch (Exception e){e.printStackTrace();
            return null;
        }
    }

    public static StoreLogin fromByte(byte[] bytes){
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            StoreLogin msg = (StoreLogin) ois.readObject();
            return msg;
        } catch (Exception e){e.printStackTrace();
            return null;
        }
    }





    public static class SUser implements Serializable {
        private String name;
        private String login;
        private String type;
        private String surname;
        private String middlename;
        private String group;
        private String publickey;
        private String directory;
        private byte[] userDBkey;
        private String userdirectory;
        private int userid;

        public SUser() {
            this.name = null;
            this.surname = null;
            this.middlename = null;
            this.group = null;
            this.publickey = null;
            this.userDBkey = null;
            this.userdirectory=null;
            this.login=null;
            this.type=null;
            this.directory=null;
        }

        public String getName() {
            return name;
        }

        public void setName(String Type) {
            name = Type;
        }

        public String getPublickey() {
            return publickey;
        }

        public void setPublickey(String data) {
            publickey = data;
        }

        public String getSurname() {
            return surname;
        }

        public void setSurname(String Surname) {
            surname = Surname;
        }

        public String getMiddlename() {
            return middlename;
        }

        public void setMiddlename(String Size) {
            middlename = Size;
        }

        public String getGroup() {
            return group;
        }

        public void setGroup(String Group) {
            group = Group;
        }

        public byte[] getUserDBKey() {
            return userDBkey;
        }

        public void setUserDBkey(byte[] UserDBkey) { userDBkey = UserDBkey; }

        public String getUserDirectory() {
            return userdirectory;
        }

        public void setUserdirectory(String UserDirectory) {
            userdirectory = UserDirectory;
        }

        public int getUserID() {
            return userid;
        }

        public void setUserid(int UserID) {
            userid = UserID;
        }

        public String getLogin() {
            return login;
        }

        public void setLogin(String Login) {
            login = Login;
        }

        public String getType() {
            return type;
        }

        public void setType(String Type) {
            type = Type;
        }

        public String getDirectory() {
            return directory;
        }

        public void setDirectory(String Directory) {
            directory = Directory;
        }
    }

}
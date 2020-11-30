package FiSy.StoreData;

import java.io.*;

public class StoreRegister implements Serializable {
        private String type;
        private String login;
        private String passwordhash;
        private byte[] passwordsalt;
        private String name;
        private String surname;
        private String middlename;
        private String group;
        private String gdata;
        private String rsaPubK;
        private String rsaPrivK;
        private byte[] databaseKey;

        public StoreRegister(String Login, String PasswordHash, byte[] PasswordSalt, String Name, String Surname, String Middlename, String Group, String data) {
            this.type = new String("UserRegister");
            this.login = Login;
            this.passwordhash = PasswordHash;
            this.passwordsalt = PasswordSalt;
            this.name=Name;
            this.surname=Surname;
            this.middlename=Middlename;
            this.group=Group;
            this.gdata=data;
        }

    public StoreRegister() {
        this.type = new String("UserRegister");
        this.login = null;
        this.passwordhash = null;
        this.passwordsalt = null;
        this.name=null;
        this.surname=null;
        this.middlename=null;
        this.group=null;
        this.gdata=null;
        this.rsaPubK =null;
        this.rsaPrivK =null;
        this.databaseKey=null;
    }

        public String getType() {
            return type;
        }

        public void setType(String Type) {
            type = Type;
        }

        public String getData() {
        return gdata;
    }

        public void setData(String data) {
        gdata = data;
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

        public byte[] getPasswordsalt(){
            return passwordsalt;
        }

        public void setPasswordsalt(byte[] PasswordSalt){
            passwordsalt = PasswordSalt;
        }

        public String getName() {
            return name;
        }

        public void setName(String Name) {
            name = Name;
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

        public void setMiddlename(String Middlename) {
            middlename = Middlename;
        }

        public String getGroup() {
            return group;
        }

        public void setGroup(String Group) {
            group = Group;
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

    public static byte[] toByte(StoreRegister message){
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

    public static StoreRegister fromByte(byte[] bytes){
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            StoreRegister msg = (StoreRegister)ois.readObject();
            return msg;
        } catch (Exception e){e.printStackTrace();
            return null;
        }
    }
}

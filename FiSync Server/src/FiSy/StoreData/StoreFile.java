package FiSy.StoreData;

import java.io.*;
import java.util.ArrayList;

public class StoreFile implements Serializable{

    private ArrayList<SFile> files;

    public ArrayList<SFile> getSFile(){
        return files;
    }

    public int getSize(){
        return files.size();
    }

    public StoreFile(){
        files = new ArrayList<SFile>();;
    }

    public void addFile(StoreFile.SFile sFile){
        files.add(sFile);
    }

    public static class SFile implements Serializable {
        private String name;
        private String date;
        private long size;
        private String nameencryptedfile;
        private ArrayList<String> filepath;
        private boolean executable;
        private long encsize;

        public SFile() {
            this.name = null;
            this.date = null;
            this.size = 0;
            this.nameencryptedfile = null;
            this.filepath = null;
            this.executable = false;
            this.encsize=0;
        }

        public SFile(String Name, String Date, long Size, ArrayList<String> FilePath, String EncryptedFileName, boolean Executable) {
            this.name = Name;
            this.date = Date;
            this.size = Size;
            this.nameencryptedfile = EncryptedFileName;
            this.filepath = FilePath;
            this.executable = Executable;
        }

        public String getName() {
            return name;
        }

        public void setName(String Type) {
            name = Type;
        }

        public ArrayList<String> getFilepath() {
            return filepath;
        }

        public void setFilepath(ArrayList<String> data) {
            filepath = data;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String Login) {
            date = Login;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long Size) {
            size = Size;
        }

        public long getEncryptedSize() {
            return encsize;
        }

        public void setEncryptedSize(long EncSize) {
            encsize = EncSize;
        }

        public String getNameencryptedfile() {
            return nameencryptedfile;
        }

        public void setNameencryptedfile(String PasswordSalt) { nameencryptedfile = PasswordSalt; }

        public boolean getExecutable() {
            return executable;
        }

        public void setExecutable(boolean Executable) {
            executable = Executable;
        }
    }

    public static byte[] toByte(StoreFile message) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(message);
            oos.flush();
            return bos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static StoreFile fnromByte(byte[] bytes) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            StoreFile msg = (StoreFile) ois.readObject();
            return msg;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
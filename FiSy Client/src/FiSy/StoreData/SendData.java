package FiSy.StoreData;

import java.io.*;
import java.security.Key;

public class SendData implements Serializable {
        private String type;
        private Object object;
        private byte[] bytes;
        private Key key;

        public SendData(String Type, Object object){
            this.type=Type;
            this.object=object;
            this.key=null;
            this.bytes=null;
        }

    public SendData(){
        this.type=null;
        this.object=null;
        this.key=null;
        this.bytes=null;
    }

        public String getType() {
            return type;
        }

        public void setType(String Type) {
            type = Type;
        }

        public Object getObject() {
            return object;
        }

        public void setObject(Object object) {
            this.object = object;
        }

        public Key getKey() {
        return key;
    }

        public void setKey(Key key) {
        this.key = key;
    }

        public byte[] getBytes() {
        return bytes;
    }

        public void setBytes(byte[] Bytes) {
        this.bytes = Bytes;
    }



    public static byte[] toByte(SendData message){
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

    public static SendData fromByte(byte[] bytes){
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            SendData msg = (SendData) ois.readObject();
            return msg;
        } catch (Exception e){e.printStackTrace();
            return null;
        }
    }
}

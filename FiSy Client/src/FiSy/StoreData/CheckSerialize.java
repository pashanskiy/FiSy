package FiSy.StoreData;

import java.io.*;

public class CheckSerialize implements Serializable{

    public static byte[] serialize(Object cl) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutput os = new ObjectOutputStream(out);
            os.writeObject(cl);
            return out.toByteArray();
        }catch (Exception e){e.printStackTrace();}
        return null;
    }

   public static boolean isStoreLogin(byte[] storeClass) {
       if (StoreLogin.class==getObject(storeClass)){
           return true;
       }
       return false;
    }

    public static boolean isStoreRegister(byte[] storeClass) {
        if (StoreRegister.class==getObject(storeClass)){
            return true;
        }
        return false;
    }

    private static Object getObject(byte[] getClass){
       try{
        ByteArrayInputStream in = new ByteArrayInputStream(getClass);
        ObjectInput is = new ObjectInputStream(in);
        return is.readObject();
    } catch (Exception e) {
        e.printStackTrace();}
            return null;
    }

    public static byte[] objectToByte(Object message){
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

    public static Object objectFromByte(byte[] bytes){
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            Object msg = (Object) ois.readObject();
            return msg;
        } catch (Exception e){e.printStackTrace();
            return null;
        }
    }
}

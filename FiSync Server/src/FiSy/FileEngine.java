package FiSy;

import FiSy.StoreData.CheckSerialize;
import FiSy.StoreData.SendData;
import FiSy.StoreData.StoreFile;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.lang.reflect.Array;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

public class FileEngine {

    public static void fileDelete(String userDirectory,ArrayList<String> filesToDelete){
    for(String fileName: filesToDelete){
        File file = new File(DataBase.mainDirectory + File.separator + userDirectory + File.separator +fileName);
        file.delete();
    }

    }

    public static byte[] getStoreFile(String serverDir, String userDir){
        String path = serverDir+File.separator+userDir+File.separator+"00000000-0000-0000-0000-000000000000";
        if(new File(path).exists()){
            try {
                return Files.readAllBytes(Paths.get(path));
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    public static void receiveAdndSaveFile(InputStream cipherInputStream, OutputStream cipherOutputStream, String userDirectory){
        try {
            SendData checkSendData = new SendData();
            checkSendData.setType("CheckSendFile");
            byte[] sdb =SendData.toByte(checkSendData);
            byte[] s = new byte[sdb.length+8];
            copyOfRangeToIndex(s,0, ByteUtils.longToByteArray(sdb.length),0,8);
            copyOfRangeToIndex(s,8, sdb,0,sdb.length);
            boolean fileinput = true;
            SendData sendData = new SendData();
            StoreFile.SFile sFile = new StoreFile.SFile();
            while (fileinput) {
                int checkFile=0;
                byte[] buff = new byte[512];
                Long size = Long.valueOf(1);
                Long size2 = Long.valueOf(1);
                boolean check = true;
                int bytesRead;
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                while (size > 0) {
                    bytesRead = cipherInputStream.read(buff, 0, buff.length);
                    buffer.write(buff, 0, bytesRead);
                    if (check) {
                        size = ByteUtils.byteArrayToLong(Arrays.copyOfRange(buff, 0, 8));
                        size2 = ByteUtils.byteArrayToLong(Arrays.copyOfRange(buff, 8, 16));
                        check = false;
                    }
                    size = size - bytesRead;
                }

                sendData = SendData.fromByte(Arrays.copyOfRange(buffer.toByteArray(), 16, buffer.size()));
                switch (sendData.getType()){
                    case "LastFile":{
                        fileinput = false;
                        sFile=(StoreFile.SFile)sendData.getObject();
                    }
                    break;
                    case "File":{
                        sFile=(StoreFile.SFile)sendData.getObject();
                    }
                    break;
                }
                OutputStream os = new FileOutputStream(DataBase.mainDirectory + File.separator + userDirectory + File.separator + sFile.getNameencryptedfile());
                cipherOutputStream.write(s,0,s.length);
                while (size2 > 0) {
                    if (size2 - 512 < 0) {
                        buff = new byte[size2.intValue()];
                    }
                    bytesRead = cipherInputStream.read(buff, 0, buff.length);
                    os.write(buff, 0, bytesRead);
                    size2 = size2 - bytesRead;
                }
                os.close();
                cipherOutputStream.write(s,0,s.length);


                System.out.println("Success Save File " + sFile.getEncryptedSize() + " " + sFile.getNameencryptedfile());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendFiles( OutputStream cipherOutputStream, File file){
        try {
        InputStream is = new FileInputStream(file);
        int numRead = 0;
        byte[] buf = new byte[512];
        while ((numRead = is.read(buf)) >= 0) {

                cipherOutputStream.write(buf, 0, numRead);

        }
        is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] copyOfRangeToIndex(byte[] array1, int index, byte[] array2, int index2, int count2){
        for(int i=0;i<count2;i++)
            array1[index+i]=array2[index2+i];
        return array1;}

    static public String ExportResource(String resourceName) throws Exception {
        InputStream stream = null;
        OutputStream resStreamOut = null;
        String jarFolder;
        try {
            stream = FileEngine.class.getResourceAsStream(resourceName);//note that each / is a directory down in the "jar tree" been the jar the root of the tree
            if(stream == null) {
                throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
            }
            File file = new File("."+File.separator+"FiSy Server Recources"+File.separator);
            file.mkdirs();
            int readBytes;
            byte[] buffer = new byte[4096];
            //jarFolder = new File(FileEngine.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile().getPath().replace('\\', '/');
            resStreamOut = new FileOutputStream("."+File.separator+"FiSy Server Recources"+File.separator+"11111111-1111-1111-1111-111111111111");
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            stream.close();
            resStreamOut.close();
        }

        return resourceName;
    }
}


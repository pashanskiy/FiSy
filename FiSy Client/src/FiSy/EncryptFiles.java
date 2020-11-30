package FiSy;

import FiSy.StoreData.CheckSerialize;
import FiSy.StoreData.SendData;
import FiSy.StoreData.StoreFile;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;

import static FiSy.SocketClient.copyOfRangeToIndex;
import static FiSy.SocketClient.dataInputStream;

public class EncryptFiles {
    //public static byte[] b = new byte[]{1, 7, 0, 2, 1, 9, 9, 9, 3, 0, 0, 3, 1, 9, 9, 8, 1, 7, 0, 2, 1, 9, 9, 9, 3, 0, 0, 3, 1, 9, 9, 8};
    //public static Key key = new SecretKeySpec(b, "AES");
    public static byte[] IV = new byte[]{0, 9, 0, 8, 2, 0, 1, 4, 2, 3, 0, 6, 2, 0, 1, 8};
    public static CountDownLatch latch = new CountDownLatch(1);
    public static CipherOutputStream cos;
    public static void enc(List<File> fileList, ArrayList<String> outDir) {


        StoreFile storeFile = FileDialog.inputStoreFile;
        ArrayList<String> directories = new ArrayList<>();

        SendData sendData = new SendData();
        sendData.setType("PrepareToReceiveFile");


        try {
            Cipher aesCipher = Cipher.getInstance("AES/CFB8/NoPadding");
            aesCipher.init(Cipher.ENCRYPT_MODE, mainfx.databaseKey, new IvParameterSpec(IV));
            cos = new CipherOutputStream(SocketClient.cipherOutputStream, aesCipher);

            SocketClient.SendAesStream(SendData.toByte(sendData));
            for(int i = 0;i<fileList.size();i++){
                directories.clear();
                for(String dirS: outDir){
                    directories.add(dirS);
                }
                if (fileList.get(i).isDirectory()) directories.add(fileList.get(i).getName());
                encryptFile(fileList.get(i), storeFile, outDir, directories);
            }


            //FileOutputStream fos = new FileOutputStream(fol2+"00000000-0000-0000-0000-000000000000");
            FileDialog.inputStoreFile = storeFile;
            sendStoreFile(storeFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendStoreFile(StoreFile storeFile){
        try {
            StoreFile.SFile sFile = new StoreFile.SFile();

            byte[] sfb = StoreFile.toByte(storeFile);
            sFile.setNameencryptedfile("00000000-0000-0000-0000-000000000000");
            sFile.setEncryptedSize(sfb.length);
            SendData sendData = new SendData();
            sendData.setType("LastFile");
            sendData.setObject(sFile);
            //SocketClient.SendAesStream(SendData.toByte(sendData));


            //OutputStream os = new CipherOutputStream(SocketClient.cipherOutputStream,aesCipher);
            //os.flush();
            //SocketClient.cipherOutputStream.flush();

            Cipher aesCipher = Cipher.getInstance("AES/CFB8/NoPadding");
            aesCipher.init(Cipher.ENCRYPT_MODE, mainfx.databaseKey, new IvParameterSpec(IV));
            cos = new CipherOutputStream(SocketClient.cipherOutputStream, aesCipher);

            byte[] buf = new byte[512];
            int numRead = 0;
            byte[] sfdata = SendData.toByte(sendData);
            byte[] data = new byte[16 + sfdata.length];
            copyOfRangeToIndex(data, 0, ByteUtils.longToByteArray(data.length), 0, 8);
            copyOfRangeToIndex(data, 8, ByteUtils.longToByteArray(sfb.length), 0, 8);
            copyOfRangeToIndex(data, 16, sfdata, 0, sfdata.length);
            int senddata = 0;
            int checkSendFile = 0;
            while (senddata < data.length) {
                int ssdata = 512;
                if ((data.length - senddata) < 512) ssdata = data.length - senddata;
                SocketClient.cipherOutputStream.write(data, senddata, ssdata);
                senddata += 512;
            }
            senddata = 0;
            latch.await();
            latch = new CountDownLatch(1);
            cos.flush();
            while (senddata < sfb.length) {
                int ssdata = 512;
                if ((sfb.length - senddata) < 512) ssdata = sfb.length - senddata;
                cos.write(sfb, senddata, ssdata);
                senddata += 512;
            }
            latch.await();
            latch = new CountDownLatch(1);
            FileDialog.displayFolder();
            animation.toogleNode(FileDialog.filetableview, 1);
            animation.toogleNode(mainfx.backbutton, 1);
            if(mainfx.userPower.equals("Administrator")){
                animation.toogleNode(FileDialog.adminmenu,1);
            }
            if(mainfx.lowuserNum>0){
                animation.toogleNode(FileDialog.lowusermenu,1);
            }
            if (FileDialog.directories.size() > 0) animation.toogleNode(FileDialog.folderbackbutton, 1);
        }catch (Exception e){e.printStackTrace();}
    }

    public static void encryptFile(File dir, StoreFile storeFile, ArrayList<String> outDir, ArrayList<String> directories) {
        try {
            if(dir.isFile()) encrypAndSendFile(dir,storeFile,outDir,directories);
            File[] files = dir.listFiles();
            for (File file : files) {
                encrypAndSendFile(file,storeFile,outDir,directories);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (directories.size() > 0) directories.remove(directories.size() - 1);
    }

    private static void encrypAndSendFile(File file, StoreFile storeFile, ArrayList<String> outDir, ArrayList<String> directories){
        try {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            if (file.isDirectory()) {
                directories.add(file.getName());
                encryptFile(file, storeFile, outDir, directories);
            } else {
                String uuid = null;
                int id = 0;
                boolean run = true;
                if ((id = getIndex(FileDialog.inputStoreFile, file.getName(), directories)) == 0) {
                    while (run) {
                        uuid = UUID.randomUUID().toString();
                        if (!searchUUID(FileDialog.inputStoreFile, uuid)) {
                            run = false;
                        }
                    }
                    if (file.canExecute())
                        storeFile.addFile(new StoreFile.SFile(file.getName(), sdf.format(date), file.length(), (ArrayList<String>) directories.clone(), uuid, true));
                    else
                        storeFile.addFile(new StoreFile.SFile(file.getName(), sdf.format(date), file.length(), (ArrayList<String>) directories.clone(), uuid, false));

                } else {
                    id -= 1;
                    if (file.canExecute()) {
                        uuid = storeFile.getSFile().get(id).getNameencryptedfile();
                        storeFile.getSFile().get(id).setDate(sdf.format(date));
                        storeFile.getSFile().get(id).setSize(file.length());
                        storeFile.getSFile().get(id).setExecutable(true);
                    } else {
                        uuid = storeFile.getSFile().get(id).getNameencryptedfile();
                        storeFile.getSFile().get(id).setDate(sdf.format(date));
                        storeFile.getSFile().get(id).setSize(file.length());
                        storeFile.getSFile().get(id).setExecutable(false);
                    }
                }

                StoreFile.SFile sFile = new StoreFile.SFile();
                sFile.setNameencryptedfile(uuid);
                sFile.setEncryptedSize(file.length());
                SendData sendData = new SendData();
                sendData.setType("File");
                sendData.setObject(sFile);
                //SocketClient.SendAesStream(SendData.toByte(sendData));

                //Thread.sleep(500);
                //Cipher aesCipher = Cipher.getInstance("AES/CFB8/NoPadding");
                //aesCipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(IV));
                InputStream is = new FileInputStream(file);
                //OutputStream os = SocketClient.cipherOutputStream;
                //CipherOutputStream cos = new CipherOutputStream(SocketClient.cipherOutputStream, aesCipher);
                byte[] buf = new byte[512];
                int numRead = 0;
                byte[] sfdata = SendData.toByte(sendData);
                byte[] data = new byte[16+sfdata.length];
                copyOfRangeToIndex(data,0, ByteUtils.longToByteArray(data.length),0,8);
                copyOfRangeToIndex(data,8, ByteUtils.longToByteArray(file.length()),0,8);
                copyOfRangeToIndex(data,16, sfdata,0,sfdata.length);
                int senddata=0;
                Cipher aesCipher = Cipher.getInstance("AES/CFB8/NoPadding");
                aesCipher.init(Cipher.ENCRYPT_MODE, mainfx.databaseKey, new IvParameterSpec(IV));
                cos = new CipherOutputStream(SocketClient.cipherOutputStream, aesCipher);

                while (senddata<data.length) {
                    int ssdata=512;
                    if((data.length-senddata)<512) ssdata = data.length-senddata;
                    SocketClient.cipherOutputStream.write(data, senddata, ssdata);
                    senddata+=512;
                }
                latch.await();
                latch = new CountDownLatch(1);
                while ((numRead = is.read(buf)) >= 0) {
                    cos.write(buf, 0, numRead);
                }
                is.close();
                //os.flush();
                SocketClient.cipherOutputStream.flush();
                //SocketClient.checkSendFile(uuid);
                latch.await();
                latch = new CountDownLatch(1);
                System.out.println(new String("Success send"));
                //SocketClient.checkSendFile(uuid);
                System.out.println("Encrypted file: " + file.length() + " " + file.getAbsolutePath());
            }
        }catch (Exception e){e.printStackTrace();}
    }

    public static int getIndex(StoreFile storeFile, String name, ArrayList<String> path)
    {
        if(storeFile!=null) {
            int id = 0;
            List<StoreFile.SFile> list = storeFile.getSFile();
            Iterator<StoreFile.SFile> iter = list.iterator();
            while (iter.hasNext()) {
                id++;
                StoreFile.SFile c = iter.next();
                if (name.equals(c.getName()) && path.equals(c.getFilepath())) {
                    return id;

                }

            }
        }else
        return 0;
        return 0;
    }
    public static boolean searchUUID(StoreFile storeFile, String uuid)
    {
        if(storeFile!=null) {
            List<StoreFile.SFile> list = storeFile.getSFile();
            Iterator<StoreFile.SFile> iter = list.iterator();
            while (iter.hasNext()) {
                StoreFile.SFile c = iter.next();
                if (uuid.equals(c.getNameencryptedfile())) {
                    return true;
                }

            }
        }else
            return false;
        return false;
    }


    public static StoreFile decryptStoreFile(byte[] object) {
        if(object!=null)
        try {
            Cipher aesCipher = Cipher.getInstance("AES/CFB8/NoPadding");
            aesCipher.init(Cipher.DECRYPT_MODE, mainfx.databaseKey, new IvParameterSpec(IV));
            //FileDialog.inputStoreFile=);


            return StoreFile.fnromByte(aesCipher.doFinal(object));
            //StoreFile storeFile = StoreFile.fromByte(aesCipher.doFinal(Files.readAllBytes(Paths.get(fol2 + "00000000-0000-0000-0000-000000000000"))));
        }catch (Exception e){e.printStackTrace();}
        return null;
        }

    public static long Modulo(long p, int q)
    {
        return p + (q - (p % q)); }
}

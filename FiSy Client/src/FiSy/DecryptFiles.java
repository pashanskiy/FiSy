package FiSy;

import FiSy.StoreData.SendData;
import FiSy.StoreData.StoreFile;
import javafx.collections.ObservableList;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class DecryptFiles {
    public static CountDownLatch latch = new CountDownLatch(0);
        static byte[] IV = EncryptFiles.IV;
        static  CipherInputStream cis;

        public static void init(){
            try {
                Cipher aesCipher = Cipher.getInstance("AES/CFB8/NoPadding");
                aesCipher.init(Cipher.DECRYPT_MODE, mainfx.databaseKey, new IvParameterSpec(IV));
                cis = new CipherInputStream(SocketClient.cipherInputStream,aesCipher);
            }catch (Exception e){e.printStackTrace();}
        }

    public static void decryptAndSave(ObservableList<tablePerson.FileDialogPerson> list, File dirToSave, ArrayList<String> curreentDirectory){
        try {
            latch = new CountDownLatch(1);
            ArrayList<StoreFile.SFile> ALsFile = new ArrayList<>();
            for (tablePerson.FileDialogPerson fileDialogPeople : list) {
                String nameAndType = fileDialogPeople.getName() + "." + fileDialogPeople.getType();
                for (StoreFile.SFile sFile : FileDialog.inputStoreFile.getSFile()) {
                    ArrayList<String> checkFile = new ArrayList<>();
                    checkFile = (ArrayList<String>) curreentDirectory.clone();
                    checkFile.add(fileDialogPeople.getName());
                    if (nameAndType.equals(sFile.getName()) && FileDialog.isTwoArrayListsWithSameValues(sFile.getFilepath(), curreentDirectory)) {
                        ALsFile.add(sFile);
                    } else if (fileDialogPeople.getType().equals("Папка") && FileDialog.checkFamilyFolder(checkFile, sFile.getFilepath())) {
                        ALsFile.add(sFile);
                    }
                }
            }


            SendData sendData = new SendData();
            sendData.setType("DisableGlobalThread");
            SocketClient.SendAesStream(SendData.toByte(sendData));
            latch.await();
            sendData.setType("PrepareToSendFile");
            for (StoreFile.SFile sFile : ALsFile) {
                StoreFile.SFile sFileToSend = new StoreFile.SFile();
                sFileToSend.setNameencryptedfile(sFile.getNameencryptedfile());
                sendData.setObject(sFileToSend);
                    String outPath = "";
                    for (int i = 0; i < sFile.getFilepath().size(); i++) {
                        if (i<curreentDirectory.size()) {
                            if(!sFile.getFilepath().get(i).equals(curreentDirectory.get(i)))
                                outPath = outPath + File.separator + sFile.getFilepath().get(i);
                        }else{
                            outPath = outPath + sFile.getFilepath().get(i)+File.separator;
                        }
                    }
                File file = new File(dirToSave+File.separator+outPath+sFile.getName());
                    file.getParentFile().mkdirs();
                init();
                SocketClient.SendAesStream(SendData.toByte(sendData));
                int bytesRead;
                OutputStream os = new FileOutputStream(file);
                Long size2 = sFile.getSize();
                byte[] buff = new byte[512];
                while (size2 > 0) {
                    if (size2 - 512 < 0) {
                        buff = new byte[size2.intValue()];
                    }

                    bytesRead = cis.read(buff, 0, buff.length);
                    os.write(buff, 0, bytesRead);
                    size2 = size2 - bytesRead;
                }
                os.close();

            System.out.println(dirToSave+File.separator+outPath+sFile.getName());

            }
            SocketClient.latch.countDown();
        }catch (Exception e){e.printStackTrace();}
    }

    private static void decryptFiles(String fol2, String fol3){

        try {
            Cipher aesCipher = Cipher.getInstance("AES/CFB8/NoPadding");
            aesCipher.init(Cipher.DECRYPT_MODE, mainfx.databaseKey, new IvParameterSpec(IV));
            StoreFile storeFile = StoreFile.fnromByte(aesCipher.doFinal(Files.readAllBytes(Paths.get(fol2+"00000000-0000-0000-0000-000000000000"))));
            for(int i = 0;i<storeFile.getSFile().size();i++){

                try {
                    InputStream is = new FileInputStream(fol2+storeFile.getSFile().get(i).getNameencryptedfile());
                    String filepath="";
                    for(String string:storeFile.getSFile().get(i).getFilepath()){
                        filepath=filepath+string+ File.separator;
                    }
                    File file = new File(fol3+filepath+storeFile.getSFile().get(i).getName());
                    file.getParentFile().mkdirs();
                    File fileout = new File(fol3+filepath+storeFile.getSFile().get(i).getName());
                    OutputStream os = new FileOutputStream(fileout);
                    fileout.setExecutable(storeFile.getSFile().get(i).getExecutable());
                    byte[] buf = new byte[1024];
// bytes read from stream will be decrypted
                    CipherInputStream cis = new CipherInputStream(is, aesCipher);
// read in the decrypted bytes and write the clear text to out
                    int numRead = 0;
                    while ((numRead = cis.read(buf)) >= 0) {
                        os.write(buf, 0, numRead);
                    }
// close all stream
                    cis.close();
                    is.close();
                    os.close();
                }
                catch (IOException e) {
                    System.out.println("I/O Error:" + e.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}

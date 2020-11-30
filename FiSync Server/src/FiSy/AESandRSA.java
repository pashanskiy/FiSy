package FiSy;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class AESandRSA {
    public static String PrivateKey;
    public static String PublicKey;
    public static KeyPair buildKeyPair() {
        try {
            final int keySize = 2048;
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(keySize);
            return keyPairGenerator.genKeyPair();
        }catch (Exception e){e.printStackTrace();}
        return null;
    }

    public static byte[] encryptRSA(String publicKey, byte[] message) {
        try{
            KeyFactory kf = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey));
            PublicKey pubKey = kf.generatePublic(keySpecX509);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return cipher.doFinal(message);
    }catch (Exception e){e.printStackTrace();}
        return null;
    }

    public static byte[] decryptRSA(String privateKey, byte[] encrypted) {
        try{
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey));
            PrivateKey privKey = kf.generatePrivate(keySpecPKCS8);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privKey);
        return cipher.doFinal(encrypted);
    }catch (Exception e){e.printStackTrace();}
        return null;
    }

    public static byte[] encryptAES(Key key, byte [] encrypted) {
        try{
            byte[] salt = new byte[]{0, 1, 0, 9, 2, 0, 1, 4, 3, 0, 0, 6, 2, 0, 1, 8};
            Cipher aesCipher = Cipher.getInstance("AES/CFB8/NoPadding");
            aesCipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(salt));
            return aesCipher.doFinal(encrypted);
        }catch (Exception e){e.printStackTrace();}
        return null;
    }

    public static byte[] decryptAES(Key key, byte [] encrypted) {
        try{
            byte[] salt = new byte[]{0, 1, 0, 9, 2, 0, 1, 4, 3, 0, 0, 6, 2, 0, 1, 8};
            Cipher aesCipher = Cipher.getInstance("AES/CFB8/NoPadding");
            aesCipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(salt));
            return aesCipher.doFinal(encrypted);
        }catch (Exception e){e.printStackTrace();}
        return null;
    }
}

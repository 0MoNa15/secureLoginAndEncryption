package com.mona15.loginencryption.ui.data;

import android.util.Base64;
import android.util.Log;

import javax.crypto.Cipher;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Permite realizar la encriptación por medio del tipo RSA
 * este tipo de cominucación encritada es asimétrica
 */
public class EncryptionAsymmetric {

    private KeyPairGenerator kpg;
    private KeyPair kp;
    private PublicKey publicKey;
    private PrivateKey privateKey;

    private String descryptedString;
    private byte[] encrytedByte;
    private byte[] descryptedByte;
    private Cipher cipher;

    private final static String CRYPTO_METHOD = "RSA";
    private final static int CRYPTO_BITS = 2048;
    private final static String OPCION_RSA= "RSA/ECB/OAEPWithSHA1AndMGF1Padding";
    //private String message = "Este mensaje es secreto, por ello va encriptado";

    public void generateKayPair() throws Exception{
        kpg = KeyPairGenerator.getInstance(CRYPTO_METHOD);
        kpg.initialize(CRYPTO_BITS);
        kp = kpg.generateKeyPair();
        publicKey = kp.getPublic();
        Log.d("TAG1", "public key -> " + publicKey);
        privateKey = kp.getPrivate();
        Log.d("TAG1", "private key -> " + privateKey);
    }

    public String encrypt(String mensajeAEncriptar) throws Exception{
        cipher = Cipher.getInstance(OPCION_RSA);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        encrytedByte = cipher.doFinal(mensajeAEncriptar.getBytes());
        return Base64.encodeToString(encrytedByte, Base64.DEFAULT);
    }

    public String descrypt(String result) throws Exception{
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        descryptedByte = cipher.doFinal(Base64.decode(result, Base64.DEFAULT));
        descryptedString = new String(descryptedByte);
        return descryptedString;
    }
}

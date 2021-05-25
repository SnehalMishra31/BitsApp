package com.rawtalent.chatencryption;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.FileUtils;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;


import javax.crypto.Cipher;


public class AssymetricEncryption {

    private static final String RSA = "RSA";


    /*
         This method creates a UserKeys object
         which will contain public and private keys in the form of byte arrays
     */

    public static UserKeys createUserKeys() throws Exception {
        SecureRandom secureRandom = new SecureRandom();
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA);

        keyPairGenerator.initialize(
                2048, secureRandom);

        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        UserKeys userKeys = new UserKeys();

        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        userKeys.setPublicKey(publicKey.getEncoded());
        userKeys.setPrivateKey(privateKey.getEncoded());

        return userKeys;
    }

    /*
      decrypts the message and returns a String
     */
    public static String decryptMessage(byte[] message, byte[] privateKey) throws Exception{
        PrivateKey key= getPrivateKey(privateKey);
        return decrypt(message,key);
    }

    private static String decrypt(byte[] cipherText, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] result = cipher.doFinal(cipherText);
        return new String(result);
    }


    /*
      encrypts the message and returns ciphertext in the form of a byte array
     */
    public static byte[] encryptMessage(String message, byte[] publicKey) throws Exception{
        PublicKey key= getPublicKey(publicKey);
        return encrypt(message,key);
    }

    private static byte[] encrypt(String plainText, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(plainText.getBytes());
    }

    private static byte[] encrypt(byte[] plainText, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(plainText);
    }


    /*
    returns PublicKey object from byte array
     */
    private static PublicKey getPublicKey(byte[] publicKey)throws Exception{
        KeyFactory keyFactory=KeyFactory.getInstance(RSA);
        return keyFactory.generatePublic(new X509EncodedKeySpec(publicKey));
    }


    /*
    returns PrivateKey object from byte array
     */
    private static PrivateKey getPrivateKey(byte[] privateKey)throws Exception{
        KeyFactory keyFactory=KeyFactory.getInstance(RSA);
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKey));
    }


    /*
     Encrypts the image and returns the cipher byte array
     */

    public static byte[] encryptImage(Bitmap bitmap,byte[] publicKey)throws Exception{

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();

        PublicKey key=getPublicKey(publicKey);

        return encrypt(b,key);
    }


    public static byte[] encryptImage(byte[] bytes,byte[] publicKey)throws Exception{
        return encrypt(bytes,getPublicKey(publicKey));
    }




     /*
         Decrypts the Image and returns a Bitmap
     */

    public static Bitmap decryptImageToBitmap(byte[] image,byte[] privateKey)throws Exception{

        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.DECRYPT_MODE, getPrivateKey(privateKey));
        byte[] result = cipher.doFinal(image);
        return BitmapFactory.decodeByteArray(result, 0, result.length);
    }




}
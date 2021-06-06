package com.rawtalent.bitsapp.DB;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import com.rawtalent.bitsapp.Utility;

import java.util.Arrays;

import static android.content.Context.MODE_PRIVATE;

public class UserSharedPreference {


    private static final String PREFERENCE_NAME = "UserDetails";

    private static final String PUBLIC_KEY = "publicKey";
    private static final String PRIVATE_KEY = "privateKey";
    private static final String PROFILE_IMAGE="profileImage";
    private static final String BIO="bio";

    public static byte[] getPrivateKey(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        String key=sharedPreferences.getString(PRIVATE_KEY,null);
        if (key!=null){
            String parts[]=key.substring(1,key.length()-1).split(", ");
            byte[] bytes=new byte[parts.length];

            for (int i=0;i<parts.length;i++){
                bytes[i]=Byte.parseByte(parts[i]);
            }

            return bytes;

        }

        return null;
    }

    public static void setPrivateKey(Context context,byte key[]){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PRIVATE_KEY, Arrays.toString(key));
        editor.apply();
    }

    public static byte[] getPublicKey(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        String key=sharedPreferences.getString(PUBLIC_KEY,null);
        if (key!=null){
            String parts[]=key.substring(1,key.length()-1).split(", ");
            byte[] bytes=new byte[parts.length];

            for (int i=0;i<parts.length;i++){
                bytes[i]=Byte.parseByte(parts[i]);
            }

            return bytes;

        }

        return null;
    }

    public static void setPublicKey(Context context,byte key[]){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PUBLIC_KEY, Arrays.toString(key));
        editor.apply();
    }

    public static String getProfileImage(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        return  sharedPreferences.getString(PROFILE_IMAGE,"");
    }

    public static void setProfileImage(Context context,String image){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PROFILE_IMAGE, image);
        editor.apply();
    }

    public static void setProfileImage(Context context, Bitmap image){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PROFILE_IMAGE, Utility.encodeTobase64(image));
        editor.apply();
    }

    public static String getBio(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        return  sharedPreferences.getString(BIO,"");
    }

    public static void setBio(Context context,String bio){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(BIO, bio);
        editor.apply();
    }


}

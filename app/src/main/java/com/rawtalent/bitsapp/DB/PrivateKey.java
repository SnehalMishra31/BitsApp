package com.rawtalent.bitsapp.DB;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Base64;

import static android.content.Context.MODE_PRIVATE;

public class PrivateKey {

    private static final String PREFERENCE_NAME = "privateKey";
    private static final String KEY = "key";

    public static byte[] getKeyFromDB(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        String key=sharedPreferences.getString(KEY,null);
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

    public static void setKey(Context context,byte key[]){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY, Arrays.toString(key));
        editor.apply();
    }


}

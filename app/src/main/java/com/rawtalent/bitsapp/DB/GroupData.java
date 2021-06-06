package com.rawtalent.bitsapp.DB;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class GroupData {

    private static final String PREFERENCE_NAME = "Groups:";
    private static final String NAME= "name";
    private static final String IMAGE= "icon";
    private static final String PRIVATE_KEY="key";

    public static String getName(Context context, String uuid){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME+uuid, MODE_PRIVATE);
        String name=sharedPreferences.getString(NAME,"");
        return name;
    }

    public static void setName(Context context,String uuid,String name){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME+uuid, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(NAME, name);
        editor.apply();
    }
    public static String getPrivateKey(Context context, String uuid){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME+uuid, MODE_PRIVATE);
        String key=sharedPreferences.getString(PRIVATE_KEY,"");
        return key;
    }

    public static void setPrivateKey(Context context,String uuid,String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME+uuid, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PRIVATE_KEY, key);
        editor.apply();
    }

    public static String getIcon(Context context, String uuid){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME+uuid, MODE_PRIVATE);
        String icon=sharedPreferences.getString(IMAGE,"");
        return icon;
    }

    public static void setIcon(Context context,String uuid,String image){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME+uuid, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(IMAGE, image);
        editor.apply();
    }
}

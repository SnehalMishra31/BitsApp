package com.rawtalent.bitsapp.DB;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Arrays;

import static android.content.Context.MODE_PRIVATE;

public class MyMessages {

    private static final String PREFERENCE_NAME = "myMessages";

    public static void setMessage(Context context,String userID, String id,String msg){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(userID+"/"+id, msg);
        editor.apply();
    }

    public static String getMessage(Context context,String userID,String id){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        return  sharedPreferences.getString(userID+"/"+id,null);
    }

}

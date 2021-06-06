package com.rawtalent.bitsapp.DB;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class OwnMessages {

    private static final String PREFERENCE_NAME = "myMessages";


    public static String getKeyFromDB(Context context,String messageID,String sender){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        String msg=sharedPreferences.getString(sender+"/"+messageID,"");
        return msg;
    }

    public static void setKey(Context context,String msg,String messageID,String sender){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(sender+"/"+messageID,msg);
        editor.apply();
    }
}

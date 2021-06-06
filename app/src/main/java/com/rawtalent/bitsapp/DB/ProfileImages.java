package com.rawtalent.bitsapp.DB;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;

import static android.content.Context.MODE_PRIVATE;

public class ProfileImages {

    public static final String PREFERENCE_NAME_IMAGE = "profileImages";


    public static void storeImage(Context context,String uuid,String image) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME_IMAGE, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(uuid, image);
            editor.apply();
        }
    }

    public static String retrieveImage(Context context,String uuid) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME_IMAGE, MODE_PRIVATE);
        String image=sharedPreferences.getString(uuid,"");
        return image;
    }


    /**
     * This function encode the image bitmap to store into shared preferences.
     *
     * @param image
     * @return
     */
    public static String encodeTobase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imagestring = Base64.encodeToString(b, Base64.DEFAULT);
        Log.d("TAG", "encodeTobase64: imagestring = "+imagestring);
        return imagestring;
    }

    /**
     * This function decode the image into bitmap to show onto screen.
     *
     * @param input
     * @return
     */
    public static Bitmap decodeBase64(String input) {
        if (input == null)
            return null;

        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}

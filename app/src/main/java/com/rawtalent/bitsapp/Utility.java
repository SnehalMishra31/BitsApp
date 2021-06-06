package com.rawtalent.bitsapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utility {


    public static String getCurrentTimeString(Date date){
        SimpleDateFormat timeformat=new SimpleDateFormat("HH:mm");
        String timeString=timeformat.format(date);
        return timeString;
    }

    public static String getCurrentDateString(Date date){
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy");
        String dateString=dateFormat.format(date);
        return dateString;
    }

    public static Date getCurrentDate(){
        Date date= Calendar.getInstance().getTime();
        return date;
    }
    public static byte[] stringToByteArray(String string){

        String parts[]=string.substring(1,string.length()-1).split(", ");
        byte[] bytes=new byte[parts.length];
        for (int i=0;i<parts.length;i++){
            bytes[i]=Byte.parseByte(parts[i]);
        }

        return bytes;
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

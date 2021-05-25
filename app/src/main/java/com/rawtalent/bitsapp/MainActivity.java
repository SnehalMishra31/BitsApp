package com.rawtalent.bitsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    Button btn;
    EditText editText;

    byte[] arr;


    boolean en=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        editText=(EditText)findViewById(R.id.et1);
        btn=(Button)findViewById(R.id.encryptbtn);


        UserKeys userKeys= null;
        try {
            userKeys = AssymetricEncryption.createUserKeys();
        } catch (Exception e) {
            e.printStackTrace();
        }

        UserKeys finalUserKeys = userKeys;
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                en=!en;

                if (en){
                    String msg=editText.getText().toString();
                    if (!msg.equals("")){
                        String str= null;
                        try {
                            arr=AssymetricEncryption.encryptMessage(msg, finalUserKeys.getPublicKey());
                            str = Arrays.toString(arr);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        editText.setText(""+str);
                    }
                }else{

                    try {
                        editText.setText(AssymetricEncryption.decryptMessage(arr,finalUserKeys.getPrivateKey()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }
}
package com.rawtalent.bitsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.tabs.TabLayout;
import com.rawtalent.chatencryption.AssymetricEncryption;
import com.rawtalent.chatencryption.UserKeys;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    Button btn;
    EditText editText;

    byte[] arr;

    TabLayout mTabLayout;
    ViewPager mViewPager;

    boolean en=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mTabLayout = findViewById(R.id.tabLayout);
        mViewPager = findViewById(R.id.myViewPager);
        setupViewPager(mViewPager);
        mTabLayout.setupWithViewPager(mViewPager);


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

    private void setupViewPager(ViewPager mViewPager) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new PersonalChat(MainActivity.this), "Friends");
        viewPagerAdapter.addFragment(new GroupChat(MainActivity.this), "Groups");
        mViewPager.setAdapter(viewPagerAdapter);
    }

}
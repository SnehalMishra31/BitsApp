package com.rawtalent.bitsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rawtalent.chatencryption.AssymetricEncryption;
import com.rawtalent.chatencryption.UserKeys;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    TabLayout mTabLayout;
    ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mTabLayout = findViewById(R.id.tabLayout);
        mViewPager = findViewById(R.id.myViewPager);
        setupViewPager(mViewPager);
        mTabLayout.setupWithViewPager(mViewPager);

    }

    private void setupViewPager(ViewPager mViewPager) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new PersonalChatListFragment(MainActivity.this), "Friends");
        viewPagerAdapter.addFragment(new GroupChatListFragment(MainActivity.this), "Groups");
        mViewPager.setAdapter(viewPagerAdapter);
    }

}
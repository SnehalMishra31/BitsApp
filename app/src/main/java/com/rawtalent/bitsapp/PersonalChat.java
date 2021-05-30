package com.rawtalent.bitsapp;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class PersonalChat extends Fragment {



    Context context;

    public PersonalChat() {
        // Required empty public constructor

    }
    public PersonalChat(Context context) {
        this.context=context;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_personal_chat, container, false);
    }
}
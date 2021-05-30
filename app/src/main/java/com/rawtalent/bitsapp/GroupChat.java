package com.rawtalent.bitsapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.rawtalent.bitsapp.ModelClasses.Contacts;
import com.rawtalent.bitsapp.ViewHolders.ContactsViewHolder;


public class GroupChat extends Fragment {


    View view;
    RecyclerView mrecylerview;
    private ProgressDialog mProgressDialog;
    private FirebaseFirestore firebaseFirestore;
    ConstraintLayout emptyView;
    private FirestoreRecyclerAdapter firestoreRecyclerAdapter;


    Context mContext;
    public GroupChat() {
        // Required empty public constructor
    }

    public GroupChat(Context mContext) {
        // Required empty public constructor
        this.mContext=mContext;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_group_chat, container, false);
        mrecylerview=view.findViewById(R.id.recyclerview);


        firebaseFirestore = FirebaseFirestore.getInstance();

        FirebaseAuth auth=FirebaseAuth.getInstance();
        Query query = firebaseFirestore.collection(Constants.USER_COLLECTION).document(auth.getCurrentUser().getUid()).collection(Constants.CONTACTS_COLLECTION)
         .orderBy("lastMessageDate", Query.Direction.DESCENDING);


        FirestoreRecyclerOptions<Contacts> options = new FirestoreRecyclerOptions.Builder<Contacts>().setQuery(query, Contacts.class).build();
        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<Contacts, ContactsViewHolder>(options) {
            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.accounts_items, parent, false);
                return new ContactsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, final int position, @NonNull final Contacts model) {


            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if (firestoreRecyclerAdapter.getItemCount()==0){
                 //   animationView.setVisibility(View.VISIBLE);

                }else{
                  //  animationView.setVisibility(View.GONE);
                    //  emptyView.setVisibility(View.GONE);
                }
            }
        };
        mrecylerview.setHasFixedSize(true);
        mrecylerview.setLayoutManager(new LinearLayoutManager(mContext));
        mrecylerview.setAdapter(firestoreRecyclerAdapter);
        return view;
    }

    private void showProgressDialogWithTitle(String substring) {
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(substring);
        mProgressDialog.show();
    }

    private void hideProgressDialogWithTitle() {
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.dismiss();
    }
    @Override
    public void onStop() {
        super.onStop();
        firestoreRecyclerAdapter.stopListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        firestoreRecyclerAdapter.startListening();
    }





    }

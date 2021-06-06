package com.rawtalent.bitsapp;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rawtalent.bitsapp.Contacts.ContactList;
import com.rawtalent.bitsapp.DB.MyMessages;
import com.rawtalent.bitsapp.DB.ProfileImages;
import com.rawtalent.bitsapp.DB.UserSharedPreference;
import com.rawtalent.bitsapp.ModelClasses.Contacts;
import com.rawtalent.bitsapp.ViewHolders.ContactsViewHolder;
import com.rawtalent.chatencryption.AssymetricEncryption;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class PersonalChatListFragment extends Fragment {



    View view;
    RecyclerView mrecylerview;
    private ProgressDialog mProgressDialog;
    private FirebaseFirestore firebaseFirestore;
    LinearLayout emptyView;
    private FirestoreRecyclerAdapter firestoreRecyclerAdapter;


    Button getcontacts;

    Context mContext;

    Map<String,Bitmap> profileImages;

    public PersonalChatListFragment() {
        // Required empty public constructor

    }
    public PersonalChatListFragment(Context mContext) {
        this.mContext=mContext;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_personal_chat, container, false);
        mrecylerview=view.findViewById(R.id.recyclerview);
        emptyView=view.findViewById(R.id.emptyview);
        getcontacts=view.findViewById(R.id.getcontacts);



        firebaseFirestore = FirebaseFirestore.getInstance();

        profileImages=new HashMap<>();

        FirebaseAuth auth=FirebaseAuth.getInstance();
        Query query = firebaseFirestore.collection(Constants.USER_COLLECTION).document(auth.getCurrentUser().getUid()).collection(Constants.CONTACTS_COLLECTION)
                .orderBy(Constants.USERCONTACTS_LAST_MESSAGE_TIMELONG, Query.Direction.DESCENDING);


        FirestoreRecyclerOptions<Contacts> options = new FirestoreRecyclerOptions.Builder<Contacts>().setQuery(query, Contacts.class).build();
        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<Contacts, ContactsViewHolder>(options) {
            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.chat_list_item, parent, false);
                return new ContactsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, final int position, @NonNull final Contacts model) {



                if (getContactName(model.getNumber())!=null){
                    holder.name.setText(getContactName(model.getNumber()));
                }else{
                    holder.name.setText(""+model.getNumber());
                }

                if (model.getSender()!=null && model.getSender().equals(auth.getCurrentUser().getUid())){
                    try {
                        String msg= MyMessages.getMessage(mContext,getSnapshots().getSnapshot(position).getId(),""+model.getLastMessageDate());
                        if (msg==null){
                            holder.lastmessage.setText("message deleted by you");
                            holder.lastmessage.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                        }else {
                            holder.lastmessage.setText("" + msg);
                            holder.lastmessage.setTypeface(Typeface.DEFAULT);
                        }
                    }catch (Exception e){
                        holder.lastmessage.setText("message deleted by you");
                        holder.lastmessage.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                    }
                }else{

                    holder.lastmessage.setTypeface(Typeface.DEFAULT);
                    try {
                        holder.lastmessage.setText(""+ AssymetricEncryption.decryptMessage(Utility.stringToByteArray(model.getLastMessage()), UserSharedPreference.getPrivateKey(mContext)));
                    } catch (Exception e) {
                        holder.lastmessage.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                        holder.lastmessage.setText("error while decrypting the message");
                    }
                }
                holder.date.setText(""+getDate(model.getStringDate(),model.getStringTime()));

                if (model.getNotifications()>0) {
                    holder.notifications.setVisibility(View.VISIBLE);
                    holder.notifications.setText("" + model.getNotifications());
                }else{
                    holder.notifications.setVisibility(View.GONE);
                }

                String mImage=""+ProfileImages.retrieveImage(mContext,getSnapshots().getSnapshot(position).getId());
                if (mImage.equals("")||mImage.isEmpty()){

                }else{
                    Bitmap bitmap=ProfileImages.decodeBase64(mImage);
                    holder.imageView.setImageBitmap(bitmap);
                }

                if (profileImages.containsKey(getSnapshots().getSnapshot(position).getId()) &&
                        profileImages.get(getSnapshots().getSnapshot(position).getId())!=null){
                        holder.imageView.setImageBitmap(profileImages.get(getSnapshots().getSnapshot(position).getId()));
                }else if (profileImages.containsKey(getSnapshots().getSnapshot(position).getId()) &&
                        profileImages.get(getSnapshots().getSnapshot(position).getId())==null){
                    holder.imageView.setImageResource(R.drawable.profileicon);
                }
                else {
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageReference = storage.getReference().child(Constants.STORAGE_PROFILEIMAGES).child(getSnapshots().getSnapshot(position).getId());
                    storageReference.getBytes(1024 * 1024 * 5).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            //       Log.d(TAG, "onSuccess: Got profile image successfully");
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            if (bitmap != null) {
                                try {
                                    profileImages.put(getSnapshots().getSnapshot(position).getId(), bitmap);
                                    ProfileImages.storeImage(mContext,getSnapshots().getSnapshot(position).getId(),ProfileImages.encodeTobase64(bitmap));
                                }catch (Exception e){
                                    Log.d("TESTERROR", "error: "+e.getMessage());
                                }
                                holder.imageView.setImageBitmap(bitmap);
                            }else{
                                profileImages.put(getSnapshots().getSnapshot(position).getId(), null);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            profileImages.put(getSnapshots().getSnapshot(position).getId(), null);

                        }
                    });

                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(mContext,PersonalChat.class);
                        try {
                            intent.putExtra("name",holder.name.getText().toString());

                        }catch (Exception e){
                            intent.putExtra("name",model.getNumber());
                        }
                        intent.putExtra("key",model.getPublicKey());
                        intent.putExtra("chatID",model.getChatID());
                        intent.putExtra("uid",getSnapshots().getSnapshot(position).getId());
                        mContext.startActivity(intent);
                    }
                });
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if (firestoreRecyclerAdapter.getItemCount()==0){
                    mrecylerview.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);


                }else{

                    emptyView.setVisibility(View.GONE);
                    mrecylerview.setVisibility(View.VISIBLE);
                }
            }
        };
        mrecylerview.setHasFixedSize(true);
        mrecylerview.setLayoutManager(new LinearLayoutManager(mContext));
        mrecylerview.setAdapter(firestoreRecyclerAdapter);


        getcontacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, ContactList.class);
                mContext.startActivity(intent);
            }
        });

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


    public String getDate(String dateOfMessage,String timeOfMessage){
        Date date= Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy");
        String currentDate=dateFormat.format(date);


        if (currentDate.equals(dateOfMessage)){
            return timeOfMessage.substring(0,5);
        }

        if (currentDate.substring(3).equals(dateOfMessage.substring(3))){
            int d1=Integer.parseInt(currentDate.substring(0,2));
            int d2=Integer.parseInt(dateOfMessage.substring(0,2));

            if (d1-d2==1){
                return "Yesterday";
            }
        }


        return dateOfMessage;

    }


    public String getContactName(String number){

        ContentResolver contentResolver=mContext.getContentResolver();
        Uri uri=Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(number));
        Cursor cursor=contentResolver.query(uri,new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME},null,null,null);

        if (cursor==null){
            return null;
        }
        String name=null;

        if (cursor.moveToFirst()){
            name=cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }
        if (cursor!=null && !cursor.isClosed()){
            cursor.close();
        }

        return name;
    }


}
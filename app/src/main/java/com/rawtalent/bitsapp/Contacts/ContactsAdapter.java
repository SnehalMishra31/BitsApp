package com.rawtalent.bitsapp.Contacts;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rawtalent.bitsapp.Constants;
import com.rawtalent.bitsapp.DB.UserSharedPreference;
import com.rawtalent.bitsapp.ModelClasses.Contacts;
import com.rawtalent.bitsapp.PersonalChat;
import com.rawtalent.bitsapp.R;
import com.rawtalent.bitsapp.SetProfile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder> {



    AlertDialog.Builder builder;
    AlertDialog progressDialog;

    ArrayList<ContactModel> contactModels;
    Context context;

    public ContactsAdapter(ArrayList<ContactModel> contactModels, Context context) {
        this.context = context;
        this.contactModels=contactModels;

        progressDialog = getBuilder().create();
        progressDialog.setCancelable(false);
    }

    @NonNull
    @Override
    public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.contact_item, parent, false);
        return new ContactsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ContactsViewHolder holder, final int position) {

        holder.name.setText(contactModels.get(position).getName());
        holder.number.setText(contactModels.get(position).getNumber());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                checkForDocument(contactModels.get(position));
            }
        });
    }


    public void checkForDocument(ContactModel contact){

        String uid=contact.getUuid();
        FirebaseAuth auth=FirebaseAuth.getInstance();
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection(Constants.USER_COLLECTION).document(auth.getCurrentUser().getUid()).collection(Constants.CONTACTS_COLLECTION).document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()){
                        progressDialog.dismiss();
                        moveToChatActivity(documentSnapshot.getString("chatID"),uid,contact.getName(),documentSnapshot.getString("publicKey"));
                    }else{
                       getFriendPublicKey(uid,contact);
                    }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getFriendPublicKey(String uid,ContactModel contactModel){

        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTIONS).document(Constants.KEY_PUBLIC).collection(Constants.KEY_PERSONS).document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String key=documentSnapshot.getString("key");
                addInFriendContact(key,contactModel);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
            }
        });

    }


    public void addInOurContact(String key,Contacts contacts,ContactModel contactModel){

        contacts.setNumber(contactModel.getNumber());
        contacts.setPublicKey(key);

        FirebaseAuth auth=FirebaseAuth.getInstance();
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection(Constants.USER_COLLECTION).document(auth.getCurrentUser().getUid()).collection(Constants.CONTACTS_COLLECTION).document(contactModel.getUuid())
                .set(contacts).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "success!", Toast.LENGTH_SHORT).show();
                moveToChatActivity(contacts.getChatID(),contactModel.getUuid(),contactModel.getName(),key);
               progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
            }
        });
    }

    public void addInFriendContact(String key,ContactModel contactModel){

        Date date= Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat timeformat=new SimpleDateFormat("HH:mm");
        String dateString=dateFormat.format(date);
        String timeString=timeformat.format(date);
        long timeInLong=date.getTime();

        FirebaseAuth auth=FirebaseAuth.getInstance();
        String chatRoomID=generateChatID(auth.getCurrentUser().getPhoneNumber(),contactModel.getNumber());

        Contacts contacts=new Contacts();
        contacts.setNumber(auth.getCurrentUser().getPhoneNumber());
        contacts.setBlocked(0);
        contacts.setLastMessage("Default chat invitation message");
        contacts.setChatID(chatRoomID);
        contacts.setLastMessageDate(timeInLong);
        contacts.setNotifications(0);
        contacts.setPublicKey(Arrays.toString(UserSharedPreference.getPublicKey(context)));
        contacts.setStringDate(dateString);
        contacts.setStringTime(timeString);

        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection(Constants.USER_COLLECTION).document(contactModel.getUuid()).collection(Constants.CONTACTS_COLLECTION).document(auth.getCurrentUser().getUid())
                .set(contacts).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                addInOurContact(key,contacts,contactModel);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
            }
        });
    }


    public void moveToChatActivity(String chatRoomID,String uid,String name,String key){
        Intent intent=new Intent(context, PersonalChat.class);
        intent.putExtra("chatID",chatRoomID);
        intent.putExtra("uid",uid);
        intent.putExtra("name",name);
        intent.putExtra("key",key);
        context.startActivity(intent);
    }


    @Override
    public int getItemCount() {
        return contactModels.size();
    }

    public static class ContactsViewHolder extends RecyclerView.ViewHolder {

        TextView name, number;


        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.namecontact);
            number = itemView.findViewById(R.id.numbercontact);

        }
    }


    public AlertDialog.Builder getBuilder() {
        if (builder == null) {
            builder = new AlertDialog.Builder(context);
            builder.setTitle("Please wait...");

            final ProgressBar progressBar = new ProgressBar(context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            progressBar.setLayoutParams(layoutParams);
            builder.setView(progressBar);
        }
        return builder;

    }

    public String generateChatID(String num1,String num2){

        String n1=num1;
        String n2=num2;

        if (num1.charAt(0)=='+'){
            num1=num1.substring(1);
        }

        if (num2.charAt(0)=='+'){
            num2=num2.substring(1);
        }

        long number1= Long.parseLong(num1);
        long number2= Long.parseLong(num2);

        if (number1>number2){
            return ""+n1+""+n2;
        }
        return ""+n2+""+n1;
    }

}

package com.rawtalent.bitsapp.GroupInfo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rawtalent.bitsapp.Constants;
import com.rawtalent.bitsapp.Contacts.ContactModel;
import com.rawtalent.bitsapp.DB.UserSharedPreference;
import com.rawtalent.bitsapp.ModelClasses.Contacts;
import com.rawtalent.bitsapp.PersonalChat;
import com.rawtalent.bitsapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.ContactsViewHolder> {



    AlertDialog.Builder builder;
    AlertDialog progressDialog;

    String groupId;

    ArrayList<MemberModel> contactModels;
    Context context;
    boolean isAdmin;

    public MembersAdapter(ArrayList<MemberModel> contactModels, Context context,boolean admin,String groupID) {
        this.context = context;
        this.contactModels=contactModels;
        isAdmin=admin;

        groupId=groupID;
        progressDialog = getBuilder().create();
        progressDialog.setCancelable(false);
    }

    @NonNull
    @Override
    public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.members_item, parent, false);
        return new ContactsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ContactsViewHolder holder, final int position) {

                if (!isAdmin){
                    holder.remove.setVisibility(View.GONE);
                }else{
                    holder.remove.setVisibility(View.VISIBLE);
                }

                holder.name.setText(""+contactModels.get(position).getName());
                try {
                    holder.imageView.setImageBitmap(contactModels.get(position).getImage());
                }catch (Exception e){

                }

                holder.remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        remove(groupId,contactModels.get(position).getUid());
                        Toast.makeText(context, "Changes will be visible soon", Toast.LENGTH_SHORT).show();
                    }
                });

    }




        public void remove(String groupID,String uid){


            try {
                FirebaseFirestore db=FirebaseFirestore.getInstance();
                db.collection(Constants.GROUPS_COLLECTION).document(groupID).update("members", FieldValue.arrayRemove(uid));
                FirebaseFirestore db2=FirebaseFirestore.getInstance();
                db2.collection(Constants.USER_COLLECTION).document(uid).collection(Constants.USER_GROUPS_COLLECTION).document(groupID).delete();
            }catch (Exception e){

            }


        }



    @Override
    public int getItemCount() {
        return contactModels.size();
    }

    public static class ContactsViewHolder extends RecyclerView.ViewHolder {

        TextView name, remove;
        ImageView imageView;


        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);

            name=(TextView)itemView.findViewById(R.id.nameofcontact);
            imageView=(ImageView) itemView.findViewById(R.id.profileimage);
            remove=(TextView)itemView.findViewById(R.id.remove);

        }
    }


    public AlertDialog.Builder getBuilder() {
        if (builder == null) {
            builder = new AlertDialog.Builder(context);
            builder.setTitle("Removing member...");

            final ProgressBar progressBar = new ProgressBar(context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            progressBar.setLayoutParams(layoutParams);
            builder.setView(progressBar);
        }
        return builder;

    }



}

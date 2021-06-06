package com.rawtalent.bitsapp.ViewHolders;

import android.content.Context;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.rawtalent.bitsapp.Constants;
import com.rawtalent.bitsapp.Contacts.ContactModel;
import com.rawtalent.bitsapp.CreateGroup;
import com.rawtalent.bitsapp.DB.UserSharedPreference;
import com.rawtalent.bitsapp.ModelClasses.Contacts;
import com.rawtalent.bitsapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class GroupUserAdapter extends RecyclerView.Adapter<GroupUserAdapter.ContactsViewHolder> {



    AlertDialog.Builder builder;
    AlertDialog progressDialog;

    ArrayList<ContactModel> contactModels;
    Context context;

    public GroupUserAdapter(ArrayList<ContactModel> contactModels, Context context) {
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

        if (contactModels.get(position).isChecked()){
            holder.check.setVisibility(View.VISIBLE);
        }else{
            holder.check.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (contactModels.get(position).isChecked()){
                    contactModels.get(position).setChecked(false);
                    CreateGroup.contactModels.get(position).setChecked(false);
                }else{
                    contactModels.get(position).setChecked(true);
                    CreateGroup.contactModels.get(position).setChecked(true);
                }

                if (contactModels.get(position).isChecked()){
                    holder.check.setVisibility(View.VISIBLE);
                }else{
                    holder.check.setVisibility(View.GONE);
                }
            }
        });
    }





    @Override
    public int getItemCount() {
        return contactModels.size();
    }

    public static class ContactsViewHolder extends RecyclerView.ViewHolder {

        TextView name, number;
        ImageView check;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.namecontact);
            number = itemView.findViewById(R.id.numbercontact);
            check = itemView.findViewById(R.id.checkmark);

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

}

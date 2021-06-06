package com.rawtalent.bitsapp.ViewHolders;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.rawtalent.bitsapp.R;

import de.hdodenhof.circleimageview.CircleImageView;


public class ContactsViewHolder extends RecyclerView.ViewHolder{


   public CircleImageView imageView;
   public TextView name,lastmessage,date,notifications;

    public ContactsViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView=itemView.findViewById(R.id.profile_image_item);
        date=itemView.findViewById(R.id.date);
        notifications=itemView.findViewById(R.id.number_of_notifications);
        lastmessage=itemView.findViewById(R.id.last_message);
        name=itemView.findViewById(R.id.profile_name_item);



    }
}
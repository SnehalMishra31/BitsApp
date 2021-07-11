package com.rawtalent.bitsapp.GroupInfo;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rawtalent.bitsapp.R;


public class MemberViewHolder extends RecyclerView.ViewHolder{


    TextView name,remove;
    ImageView imageView;


    public MemberViewHolder(@NonNull View itemView) {
        super(itemView);

        name=(TextView)itemView.findViewById(R.id.nameofcontact);
        imageView=(ImageView) itemView.findViewById(R.id.profileimage);
        remove=(TextView)itemView.findViewById(R.id.remove);


    }
}
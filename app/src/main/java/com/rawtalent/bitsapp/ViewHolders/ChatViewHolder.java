package com.rawtalent.bitsapp.ViewHolders;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.rawtalent.bitsapp.R;


public class ChatViewHolder extends RecyclerView.ViewHolder{


    public TextView message1,message2,date1,date2,time1,time2,filename1,filename2;
    public TextView senderName;
    public ImageButton download1,download2;
    public ImageView seen1,seen2,image1,image2;

    public LinearLayout yourmessage,othermessage,filelayout1,filelayout2;

    public ChatViewHolder(@NonNull View itemView) {
        super(itemView);

        message1=itemView.findViewById(R.id.message1);
        message2=itemView.findViewById(R.id.message2);
        date1=itemView.findViewById(R.id.date1);
        date2=itemView.findViewById(R.id.date2);
        time1=itemView.findViewById(R.id.time1);
        time2=itemView.findViewById(R.id.time2);
        filename1=itemView.findViewById(R.id.filename1);
        filename2=itemView.findViewById(R.id.filename2);

        senderName=itemView.findViewById(R.id.sendername1);

        download1=itemView.findViewById(R.id.download1);
        download2=itemView.findViewById(R.id.download2);

        seen1=itemView.findViewById(R.id.seen1);
        seen2=itemView.findViewById(R.id.seen2);
        image1=itemView.findViewById(R.id.image1);
        image2=itemView.findViewById(R.id.image2);


        yourmessage=itemView.findViewById(R.id.yourmessage);
        othermessage=itemView.findViewById(R.id.othermessage);


        filelayout1=itemView.findViewById(R.id.filelayout1);
        filelayout2=itemView.findViewById(R.id.filelayout2);

    }
}
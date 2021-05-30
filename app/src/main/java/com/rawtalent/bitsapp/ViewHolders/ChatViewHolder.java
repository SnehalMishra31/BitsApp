package com.rawtalent.bitsapp.ViewHolders;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.rawtalent.bitsapp.R;


public class ChatViewHolder extends RecyclerView.ViewHolder{

  public    TextView name,date,message,filename,time,time2;
    public  Button download;

    public CardView fileLayout;
    public ImageView fileImageview;

    public ImageView imageDisplay;

    public CardView cardView;

    public TextView name2,date2,message2,filename2;
    public Button download2;

    public CardView fileLayout2;
    public ImageView fileImageview2;

    public ImageView imageDisplay2;

    public CardView cardView2;


    public ImageView seen,seen2;

    public ChatViewHolder(@NonNull View itemView) {
        super(itemView);

        name=itemView.findViewById(R.id.name);
        date=itemView.findViewById(R.id.dateAndTime);
        message=itemView.findViewById(R.id.message);
        time=itemView.findViewById(R.id.timeOfmsg);
        filename=itemView.findViewById(R.id.filename);
        download=itemView.findViewById(R.id.download);

        fileImageview=itemView.findViewById(R.id.filetype);
        fileLayout=itemView.findViewById(R.id.filelayout);

        cardView=itemView.findViewById(R.id.cardview);

        imageDisplay=itemView.findViewById(R.id.image_display);

        name2=itemView.findViewById(R.id.name2);
        date2=itemView.findViewById(R.id.dateAndTime2);
        message2=itemView.findViewById(R.id.message2);
        time2=itemView.findViewById(R.id.timeOfmsg2);
        filename2=itemView.findViewById(R.id.filename2);
        download2=itemView.findViewById(R.id.download2);

        fileImageview2=itemView.findViewById(R.id.filetype2);
        fileLayout2=itemView.findViewById(R.id.filelayout2);

        cardView2=itemView.findViewById(R.id.cardview2);

        imageDisplay2=itemView.findViewById(R.id.image_display2);

        seen=itemView.findViewById(R.id.seen);
        seen2=itemView.findViewById(R.id.seen2);



    }
}
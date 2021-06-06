package com.rawtalent.bitsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rawtalent.bitsapp.DB.GroupData;
import com.squareup.picasso.Picasso;

public class ShowImage extends AppCompatActivity {

    ImageView imageView;
    Button button;

    String url,name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        button=findViewById(R.id.downloadImage);
        imageView=findViewById(R.id.imageShow);


        name=getIntent().getStringExtra("name");
        url=getIntent().getStringExtra("url");

       getImage();

        try {
            getSupportActionBar().setTitle(name);
        }catch (Exception e){

        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    DownloadManager downloadManager=(DownloadManager)getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri=Uri.parse(url);

                    DownloadManager.Request request=new DownloadManager.Request(uri);
                    request.setTitle("File Download");
                    request.setDescription("downloading file...");
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalFilesDir(ShowImage.this,"BitsApp",""+name);
                    downloadManager.enqueue(request);

                }catch (Exception e){
                    Toast.makeText(ShowImage.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });




    }


    public void getImage(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl(url);
        storageReference.getBytes(1024 * 1024 * 5).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                //       Log.d(TAG, "onSuccess: Got profile image successfully");
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}
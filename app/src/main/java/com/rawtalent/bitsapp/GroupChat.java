package com.rawtalent.bitsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.rawtalent.bitsapp.DB.GroupData;
import com.rawtalent.bitsapp.DB.ProfileImages;
import com.rawtalent.bitsapp.ModelClasses.ChatModel;
import com.rawtalent.bitsapp.ViewHolders.ChatViewHolder;
import com.rawtalent.chatencryption.AssymetricEncryption;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChat extends AppCompatActivity {


    private RecyclerView mrecyclerview;
    private FirebaseFirestore firebaseFirestore;
    private FirestoreRecyclerAdapter firestoreRecyclerAdapter;


    AlertDialog.Builder builder;
    AlertDialog progressDialog;

    ImageButton send,cancel,attach,imgbtn,pdfbtn;
    TextView filename;
    EditText message;

    LinearLayout filelayout,typelayout;

    boolean isVisible=false;

    Uri file;
    int type=1;


    boolean showFile=false;

    String nameOfFile="";

    TextView name;
    CircleImageView icon;
    ImageButton fileButton;

    FirebaseAuth mAuth;

    String groupID;
    String publicKeyOfGroup;
    String privateKeyOfGroup;
    String grpname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        mAuth=FirebaseAuth.getInstance();
        progressDialog = getBuilder().create();
        progressDialog.setCancelable(false);


        groupID=getIntent().getStringExtra("groupID");
        publicKeyOfGroup=getIntent().getStringExtra("publickey");
        privateKeyOfGroup=getIntent().getStringExtra("privatekey");
        grpname=getIntent().getStringExtra("name");


        initialize();


        String image=GroupData.getIcon(GroupChat.this,groupID);

        if (!image.equals("")){
            icon.setImageBitmap(ProfileImages.decodeBase64(image));
        }
        if (!grpname.equals("")){
            name.setText(grpname);
        }



        firebaseFirestore = FirebaseFirestore.getInstance();
        Query query = firebaseFirestore.collection(Constants.GROUPS_COLLECTION).document(groupID).collection(Constants.GROUPS_CHAT_COLLECTION)
         .orderBy("timeLong", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<ChatModel> options = new FirestoreRecyclerOptions.Builder<ChatModel>().setQuery(query, ChatModel.class).build();
        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<ChatModel, ChatViewHolder>(options) {
            @NonNull
            @Override
            public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(GroupChat.this).inflate(R.layout.chat_new_item, parent, false);
                return new ChatViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final ChatViewHolder holder, final int position, @NonNull final ChatModel model) {

                if (model.getUid().equals(mAuth.getCurrentUser().getUid())){
                    //This message was posted by you

                    holder.othermessage.setVisibility(View.GONE);
                    holder.yourmessage.setVisibility(View.VISIBLE);

                    holder.time1.setVisibility(View.GONE);
                    holder.time2.setVisibility(View.VISIBLE);


                    try {
                        holder.message2.setText(""+AssymetricEncryption.decryptMessage(Utility.stringToByteArray(model.getMessage()),Utility.stringToByteArray(privateKeyOfGroup)));
                    } catch (Exception e) {

                    }
                    holder.date2.setText(""+model.getDate());
                    holder.time2.setText(""+model.getTime());
                    holder.filename2.setText(""+model.getFileName());

                    if (model.getTag().equals("pdf")){
                        holder.filelayout2.setVisibility(View.VISIBLE);
                        holder.image2.setVisibility(View.GONE);
                    }else if (model.getTag().equals("img")){
                        holder.filelayout2.setVisibility(View.GONE);
                        holder.image2.setVisibility(View.VISIBLE);

                        Picasso.get()
                                .load(model.getUrl())
                                .fit()
                                .centerCrop().placeholder(R.drawable.loginbackground)
                                .into(holder.image2);
                    }
                    else{
                        holder.filelayout2.setVisibility(View.GONE);
                        holder.image2.setVisibility(View.GONE);
                    }

                }else{


                    holder.othermessage.setVisibility(View.VISIBLE);
                    holder.yourmessage.setVisibility(View.GONE);

                    holder.time2.setVisibility(View.GONE);
                    holder.time1.setVisibility(View.VISIBLE);


                    holder.senderName.setText(""+model.getName());

                    try {
                        holder.message1.setText(""+AssymetricEncryption.decryptMessage(Utility.stringToByteArray(model.getMessage()),Utility.stringToByteArray(privateKeyOfGroup)));
                    } catch (Exception e) {

                    }
                    holder.date1.setText(""+model.getDate());
                    holder.time1.setText(""+model.getTime());
                    holder.filename1.setText(""+model.getFileName());

                    if (model.getTag().equals("pdf")){
                        holder.filelayout1.setVisibility(View.VISIBLE);
                        holder.image1.setVisibility(View.GONE);
                    }else if (model.getTag().equals("img")){
                        holder.filelayout1.setVisibility(View.GONE);
                        holder.image1.setVisibility(View.VISIBLE);

                        Picasso.get()
                                .load(model.getUrl())
                                .fit()
                                .placeholder(R.drawable.loginbackground)
                                .into(holder.image1);

                    }
                    else{
                        holder.filelayout1.setVisibility(View.GONE);
                        holder.image1.setVisibility(View.GONE);
                    }

                }

                holder.image1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent=new Intent(GroupChat.this,ShowImage.class);
                        intent.putExtra("url",model.getUrl());
                        intent.putExtra("name",model.getFileName());
                        startActivity(intent);

                    }
                });

                holder.image2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                        Intent intent=new Intent(GroupChat.this,ShowImage.class);
                        intent.putExtra("url",model.getUrl());
                        intent.putExtra("name",model.getFileName());
                        startActivity(intent);



                    }
                });





                holder.download1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {

                            DownloadManager downloadManager=(DownloadManager)getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
                            Uri uri=Uri.parse(model.getUrl());

                            DownloadManager.Request request=new DownloadManager.Request(uri);
                            request.setTitle("File Download");
                            request.setDescription("downloading file...");
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            request.setDestinationInExternalFilesDir(GroupChat.this,"BitsApp",""+model.getFileName());
                            downloadManager.enqueue(request);

                        }catch (Exception e){
                            Toast.makeText(GroupChat.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                holder.download2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        try {

                            DownloadManager downloadManager=(DownloadManager)getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
                            Uri uri=Uri.parse(model.getUrl());

                            DownloadManager.Request request=new DownloadManager.Request(uri);
                            request.setTitle("File Download");
                            request.setDescription("downloading file...");
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            request.setDestinationInExternalFilesDir(GroupChat.this,"BitsApp",""+model.getFileName());
                            downloadManager.enqueue(request);

                        }catch (Exception e){
                            Toast.makeText(GroupChat.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });




            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if (firestoreRecyclerAdapter.getItemCount()==0){
                   // animationView.setVisibility(View.VISIBLE);

                }else{

                    mrecyclerview.smoothScrollToPosition(firestoreRecyclerAdapter.getItemCount()-1);
                    resetNumberOfUnseenMessages();
                    //  animationView.setVisibility(View.GONE);
                    //  emptyView.setVisibility(View.GONE);
                }
            }
        };

        mrecyclerview.setHasFixedSize(false);
        mrecyclerview.setLayoutManager(new LinearLayoutManager(GroupChat.this));
        mrecyclerview.setAdapter(firestoreRecyclerAdapter);



        fileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFile=!showFile;
                if (showFile){
                    Query query = firebaseFirestore.collection(Constants.GROUPS_COLLECTION).document(groupID).collection(Constants.GROUPS_CHAT_COLLECTION)
                            .whereNotEqualTo("tag","str");
                    FirestoreRecyclerOptions<ChatModel> options = new FirestoreRecyclerOptions.Builder<ChatModel>().setQuery(query, ChatModel.class).build();
                    firestoreRecyclerAdapter.updateOptions(options);

                }else{
                    Query query = firebaseFirestore.collection(Constants.GROUPS_COLLECTION).document(groupID).collection(Constants.GROUPS_CHAT_COLLECTION)
                            .orderBy("timeLong", Query.Direction.ASCENDING);
                    FirestoreRecyclerOptions<ChatModel> options = new FirestoreRecyclerOptions.Builder<ChatModel>().setQuery(query, ChatModel.class).build();
                    firestoreRecyclerAdapter.updateOptions(options);

                }
            }
        });

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

    public void initialize(){
        mrecyclerview=findViewById(R.id.chatrecyclerview);
        send=findViewById(R.id.sendbtn);
        cancel=findViewById(R.id.cancelfile);
        attach=findViewById(R.id.attachfile);
        filename=findViewById(R.id.filenametv);
        message=findViewById(R.id.message_et);
        imgbtn=findViewById(R.id.img);
        pdfbtn=findViewById(R.id.pdf);
        filelayout=findViewById(R.id.fileshowlayout);
        typelayout=findViewById(R.id.selectfilelayout);

        name = findViewById(R.id.nameOfGroup);
        fileButton = findViewById(R.id.filebutton);
        icon = findViewById(R.id.groupicon);



        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(GroupChat.this, "changing activity", Toast.LENGTH_SHORT).show();
               Intent intent=new Intent(GroupChat.this,GroupInformation.class);
               intent.putExtra("groupID",groupID);
               startActivity(intent);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendMessage();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setCancel();

            }
        });

        imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectFile(1);
            }
        });

        pdfbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectFile(2);

            }
        });

        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAttach();
            }
        });
    }


    public void sendMessage(){

        String msg=message.getText().toString();


        if ((msg.equals("")||msg.isEmpty())&&file==null){
            return;
        }

        try {
            msg= Arrays.toString(AssymetricEncryption.encryptMessage(msg,Utility.stringToByteArray(publicKeyOfGroup)));
        } catch (Exception e) {
            Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
            return;
        }


        progressDialog.show();

        if (file!=null){

            saveFile(msg);
        }else{
            postMessage(msg,"",0);
        }



    }

    public void saveFile(String msg){
        FirebaseAuth mAuth= FirebaseAuth.getInstance();
        String uuid=mAuth.getCurrentUser().getUid();

        StorageReference mStorageReference= FirebaseStorage.getInstance().getReference();
        StorageReference reference;

        if (type==1){
            reference = mStorageReference.child("Group/Images" + "/" + nameOfFile+""+System.currentTimeMillis());
        }else{
             reference = mStorageReference.child("Group/PDFs" + "/" + nameOfFile+""+System.currentTimeMillis());
        }
        reference.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        postMessage(msg,uri.toString(),type);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(GroupChat.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressDialog.dismiss();
                Toast.makeText(GroupChat.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void postMessage(String msg,String url,int t){

        FirebaseFirestore db=FirebaseFirestore.getInstance();
        FirebaseAuth mAuth= FirebaseAuth.getInstance();
        String uuid=mAuth.getCurrentUser().getUid();

        Map<String,Object> map=new HashMap<>();
        map.put("name",mAuth.getCurrentUser().getDisplayName());
        map.put("uid",uuid);
        map.put("message",msg);
        map.put("url",url);
        map.put("fileName",nameOfFile);


        if (t==0){
            map.put("tag","str");
        }else if (t==1){
            map.put("tag","img");
        }else if (t==2){
            map.put("tag","pdf");
        }

        Date date= Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat timeformat=new SimpleDateFormat("HH:mm:ss");

        String dateString=dateFormat.format(date);
        String timeString=timeformat.format(date);

        long timeInLong=date.getTime();

        map.put("time",timeString);
        map.put("date",dateString);
        map.put("timeLong",timeInLong);


        db.collection(Constants.GROUPS_COLLECTION).document(groupID).collection("Chats").document().set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                message.setText("");

                Map<String,Object> map2=new HashMap<>();
                map2.put(Constants.USERCONTACTS_LAST_MESSAGE_TIMELONG,timeInLong);
                map2.put(Constants.USERCONTACTS_LAST_MESSAGE_TIME,timeString);
                map2.put(Constants.USERCONTACTS_LAST_MESSAGE_DATE,dateString);
                map2.put(Constants.USERCONTACTS_LAST_MESSAGE,msg);
                map2.put(Constants.USERCONTACTS_NOTIFICATIONS, FieldValue.increment(1));
                sendMessageToAll(map2);

                setCancel();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
             progressDialog.dismiss();
                Toast.makeText(GroupChat.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setCancel(){

        file=null;
        nameOfFile="";
        filelayout.setVisibility(View.GONE);
    }

    public void setAttach(){


        if (isVisible){
            typelayout.setVisibility(View.GONE);
        }else{
            typelayout.setVisibility(View.VISIBLE);
        }

        isVisible=(!isVisible);

    }

    private void selectFile(int t) {
        Intent intent = new Intent();

        if (t==1){
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
        }else{
            intent.setType("application/pdf");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select PDF"), 2);

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            file = data.getData();
            //imageView.setVisibility(View.VISIBLE);
            try {

                Cursor cursor=getContentResolver().query(file,null,null,null,null);
                int nameIndex=cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                cursor.moveToFirst();

                nameOfFile=cursor.getString(nameIndex);
                filename.setText(""+cursor.getString(nameIndex));

                cursor.close();
                type=1;
                setAttach();
                filelayout.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                Toast.makeText(this, " " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == 2 && resultCode == RESULT_OK && data != null && data.getData() != null) {


            file = data.getData();
            //imageView.setVisibility(View.VISIBLE);
            try {

                Cursor cursor=getContentResolver().query(file,null,null,null,null);
                int nameIndex=cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                cursor.moveToFirst();
                nameOfFile=cursor.getString(nameIndex);
               filename.setText(""+cursor.getString(nameIndex));

                cursor.close();

                type=2;
               setAttach();
                filelayout.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                Toast.makeText(this, " " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public AlertDialog.Builder getBuilder() {
        if (builder == null) {
            builder = new AlertDialog.Builder(GroupChat.this);
            builder.setTitle("processing...");

            final ProgressBar progressBar = new ProgressBar(GroupChat.this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            progressBar.setLayoutParams(layoutParams);
            builder.setView(progressBar);
        }
        return builder;

    }


    public void sendMessageToAll(Map<String,Object> map){
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection(Constants.GROUPS_COLLECTION).document(groupID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
              List<String> members= (List<String>) documentSnapshot.get("members");
              for (int i=0;i<members.size();i++){
                  setLastMessage(map,members.get(i));
              }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    public void setLastMessage(Map<String,Object> map,String uuid){

        FirebaseAuth auth=FirebaseAuth.getInstance();

        if (uuid.equals(auth.getCurrentUser().getUid())){
            Map<String,Object> map2=new HashMap<>();
            map2.put(Constants.USERCONTACTS_LAST_MESSAGE_TIMELONG,map.get(Constants.USERCONTACTS_LAST_MESSAGE_TIMELONG));
            map2.put(Constants.USERCONTACTS_LAST_MESSAGE_TIME,map.get(Constants.USERCONTACTS_LAST_MESSAGE_TIME));
            map2.put(Constants.USERCONTACTS_LAST_MESSAGE_DATE,map.get(Constants.USERCONTACTS_LAST_MESSAGE_DATE));
            map2.put(Constants.USERCONTACTS_LAST_MESSAGE,map.get(Constants.USERCONTACTS_LAST_MESSAGE));
            map2.put(Constants.USERCONTACTS_NOTIFICATIONS, 0);

            FirebaseFirestore db=FirebaseFirestore.getInstance();
            db.collection(Constants.USER_COLLECTION).document(uuid).collection(Constants.USER_GROUPS_COLLECTION).document(groupID).update(map2);
        }else {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(Constants.USER_COLLECTION).document(uuid).collection(Constants.USER_GROUPS_COLLECTION).document(groupID).update(map);
        }
    }


    public void resetNumberOfUnseenMessages(){
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection(Constants.USER_COLLECTION).document(mAuth.getCurrentUser().getUid()).collection(Constants.USER_GROUPS_COLLECTION).document(groupID)
                .update(Constants.USERCONTACTS_NOTIFICATIONS, 0);
    }



}
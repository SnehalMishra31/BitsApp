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
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import com.rawtalent.bitsapp.DB.MyMessages;
import com.rawtalent.bitsapp.DB.ProfileImages;
import com.rawtalent.bitsapp.DB.UserSharedPreference;
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
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
public class PersonalChat extends AppCompatActivity {


    boolean isTytping=false;
    long oppositeTypingDate=0;
    Handler handler,handler2;
    Runnable runnable,runnable2;



    TextView typingtv;

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


    CircleImageView imageView;
    TextView profileName;

    String nameOfFile="";


    FirebaseAuth mAuth;

    String chatID;
    String opposite_uid;
    String userName;
    String publicKey="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_chat);


        chatID = getIntent().getStringExtra("chatID");
        opposite_uid = getIntent().getStringExtra("uid");
        userName = getIntent().getStringExtra("name");
        publicKey = getIntent().getStringExtra("key");

        initialize();

        firebaseFirestore = FirebaseFirestore.getInstance();
        Query query = firebaseFirestore.collection(Constants.CHATROOM_COLLECTION).document(chatID).collection("Chats")
                .orderBy("timeLong", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<ChatModel> options = new FirestoreRecyclerOptions.Builder<ChatModel>().setQuery(query, ChatModel.class).build();
        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<ChatModel, ChatViewHolder>(options) {
            @NonNull
            @Override
            public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(PersonalChat.this).inflate(R.layout.chat_new_item, parent, false);
                return new ChatViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final ChatViewHolder holder, final int position, @NonNull final ChatModel model) {

                holder.senderName.setVisibility(View.GONE);

               if (model.getUid().equals(mAuth.getCurrentUser().getUid())){
                    //This message was posted by you

                    holder.othermessage.setVisibility(View.GONE);
                    holder.yourmessage.setVisibility(View.VISIBLE);
                    holder.seen1.setVisibility(View.VISIBLE);
                    holder.seen2.setVisibility(View.VISIBLE);

                    holder.time1.setVisibility(View.GONE);
                    holder.time2.setVisibility(View.VISIBLE);




                    if (model.getSeen()==1){
                        Log.d("lastMessage", "onBindViewHolder:seen true");
                        holder.seen1.setImageResource(R.drawable.ic_seen);
                        holder.seen2.setImageResource(R.drawable.ic_seen);
                    }else{
                        Log.d("lastMessage", "onBindViewHolder:seen false");
                        holder.seen1.setImageResource(R.drawable.ic_unseen);
                        holder.seen2.setImageResource(R.drawable.ic_unseen);
                    }



                   try {
                      String msg= MyMessages.getMessage(PersonalChat.this,opposite_uid,""+model.getTimeLong());
                      if (msg==null){
                          holder.message2.setText("message deleted by you");
                          holder.message2.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                      }else {
                          holder.message2.setText("" + msg);
                          holder.message2.setTypeface(Typeface.DEFAULT);
                      }
                   }catch (Exception e){

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
                    holder.seen1.setVisibility(View.GONE);
                    holder.seen2.setVisibility(View.GONE);
                    holder.time2.setVisibility(View.GONE);
                    holder.time1.setVisibility(View.VISIBLE);


                    holder.senderName.setText(""+model.getName());
                   try {
                       holder.message2.setTypeface(Typeface.DEFAULT);
                       holder.message1.setText(""+AssymetricEncryption.decryptMessage(Utility.stringToByteArray(model.getMessage()), UserSharedPreference.getPrivateKey(PersonalChat.this)));
                   } catch (Exception e) {
                       holder.message2.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                       holder.message1.setText("error while decrypting the message");
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

                        Intent intent=new Intent(PersonalChat.this, ShowImage.class);
                        intent.putExtra("url",model.getUrl());
                        intent.putExtra("name",model.getFileName());
                        startActivity(intent);

                    }
                });

                holder.image2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent=new Intent(PersonalChat.this,ShowImage.class);
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
                            request.setDestinationInExternalFilesDir(PersonalChat.this,"BitsApp",""+model.getFileName());
                            downloadManager.enqueue(request);

                        }catch (Exception e){
                            Toast.makeText(PersonalChat.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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
                            request.setDestinationInExternalFilesDir(PersonalChat.this,"BitsApp",""+model.getFileName());
                            downloadManager.enqueue(request);

                        }catch (Exception e){
                            Toast.makeText(PersonalChat.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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
                    setSeen();
                    //  animationView.setVisibility(View.GONE);
                    //  emptyView.setVisibility(View.GONE);
                }
            }
        };
        mrecyclerview.setHasFixedSize(false);
        mrecyclerview.setLayoutManager(new LinearLayoutManager(PersonalChat.this));
        mrecyclerview.setAdapter(firestoreRecyclerAdapter);


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

        mAuth=FirebaseAuth.getInstance();
        progressDialog = getBuilder().create();
        progressDialog.setCancelable(false);

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

        imageView=findViewById(R.id.profileicon);
        profileName=findViewById(R.id.profileName);

        typingtv=findViewById(R.id.typing);
        typingtv.setVisibility(View.GONE);


        setTypingListener();
       setHandler1();
       setHandler2();



        profileName.setText(""+userName);

        try {
            String pImage= ProfileImages.retrieveImage(PersonalChat.this,opposite_uid);
            if (pImage!=null && !pImage.equals("")){
                imageView.setImageBitmap(ProfileImages.decodeBase64(pImage));
            }
        }catch (Exception e){

        }

        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length()!=0){
                            isTytping=true;
                        }else{
                            isTytping=false;
                        }
            }

            @Override
            public void afterTextChanged(Editable s) {

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
                        Toast.makeText(PersonalChat.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressDialog.dismiss();
                Toast.makeText(PersonalChat.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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
        map.put("seen",0);

        try {
            MyMessages.setMessage(PersonalChat.this,opposite_uid,""+timeInLong,msg);
            Log.d("MSGCHECK", "postMessage: "+MyMessages.getMessage(PersonalChat.this,opposite_uid,msg));
        }catch (Exception e){

        }

        try {
            msg= Arrays.toString(AssymetricEncryption.encryptMessage(msg,Utility.stringToByteArray(publicKey)));
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }

        map.put("message",msg);

        String finalMsg = msg;
        db.collection(Constants.CHATROOM_COLLECTION).document(chatID).collection("Chats").document().set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                message.setText("");



                setCancel();
                try {
                    if (finalMsg.equals("")|| finalMsg.isEmpty()){
                        setLastMessage(nameOfFile,timeString,dateString,timeInLong);
                    }else{
                        setLastMessage(finalMsg,timeString,dateString,timeInLong);
                    }
                }catch (Exception e){
                    setLastMessage(finalMsg,timeString,dateString,timeInLong);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(PersonalChat.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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
            builder = new AlertDialog.Builder(PersonalChat.this);
            builder.setTitle("processing...");

            final ProgressBar progressBar = new ProgressBar(PersonalChat.this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            progressBar.setLayoutParams(layoutParams);
            builder.setView(progressBar);
        }
        return builder;
    }


    public void setSeen(){

        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection(Constants.CHATROOM_COLLECTION).document(chatID).collection("Chats").whereEqualTo("uid",opposite_uid).whereEqualTo("seen",0).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documents=queryDocumentSnapshots.getDocuments();
                updateStatus(documents);
            }
        });
    }

    public void updateStatus(List<DocumentSnapshot> documents){

        for (int i=0;i<documents.size();i++) {
            try {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection(Constants.CHATROOM_COLLECTION).document(chatID).collection("Chats").document(documents.get(i).getId()).update("seen",1);
            }catch (Exception e){

            }
        }

        resetNumberOfUnseenMessages();
    }





    public void setLastMessage(String message,String timeString,String dateString,long time){
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        FirebaseFirestore db=FirebaseFirestore.getInstance();

        Map<String,Object> map=new HashMap<>();
        map.put(Constants.USERCONTACTS_LAST_MESSAGE,message);
        map.put(Constants.USERCONTACTS_NOTIFICATIONS,FieldValue.increment(1));
        map.put(Constants.USERCONTACTS_LAST_MESSAGE_TIMELONG,time);
        map.put(Constants.USERCONTACTS_LAST_MESSAGE_TIME,timeString);
        map.put(Constants.USERCONTACTS_LAST_MESSAGE_DATE,dateString);
        map.put(Constants.USERCONTACTS_LAST_MESSAGE_SENDER,mAuth.getCurrentUser().getUid());

        db.collection(Constants.USER_COLLECTION).document(opposite_uid).collection(Constants.CONTACTS_COLLECTION).document(mAuth.getCurrentUser().getUid())
                .update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("lastMessage", "Success!"+message);
                db.collection(Constants.USER_COLLECTION).document(mAuth.getCurrentUser().getUid()).collection(Constants.CONTACTS_COLLECTION).document(opposite_uid)
                        .update(Constants.USERCONTACTS_LAST_MESSAGE,message,Constants.USERCONTACTS_LAST_MESSAGE_TIMELONG,time,
                                Constants.USERCONTACTS_LAST_MESSAGE_TIME,timeString,
                                Constants.USERCONTACTS_LAST_MESSAGE_DATE,dateString,
                                Constants.USERCONTACTS_LAST_MESSAGE_SENDER,mAuth.getCurrentUser().getUid());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("lastMessage", "onFailure: "+e.getMessage());
            }
        });
    }

    public void resetNumberOfUnseenMessages(){
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection(Constants.USER_COLLECTION).document(mAuth.getCurrentUser().getUid()).collection(Constants.CONTACTS_COLLECTION).document(opposite_uid)
                .update(Constants.USERCONTACTS_NOTIFICATIONS, 0);
    }


    public void setTypingListener(){

        FirebaseFirestore db=FirebaseFirestore.getInstance();
        FirebaseAuth auth=FirebaseAuth.getInstance();
        DocumentReference reference=db.collection(Constants.USER_COLLECTION).document(opposite_uid).collection(Constants.CONTACTS_COLLECTION)
                .document(auth.getCurrentUser().getUid());
        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!=null){
                    return;
                }
                if (value!=null && value.exists()){

                    try {
                        if (value.getLong("typingDate")>0){
                            oppositeTypingDate=value.getLong("typingDate");
                        }
                    }catch (Exception e){

                    }

                }
            }
        });
    }


    public void setHandler1(){
        handler=new Handler();
        runnable=new Runnable() {
            @Override
            public void run() {
                try {

                    if (isTytping){
                        setTyping();
                    }else{

                    }

                }finally {
                    handler.postDelayed(runnable,4000);
                }

            }
        };
        runnable.run();


    }

    public void setHandler2(){
        handler2=new Handler();
        runnable2=new Runnable() {
            @Override
            public void run() {
                try {

                    if (oppositeTypingDate+5000>=Utility.getCurrentDate().getTime()){
                       typingtv.setVisibility(View.VISIBLE);
                    }else{
                        typingtv.setVisibility(View.GONE);
                    }

                }finally {
                    handler2.postDelayed(runnable2,1000);
                }

            }
        };
        runnable2.run();
    }

    public void setTyping(){
        long date=Utility.getCurrentDate().getTime();
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        FirebaseAuth auth=FirebaseAuth.getInstance();
        db.collection(Constants.USER_COLLECTION).document(auth.getCurrentUser().getUid()).collection(Constants.CONTACTS_COLLECTION).document(opposite_uid)
                .update("typingDate",date);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(runnable);
        handler2.removeCallbacks(runnable2);
        super.onDestroy();
    }
}
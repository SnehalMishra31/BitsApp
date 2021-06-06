package com.rawtalent.bitsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rawtalent.bitsapp.Contacts.ContactList;
import com.rawtalent.bitsapp.Contacts.ContactModel;
import com.rawtalent.bitsapp.Contacts.ContactsAdapter;
import com.rawtalent.bitsapp.DB.GroupData;
import com.rawtalent.bitsapp.ModelClasses.GroupListModel;
import com.rawtalent.bitsapp.ModelClasses.GroupModel;
import com.rawtalent.bitsapp.ViewHolders.GroupUserAdapter;
import com.rawtalent.chatencryption.AssymetricEncryption;
import com.rawtalent.chatencryption.UserKeys;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateGroup extends AppCompatActivity {

   public static ArrayList<ContactModel> contactModels;
    RecyclerView recyclerView;
    GroupUserAdapter adapter;

    AlertDialog.Builder builder;
    AlertDialog progressDialog;

    EditText groupName,groupDescription;
    ImageButton selectImage;
    ImageView imageView;


    private byte[] byteArray;
    private Uri mImageHolder;


    private FirebaseFirestore mFirebaseDb;
    private StorageReference mStorageReference;



    String name;
    String description="";
    Button create;
    String TAG="ABC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        mFirebaseDb = FirebaseFirestore.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        progressDialog = getBuilder().create();
        progressDialog.setCancelable(false);

        create=findViewById(R.id.creategrp);
        groupName=findViewById(R.id.groupnameet);
        imageView=findViewById(R.id.imageicon);
        groupDescription=findViewById(R.id.groupdescriptionet);
        selectImage=findViewById(R.id.selectgroupicon);

        try {
            getSupportActionBar().setTitle("Create Group");
        }catch (Exception e){

        }

        contactModels=new ArrayList<>();


        recyclerView=findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        checkPermission();

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.show();

                name=groupName.getText().toString();
                if (name.equals("")||name.isEmpty()){
                    Toast.makeText(CreateGroup.this, "Please enter group name", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                return;
                }

                description=groupDescription.getText().toString();


                ArrayList<ContactModel> models=getSelectedContacts();
                if (models.size()<=0){
                    Toast.makeText(CreateGroup.this, "You must include atleast one contact", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    return;
                }


                Log.d(TAG, "onClick: "+"creating "+name+" group:"+ models.get(0).getNumber());
                createGroup(models);

            }
        });
    }

    public void createGroup(ArrayList<ContactModel> models){

        Log.d(TAG, "createGroup: ");
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        FirebaseAuth auth=FirebaseAuth.getInstance();
        String groupID=auth.getCurrentUser().getUid()+""+Utility.getCurrentDate().getTime();

        UserKeys userKeys;

        try {
            userKeys = AssymetricEncryption.createUserKeys();

        } catch (Exception e) {
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }
        Log.d(TAG, "createGroup: "+ Arrays.toString(userKeys.getPublicKey()) +" "+ Arrays.toString(userKeys.getPrivateKey()));
        GroupModel groupModel=new GroupModel();
        groupModel.setName(name);
        groupModel.setAdmin(auth.getCurrentUser().getUid());
        groupModel.setDescription(description);

        List<String> memeberid=new ArrayList<>();
        List<String> memebernumbers=new ArrayList<>();

        memeberid.add(auth.getCurrentUser().getUid());
        memebernumbers.add(auth.getCurrentUser().getPhoneNumber());

        for (int i=0;i<models.size();i++){
            memeberid.add(models.get(i).getUuid());
            memebernumbers.add(models.get(i).getNumber());
        }


        groupModel.setMembers(memeberid);
        groupModel.setMembersNumber(memebernumbers);

        Log.d(TAG, "createGroup: calling for upload ");
        db.collection(Constants.GROUPS_COLLECTION).document(groupID).set(groupModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Log.d(TAG, "onSuccess: groupcreated");

             GroupListModel listModel=getTemplate(groupModel,userKeys);
             for (int i=0;i<memeberid.size();i++){
                 addUsersToGroup(memeberid.get(i),listModel,groupID);
             }
             uploadImage(groupID);
                Toast.makeText(CreateGroup.this, "Created the group successfully", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();

                Intent intent=new Intent(CreateGroup.this,MainActivity.class);
                startActivity(intent);
                CreateGroup.this.finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateGroup.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }


    public GroupListModel getTemplate(GroupModel groupModel, UserKeys userKeys){

        FirebaseAuth auth=FirebaseAuth.getInstance();

        GroupListModel listModel=new GroupListModel();
        listModel.setAdmin(groupModel.getAdmin());
        listModel.setName(name);
        listModel.setPublicKey(Arrays.toString(userKeys.getPublicKey()));
        listModel.setPrivateKey(Arrays.toString(userKeys.getPrivateKey()));

        String message=""+auth.getCurrentUser().getDisplayName()+" has created this group";
        try {
            message=Arrays.toString(AssymetricEncryption.encryptMessage(message,userKeys.getPublicKey()));
        } catch (Exception e) {

        }

        listModel.setLastMessage(message);
        listModel.setNotifications(0);

        Date date=Utility.getCurrentDate();
        String timeString=Utility.getCurrentTimeString(date);
        String dateString=Utility.getCurrentDateString(date);

        listModel.setLastMessageDate(date.getTime());
        listModel.setStringDate(timeString);
        listModel.setStringTime(dateString);

        return listModel;

    }
    public void addUsersToGroup(String uid,GroupListModel listModel,String groupID){
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection(Constants.USER_COLLECTION).document(uid).collection(Constants.GROUPS_COLLECTION).document(groupID).set(listModel);
    }

    public ArrayList<ContactModel> getSelectedContacts(){

        ArrayList<ContactModel> models=new ArrayList<>();

        for (int i=0;i<contactModels.size();i++){
            if (contactModels.get(i).isChecked()){
                models.add(contactModels.get(i));
            }
        }

        return models;
    }

    public void getContactList(){

        Cursor cursor=getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);

        while (cursor.moveToNext()){
            String name=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String number=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contactModels.add(new ContactModel(number,name));

        }

        Log.d(TAG, "getContactList: "+contactModels.size());
        cursor.close();

        findRelatedContacts();

    }

    public void checkPermission(){

        Log.d(TAG, "checkPermission: ");
        if (ContextCompat.checkSelfPermission(CreateGroup.this, Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(CreateGroup.this,new String[]{Manifest.permission.READ_CONTACTS},100);
        }else{
            Log.d(TAG, "checkPermission: granted");
            getContactList();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode==100 && grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            getContactList();

        }else{
            //denied permission
        }
    }

    public void findRelatedContacts(){

        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection(Constants.USERS_LIST_COLLECTION).document(Constants.USERS_LIST_COLLECTION).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<String> contacts=(List<String>)documentSnapshot.get("list");
                Log.d(TAG, "onSuccess: "+ Arrays.toString(contacts.toArray()));
                adapter=new GroupUserAdapter(matchContacts(contacts),CreateGroup.this);
                recyclerView.setAdapter(adapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateGroup.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public ArrayList<ContactModel> matchContacts(List<String> contacts){

        Map<String,Object> map=new HashMap<>();
        ArrayList<ContactModel> arrayList=new ArrayList<>();

        FirebaseAuth auth=FirebaseAuth.getInstance();
        FirebaseUser user=auth.getCurrentUser();
        String ownnumber=user.getPhoneNumber();

        for (int i=0;i<contacts.size();i++){

            String data[]=contacts.get(i).split(",");
            map.put(data[1],data[0]);

            Log.d(TAG, "matchContacts: "+data[1]+" "+data[0]);
        }


        for (int i=0;i<contactModels.size();i++){

            Log.d(TAG, "matchContacts: "+contactModels.get(i).getNumber());
            if (map.containsKey(contactModels.get(i).getNumber().trim()) && !contactModels.get(i).getNumber().equals(ownnumber)  ){

                ContactModel model=new ContactModel(contactModels.get(i).getNumber(),contactModels.get(i).getName());
                model.setUuid(""+map.get(contactModels.get(i).getNumber()));
                arrayList.add(model);

            }
        }

        contactModels=arrayList;

        Log.d(TAG, "matchContacts: "+arrayList.size());
        return arrayList;
    }

    public AlertDialog.Builder getBuilder() {
        if (builder == null) {
            builder = new AlertDialog.Builder(CreateGroup.this);
            builder.setTitle("Creating Group...");

            final ProgressBar progressBar = new ProgressBar(CreateGroup.this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            progressBar.setLayoutParams(layoutParams);
            builder.setView(progressBar);
        }
        return builder;
    }


    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Group Icon"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageHolder = data.getData();
            imageView.setVisibility(View.VISIBLE);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageHolder);
                imageView.setImageBitmap(bitmap);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//                bitmap.recycle();
                //      byteArray = stream.toByteArray();
            } catch (Exception e) {
                Toast.makeText(this, " " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void uploadImage(String groupID){
        if (mImageHolder!=null) {

            try {
                GroupData.setIcon(CreateGroup.this,groupID,Utility.encodeTobase64(MediaStore.Images.Media.getBitmap(getContentResolver(), mImageHolder)));
            }catch (Exception e){

            }
            StorageReference reference = mStorageReference.child(Constants.STORAGE_GROUP_ICON_IMAGES + "/" + groupID);
            reference.putFile(mImageHolder);
        }
    }

}
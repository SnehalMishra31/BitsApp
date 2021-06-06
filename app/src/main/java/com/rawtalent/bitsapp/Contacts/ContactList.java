package com.rawtalent.bitsapp.Contacts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rawtalent.bitsapp.Constants;
import com.rawtalent.bitsapp.ModelClasses.Contacts;
import com.rawtalent.bitsapp.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactList extends AppCompatActivity {


    ArrayList<ContactModel> contactModels;
    RecyclerView recyclerView;
    ContactsAdapter adapter;

    String TAG="contactCheck";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        try {
            getSupportActionBar().setTitle("Your Contacts");
        }catch (Exception e){

        }

        contactModels=new ArrayList<>();


        recyclerView=findViewById(R.id.contactrecyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        checkPermission();

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
        if (ContextCompat.checkSelfPermission(ContactList.this, Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(ContactList.this,new String[]{Manifest.permission.READ_CONTACTS},100);
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
                adapter=new ContactsAdapter(matchContacts(contacts),ContactList.this);
                recyclerView.setAdapter(adapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ContactList.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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

        Log.d(TAG, "matchContacts: "+arrayList.size());
        return arrayList;
    }
}
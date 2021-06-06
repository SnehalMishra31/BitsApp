package com.rawtalent.bitsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rawtalent.bitsapp.Contacts.ContactList;
import com.rawtalent.bitsapp.DB.PrivateKey;
import com.rawtalent.bitsapp.DB.UserSharedPreference;
import com.rawtalent.chatencryption.AssymetricEncryption;
import com.rawtalent.chatencryption.UserKeys;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SetProfile extends AppCompatActivity {


    AlertDialog.Builder builder;
    AlertDialog progressDialog;

    LottieAnimationView animationView;

    Button send;
    EditText name,bio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile);



        progressDialog = getBuilder().create();
        progressDialog.setCancelable(false);

        animationView=findViewById(R.id.animation_view4);
        send=findViewById(R.id.senddetails);
        name=findViewById(R.id.profilename);
        bio=findViewById(R.id.bio);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mName=name.getText().toString();
                String mBio=bio.getText().toString();

                if (mName.equals("")||mName.isEmpty()){
                    name.setError("name is mandatory");
                    return;
                }

                animationView.pauseAnimation();
                progressDialog.show();
                generateKeys(mName,mBio);



            }
        });

    }


    public void generateKeys(String name,String bio){

        try {
            UserKeys userKeys= AssymetricEncryption.createUserKeys();

            UserSharedPreference.setPrivateKey(SetProfile.this,userKeys.getPrivateKey());
            UserSharedPreference.setPublicKey(SetProfile.this,userKeys.getPublicKey());
            UserSharedPreference.setBio(SetProfile.this,""+bio);

            uploadKeys(userKeys.getPublicKey(),userKeys.getPrivateKey());

            FirebaseAuth auth=FirebaseAuth.getInstance();
            String number=auth.getCurrentUser().getPhoneNumber();
            setDetails(name,bio, Arrays.toString(userKeys.getPublicKey()),number);

        } catch (Exception e) {
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

    }
    public AlertDialog.Builder getBuilder() {
        if (builder == null) {
            builder = new AlertDialog.Builder(SetProfile.this);
            builder.setTitle("Please wait...");

            final ProgressBar progressBar = new ProgressBar(SetProfile.this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            progressBar.setLayoutParams(layoutParams);
            builder.setView(progressBar);
        }
        return builder;

    }

    public void uploadKeys(byte[] publickKey,byte[] privateKey){
        FirebaseFirestore db=FirebaseFirestore.getInstance();

        FirebaseAuth auth=FirebaseAuth.getInstance();
        Map<String,Object> map=new HashMap<>();
        map.put("key", Arrays.toString(publickKey));
        db.collection(Constants.KEY_COLLECTIONS).document(Constants.KEY_PUBLIC).collection(Constants.KEY_PERSONS).document(auth.getCurrentUser().getUid()).set(map);

        map=new HashMap<>();
        map.put("key", Arrays.toString(privateKey));
        db.collection(Constants.KEY_COLLECTIONS).document(Constants.KEY_PRIVATE).collection(Constants.KEY_PERSONS).document(auth.getCurrentUser().getUid()).set(map);
    }


    public void setDetails(String mname,String mbio,String publicKey,String number){
        Map<String,Object> map=new HashMap<>();
        map.put(Constants.USER_NAME,mname);
        map.put(Constants.USER_BIO,mbio);
        map.put(Constants.USERCONTACTS_PUBLICKEY,publicKey);
        map.put(Constants.USER_NUMBER,number);

        FirebaseAuth auth=FirebaseAuth.getInstance();
        FirebaseFirestore db=FirebaseFirestore.getInstance();

        db.collection(Constants.USER_COLLECTION).document(auth.getCurrentUser().getUid()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(SetProfile.this, "Profile Uploaded Successfully!", Toast.LENGTH_SHORT).show();

                FirebaseAuth auth1=FirebaseAuth.getInstance();
                FirebaseUser user = auth1.getCurrentUser();
                UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(mname).build();
                user.updateProfile(profileChangeRequest);

                addUserToUsersList(auth1.getCurrentUser().getPhoneNumber());
                Intent intent=new Intent(SetProfile.this, ContactList.class);
                progressDialog.dismiss();
                startActivity(intent);
                SetProfile.this.finish();



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SetProfile.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }


    public void addUserToUsersList(String number){

        FirebaseAuth auth=FirebaseAuth.getInstance();
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        number=auth.getCurrentUser().getUid()+","+number;
        db.collection(Constants.USERS_LIST_COLLECTION).document(Constants.USERS_LIST_COLLECTION).update("list", FieldValue.arrayUnion(number));
    }

}
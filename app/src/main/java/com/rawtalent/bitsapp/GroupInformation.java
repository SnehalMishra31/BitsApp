package com.rawtalent.bitsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.rawtalent.bitsapp.DB.GroupData;
import com.rawtalent.bitsapp.DB.ProfileImages;
import com.rawtalent.bitsapp.GroupInfo.MemberModel;
import com.rawtalent.bitsapp.GroupInfo.MembersAdapter;
import com.rawtalent.bitsapp.ModelClasses.GroupModel;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupInformation extends AppCompatActivity {


    private Uri mImageHolder;


    Button save;

    private FirebaseFirestore mFirebaseDb;
    private StorageReference mStorageReference;

    ImageView groupIcon;
    TextView name,description;
    RecyclerView recyclerView;
    MembersAdapter adapter;

    ImageButton selectIcon;
    String groupID;

    boolean isAdmin=false;

    AlertDialog.Builder builder;
    AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_information);


        name=findViewById(R.id.groupnameet);
        description=findViewById(R.id.groupdescriptionet);
        selectIcon=findViewById(R.id.selectgroupicon);
        recyclerView=findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        groupIcon=findViewById(R.id.imageicon);
        save=findViewById(R.id.savechanges);

        name.setEnabled(false);
        description.setEnabled(false);
        selectIcon.setVisibility(View.GONE);


        groupID=getIntent().getStringExtra("groupID");
        progressDialog = getBuilder().create();
        progressDialog.setCancelable(false);

        selectIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String grpname=""+name.getText().toString();
                String grpdescription=""+description.getText().toString();

                if (grpname.equals("")|| grpdescription.equals("")){
                    Toast.makeText(GroupInformation.this, "Invalid Changes", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveChanges(grpname,grpdescription);
            }
        });

        getGroupData();
    }

    public void saveChanges(String name,String description){
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        Map<String,Object> map=new HashMap<>();
        map.put("name",name);
        map.put("description",description);
        db.collection(Constants.GROUPS_COLLECTION).document(groupID).update(map);
        uploadImage(groupID);

        Toast.makeText(this, "Changes will be updated soon", Toast.LENGTH_SHORT).show();

    }

    public void getGroupData(){

        progressDialog.show();
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection(Constants.GROUPS_COLLECTION).document(groupID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                setData(documentSnapshot);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GroupInformation.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }


    public void setData(DocumentSnapshot documentSnapshot){

        GroupModel model=documentSnapshot.toObject(GroupModel.class);
        if (model != null) {
            name.setText(model.getName());
            description.setText(model.getDescription());
            FirebaseAuth auth=FirebaseAuth.getInstance();
            if (auth.getCurrentUser().getUid().equals(model.getAdmin())){
                name.setEnabled(true);
                description.setEnabled(true);
                selectIcon.setVisibility(View.VISIBLE);
                isAdmin=true;
            }

            List<String> membersList=model.getMembers();
            List<String> membersNumber=model.getMembersNumber();

            ArrayList<MemberModel> memberModels=new ArrayList<>();

            for (int i=0;i<membersList.size();i++){
                MemberModel obj=new MemberModel();
                obj.setName(membersNumber.get(i));
                obj.setUid(membersList.get(i));
                String mImage=""+ ProfileImages.retrieveImage(GroupInformation.this,obj.getUid());
                if (mImage.equals("")||mImage.isEmpty()){

                }else{
                    Bitmap bitmap=ProfileImages.decodeBase64(mImage);
                    obj.setImage(bitmap);
                }

                memberModels.add(obj);
            }

            adapter=new MembersAdapter(memberModels,GroupInformation.this,isAdmin,groupID);
            recyclerView.setAdapter(adapter);





        }

        progressDialog.dismiss();
    }

    public AlertDialog.Builder getBuilder() {
        if (builder == null) {
            builder = new AlertDialog.Builder(GroupInformation.this);
            builder.setTitle("Please wait...");

            final ProgressBar progressBar = new ProgressBar(GroupInformation.this);
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
            groupIcon.setVisibility(View.VISIBLE);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageHolder);
                groupIcon.setImageBitmap(bitmap);
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
                GroupData.setIcon(GroupInformation.this,groupID,Utility.encodeTobase64(MediaStore.Images.Media.getBitmap(getContentResolver(), mImageHolder)));
            }catch (Exception e){

            }
            StorageReference reference = mStorageReference.child(Constants.STORAGE_GROUP_ICON_IMAGES + "/" + groupID);
            reference.putFile(mImageHolder);
        }
    }
}
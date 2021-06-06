package com.rawtalent.bitsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rawtalent.bitsapp.DB.ProfileImages;
import com.rawtalent.bitsapp.DB.UserSharedPreference;

public class SplashScreen extends AppCompatActivity {

    TextView tv1,tv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        tv1=findViewById(R.id.textView4);
        tv2=findViewById(R.id.tv2);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        LottieAnimationView lottieAnimationView=findViewById(R.id.animation_view);

        lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                tv1.setVisibility(View.VISIBLE);
                tv2.setVisibility(View.VISIBLE);
                check();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


    }

    public void check(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();

                if (user != null) {
                    //User is logged in
                    try {
                        if (user.getDisplayName().equals("") || user.getDisplayName().isEmpty()) {
                            //User has not set profile yet
                            gotoSetProfile();
                        } else {
                            //User has set his profile go to dashboard directly
                            gotoDashBoard();
                        }
                    } catch (Exception e) {
                        gotoSetProfile();
                    }

                } else {
                    //No user is logged in go to Login Page
                    gotoLoginPage();
                }

            }
        }, 2000);
    }

    public void gotoDashBoard() {

        FirebaseAuth auth = FirebaseAuth.getInstance();

        String image = UserSharedPreference.getProfileImage(SplashScreen.this);
        if (image.equals("") || image.isEmpty()) {
            getImage();
        }
        Intent myIntent = new Intent(SplashScreen.this, MainActivity.class);
        SplashScreen.this.startActivity(myIntent);
        SplashScreen.this.finish();
    }

    public void gotoLoginPage() {
        Intent myIntent = new Intent(SplashScreen.this, LoginActivity.class);
        SplashScreen.this.startActivity(myIntent);
        SplashScreen.this.finish();
    }

    public void gotoSetProfile() {
        Intent myIntent = new Intent(SplashScreen.this, SetProfile.class);
        SplashScreen.this.startActivity(myIntent);
        SplashScreen.this.finish();
    }


    public void getImage() {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        FirebaseAuth mAuth2 = FirebaseAuth.getInstance();
        StorageReference storageReference = storage.getReference().child(Constants.STORAGE_PROFILEIMAGES).child(mAuth2.getCurrentUser().getUid());
        storageReference.getBytes(1024 * 1024 * 5).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                //       Log.d(TAG, "onSuccess: Got profile image successfully");
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                if (bitmap != null) {
                    try {
                        UserSharedPreference.setProfileImage(SplashScreen.this,bitmap);
                    }catch (Exception e){

                    }
                } else {

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
               }
        });
    }
}

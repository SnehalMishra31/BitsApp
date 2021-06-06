package com.rawtalent.bitsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rawtalent.bitsapp.DB.PrivateKey;
import com.rawtalent.bitsapp.DB.UserSharedPreference;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {


    FirebaseAuth auth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks changedCallbacks;

    String OTP = "";
    String NUMBER = "";

    Button verifyOTP, sendOTP;
    EditText countrycode, mobile;
    EditText otp1, otp2, otp3, otp4, otp5, otp6;
    TextView displaymobilenumber;
    LinearLayout numberBlock, otpBlock;

    AlertDialog.Builder builder;
    AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initialize();

        sendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String num = "" + mobile.getText().toString();
                String code = "" + countrycode.getText().toString();


                if (num.equals("") || code.equals("") || num.length() != 10) {
                    Toast.makeText(LoginActivity.this, "Please enter a valid mobile number", Toast.LENGTH_SHORT).show();
                    return;
                }
                String number = code + num;
                NUMBER = number;
                PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth).setPhoneNumber(number).setTimeout(60L, TimeUnit.SECONDS).setActivity(LoginActivity.this)
                        .setCallbacks(changedCallbacks).build();
                PhoneAuthProvider.verifyPhoneNumber(options);

                progressDialog.show();
            }
        });

        verifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String x = getOtp();
                if (x.equals("") || x.length() != 6) {
                    Toast.makeText(LoginActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressDialog.show();

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(OTP, x);
                auth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        FirebaseUser user = auth.getCurrentUser();

                        try {
                            if (user.getDisplayName().equals("")) {
                                Intent intent = new Intent(LoginActivity.this, SetProfile.class);
                                startActivity(intent);
                                progressDialog.dismiss();
                                LoginActivity.this.finish();
                            } else {

                                Log.d("RRCHECK", "onSuccess: " + user.getDisplayName());
                                checkForKey();
                            }
                        } catch (Exception e) {
                            Intent intent = new Intent(LoginActivity.this, SetProfile.class);
                            startActivity(intent);
                            progressDialog.dismiss();
                            LoginActivity.this.finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                    }
                });
            }
        });

    }

    public void checkForKey() {

        byte key[] = UserSharedPreference.getPrivateKey(LoginActivity.this);
        if (key == null) {
            getImage();
            getPrivateKeyFromFirestore();
        } else {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            LoginActivity.this.finish();
        }
    }

    public void initialize() {

        auth = FirebaseAuth.getInstance();
        progressDialog = getBuilder().create();
        progressDialog.setCancelable(false);

        verifyOTP = findViewById(R.id.verifyotp);
        sendOTP = findViewById(R.id.sendotp);

        countrycode = findViewById(R.id.countrycode);
        mobile = findViewById(R.id.mobile);
        displaymobilenumber = findViewById(R.id.displaynumber);

        otp1 = findViewById(R.id.otpbox1);
        otp2 = findViewById(R.id.otpbox2);
        otp3 = findViewById(R.id.otpbox3);
        otp4 = findViewById(R.id.otpbox4);
        otp5 = findViewById(R.id.otpbox5);
        otp6 = findViewById(R.id.otpbox6);

        numberBlock = findViewById(R.id.numberblock);
        otpBlock = findViewById(R.id.otpblock);


        changedCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {


            }


            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                progressDialog.dismiss();
                numberBlock.setVisibility(View.GONE);
                otpBlock.setVisibility(View.VISIBLE);

                displaymobilenumber.setText(NUMBER);
                OTP = s;
                //otp has been sent
                Toast.makeText(LoginActivity.this, "OTP sent", Toast.LENGTH_SHORT).show();
            }
        };

    }

    public String getOtp() {
        String otp = otp1.getText().toString() + otp2.getText().toString() + otp3.getText().toString() +
                otp4.getText().toString() + otp5.getText().toString() + otp6.getText().toString();
        return otp;
    }

    public AlertDialog.Builder getBuilder() {
        if (builder == null) {
            builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle("Please wait...");

            final ProgressBar progressBar = new ProgressBar(LoginActivity.this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            progressBar.setLayoutParams(layoutParams);
            builder.setView(progressBar);
        }
        return builder;

    }

    public void getPublicKeyFromFirestore() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(Constants.KEY_COLLECTIONS).document(Constants.KEY_PUBLIC).collection(Constants.KEY_PERSONS).document(auth.getCurrentUser().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserSharedPreference.setPublicKey(LoginActivity.this, Utility.stringToByteArray("" + documentSnapshot.getString("key")));
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                progressDialog.dismiss();
                startActivity(intent);
                LoginActivity.this.finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getPrivateKeyFromFirestore() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(Constants.KEY_COLLECTIONS).document(Constants.KEY_PRIVATE).collection(Constants.KEY_PERSONS).document(auth.getCurrentUser().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserSharedPreference.setPrivateKey(LoginActivity.this, Utility.stringToByteArray("" + documentSnapshot.getString("key")));
                getPublicKeyFromFirestore();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
                    UserSharedPreference.setProfileImage(LoginActivity.this,bitmap);
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
package com.example.lab2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lab2.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
//import com.squareup.picasso.Picasso;

import java.math.BigInteger;
import java.security.SecureRandom;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class HomeActivity extends AppCompatActivity {
    private Button btnLogout, btnHistory, btnCalculator;
    private EditText emailId, firstname, lastname, numberPhone;
    private int CAMERA_REQUEST_CODE = 0;
    private ProgressDialog mProgressDialog;
    private StorageReference mStorage;
    private DatabaseReference mDataBase;
    private ImageView mImage;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
    }

    public void init() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.editEmail);
        emailId.setEnabled(false);
        firstname = findViewById(R.id.editFirstname);
        firstname.setEnabled(false);
        lastname = findViewById(R.id.editLastname);
        lastname.setEnabled(false);
        numberPhone = findViewById(R.id.editNumberPhone);
        numberPhone.setEnabled(false);
        btnLogout = findViewById(R.id.logout);
        mStorage = FirebaseStorage.getInstance().getReference();
        mDataBase = FirebaseDatabase.getInstance().getReference();
        mImage = findViewById(R.id.imageAvatar);

        mStorage = FirebaseStorage.getInstance().getReference();
        mDataBase = FirebaseDatabase.getInstance().getReference();
        mDataBase.child("USERS").child(mFirebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    //Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    User user = task.getResult().getValue(User.class);
                    emailId.setText(user.email);
                    firstname.setText(user.firstname);
                    lastname.setText(user.lastname);
                    numberPhone.setText(user.numberPhone);
                    Toast.makeText(HomeActivity.this, user.numberPhone, Toast.LENGTH_SHORT).show();
                    if (!user.image.equals("default") && !TextUtils.isEmpty(user.image)) {
                        Picasso.get()
                                .load(Uri.parse(user.image))
                                .into(mImage);
                    }
                }
            }
        });
    }

    public void onClickImage(View view)
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if (intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(Intent.createChooser(intent, "Select a pic"), CAMERA_REQUEST_CODE);
        }
    }

    public void LogOut(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
    }

    public void GoToCalculator(View view){
        startActivity(new Intent(HomeActivity.this, CalculatorActivity.class));
    }


    public String GetRandomString(){
        return new BigInteger(130, new SecureRandom()).toString(32);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && requestCode == RESULT_OK){
            if (mFirebaseAuth.getCurrentUser() == null){
                return;
            }

            mProgressDialog.setMessage("Uploading image...");
            mProgressDialog.show();
            final Uri uri = data.getData();
            if (uri == null){
                mProgressDialog.dismiss();
                return;
            }

            final StorageReference filePath = mStorage.child("Photos").child(GetRandomString());
            final DatabaseReference currentUserDB = mDataBase.child(mFirebaseAuth.getCurrentUser().getUid());
            currentUserDB.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if (!user.image.equals("default") || TextUtils.isEmpty(user.image)) {
                        Task<Void> task = FirebaseStorage.getInstance().getReferenceFromUrl(user.image).delete();
                        task.addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()){
                                Toast.makeText(HomeActivity.this, "Delete image successful.", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(HomeActivity.this, "Delete image unsuccessful.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    currentUserDB.child("image").removeEventListener(this);
                    filePath.putFile(uri).addOnSuccessListener(HomeActivity.this, taskSnapshot -> {
                        mProgressDialog.dismiss();
                        Task<Uri> downloadUri = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                        Toast.makeText(HomeActivity.this, "Finished", Toast.LENGTH_SHORT).show();
                        Picasso.get().load(uri).fit().centerCrop().into(mImage);
                        DatabaseReference currentUserDB1 = mDataBase.child(mFirebaseAuth.getCurrentUser().getUid());
                        currentUserDB1.child("image").setValue(downloadUri.toString());
                    }).addOnFailureListener(HomeActivity.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mProgressDialog.dismiss();
                            Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }
}
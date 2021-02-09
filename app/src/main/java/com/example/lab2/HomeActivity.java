package com.example.lab2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lab2.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

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

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (mFirebaseAuth.getCurrentUser() == null)
                {
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    finish();
                }
                else {
                    mDataBase.child(mFirebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);
                            if (!user.image.equals("default") || TextUtils.isEmpty(user.image)) {
                                Picasso.with(HomeActivity.this).load(Uri.parse(user.image)).into(mImage);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        };
    }

    public void init() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.editEmail);
        firstname = findViewById(R.id.editFirstname);
        lastname = findViewById(R.id.editLastname);
        numberPhone = findViewById(R.id.editPhone);
        btnLogout = findViewById(R.id.logout);
        mStorage = FirebaseStorage.getInstance().getReference();
        mDataBase = FirebaseDatabase.getInstance().getReference().child("USERS");
        mImage = findViewById(R.id.imageAvatar);

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

    public void GoToHistory(View view){
        startActivity(new Intent(HomeActivity.this, HistoryActivity.class));
    }


}
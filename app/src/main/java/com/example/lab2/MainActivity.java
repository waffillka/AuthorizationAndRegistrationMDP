package com.example.lab2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lab2.Model.User;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private EditText emailId, password, firstname, lastname, numberPhone;
    private Button btnSignUp;
    private TextView tvSignIn;
    private FirebaseAuth mFirebaseAuth;

    private DatabaseReference mDataBase;
    private ProgressDialog mProgress;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    public void init(){
        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.editEmail);
        password = findViewById(R.id.editPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvSignIn = findViewById(R.id.tvSignIn);
        firstname = findViewById(R.id.editFirstname);
        lastname = findViewById(R.id.editLastname);
        numberPhone = findViewById(R.id.editPhone);
        mDataBase = FirebaseDatabase.getInstance().getReference();
        mProgress = new ProgressDialog(this);
    }

    public void SignUp(View view) {
        String email = emailId.getText().toString().trim();
        String pwd = password.getText().toString(). trim();
        String firstN = firstname.getText().toString().trim();
        String lastN = lastname.getText().toString().trim();
        String nPhone = numberPhone.getText().toString().trim();

        if (email.isEmpty()){
            Toast.makeText(MainActivity.this, "Please enter Email", Toast.LENGTH_SHORT).show();
            emailId.requestFocus();
        }
        else if (pwd.isEmpty()){
            Toast.makeText(MainActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();
            password.requestFocus();
        }
        else if (firstN.isEmpty()){
            Toast.makeText(MainActivity.this, "Please enter first name", Toast.LENGTH_SHORT).show();
            password.requestFocus();
        }
        else if (lastN.isEmpty()){
            Toast.makeText(MainActivity.this, "Please enter last name", Toast.LENGTH_SHORT).show();
            password.requestFocus();
        }
        else if (nPhone.isEmpty()){
            Toast.makeText(MainActivity.this, "Please enter number phone", Toast.LENGTH_SHORT).show();//----------------------------------------------------------
            password.requestFocus();
        }
        else if (email.isEmpty() && pwd.isEmpty() && firstN.isEmpty() && lastN.isEmpty() && nPhone.isEmpty()){/////////////-----bad code-------------
            Toast.makeText(MainActivity.this, "Fields are Empty!", Toast.LENGTH_SHORT).show();
        }
        else if (!(email.isEmpty() && pwd.isEmpty() && firstN.isEmpty() && lastN.isEmpty() && nPhone.isEmpty())) {
            mProgress.setMessage("Create new account, wait...");
            mProgress.show();
            mFirebaseAuth.createUserWithEmailAndPassword(email, pwd)
                    .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()){
                        Toast.makeText(MainActivity.this, "SignUp Unsuccessful\nPlease try again.", Toast.LENGTH_SHORT).show();
                        mProgress.dismiss();
                    }
                    else{
                        DatabaseReference currentUserDB = mDataBase.child("USERS").child(mFirebaseAuth.getCurrentUser().getUid());
                        User user = new User(email, nPhone, firstN, lastN);

                        currentUserDB.setValue(user);
                        currentUserDB.push();
                        /*currentUserDB.child("Email").setValue(email);
                        currentUserDB.child("Last_name").setValue(lastN);
                        currentUserDB.child("First_name").setValue(firstN);
                        currentUserDB.child("PhoneNumber").setValue(numberPhone);
                        currentUserDB.child("image").setValue("default");*/
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        mProgress.dismiss();
                    }
                }
            });
        }
        else{
            Toast.makeText(MainActivity.this, "Error Occurred!", Toast.LENGTH_SHORT).show();
        }
    }

    public void GoToSignIn(View view) {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    private Boolean passwordValidation(String str) //A-Z, a-z, 0-9, min count = 6
    {
        Boolean w = true;
        Boolean temp = false;
        str = str.trim();
        if (str.length() < 6) {
            Toast.makeText(MainActivity.this, "The password must consist of A-Z, a-z, 0-9, minimum length 6!", Toast.LENGTH_SHORT).show();
            w = false;
        }

        if (w) {
            for(int i = 0; i < str.length(); i++) {
                if (str.charAt(i) >= 'A' && str.charAt(i) <= 'Z')
                {
                    temp = true;
                }
            }

            if(!temp) {
                Toast.makeText(MainActivity.this, "The password must consist of A-Z, a-z, 0-9, minimum length 6!", Toast.LENGTH_SHORT).show();
                w = false;
            }
        }

        if (w) {
            temp = false;
            for(int i = 0; i < str.length(); i++) {
                if (str.charAt(i) >= 'a' && str.charAt(i) <= 'z')
                {
                    temp = true;
                }
            }

            if(!temp) {
                Toast.makeText(MainActivity.this, "The password must consist of A-Z, a-z, 0-9, minimum length 6!", Toast.LENGTH_SHORT).show();
                w = false;
            }
        }

        if (w) {
            temp = false;
            for(int i = 0; i < str.length(); i++) {
                if (str.charAt(i) >= '0' && str.charAt(i) <= '9')
                {
                    temp = true;
                }
            }

            if(!temp) {
                Toast.makeText(MainActivity.this, "The password must consist of A-Z, a-z, 0-9, minimum length 6!", Toast.LENGTH_SHORT).show();
                w = false;
            }
        }
        return w;
    }

    private Boolean mailValidation (String str) //
    {
        Boolean w = true;
        return false;
    }
}
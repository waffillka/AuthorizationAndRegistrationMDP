package com.example.lab2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private EditText emailId, password;
    private Button btnSignUp;
    private TextView tvSignIn;
    private FirebaseAuth mFirebaseAuth;

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
    }

    public void SignUp(View view) {
        String email = emailId.getText().toString();
        String pwd = password.getText().toString();

        if (email.isEmpty()){
            emailId.setText("Please enter Email");
            emailId.requestFocus();
        }
        else if (pwd.isEmpty()){
            password.setText("Please enter password");
            password.requestFocus();
        }
        else if (email.isEmpty() && pwd.isEmpty()){
            Toast.makeText(MainActivity.this, "Fields are Empty!", Toast.LENGTH_SHORT).show();
        }
        else if (!(email.isEmpty() && pwd.isEmpty()))
        {
            mFirebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()){
                        Toast.makeText(MainActivity.this, "SignUp Unsuccessful\nPlease try again.", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        GoToSignIn();
                    }
                }
            });
        }
        else{
            Toast.makeText(MainActivity.this, "Error Occurred!", Toast.LENGTH_SHORT).show();
        }
    }

    public void GoToSignIn() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

}
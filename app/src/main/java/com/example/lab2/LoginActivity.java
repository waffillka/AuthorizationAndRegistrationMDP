package com.example.lab2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lab2.Model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private EditText emailId, password;
    private Button btnSignIn;
    private TextView tvSignUp;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private ProgressDialog mProgress;
    private DatabaseReference mDataBase;

    //GOOGLE
    private GoogleSignInClient mGoogleSignInClient;
    //private ActivityGoogleBinding mBinding;
    private final static int RC_SIGN_IN = 123;
    private static final String TAG = "GoogleActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        // [START_EXCLUDE silent]

        // [END_EXCLUDE]
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();

                            DatabaseReference currentUserDB = mDataBase.child("USERS").child(mFirebaseAuth.getCurrentUser().getUid());
                            if (currentUserDB == null){
                                FirebaseUser m = mFirebaseAuth.getCurrentUser();
                                User user_ = new User(m.getEmail(), m.getPhoneNumber(), m.getDisplayName(), "empty", m.getPhotoUrl().toString());
                                Toast.makeText(LoginActivity.this, m.getDisplayName(), Toast.LENGTH_SHORT).show();
                                Log.d("firebase", m.getDisplayName());
                                currentUserDB.setValue(user_);
                                currentUserDB.push();
                            }
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        }
                        else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser != null) {
            Toast.makeText(LoginActivity.this, "You are logged in!", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(i);
        } else {
            Toast.makeText(LoginActivity.this, "Please Login", Toast.LENGTH_SHORT).show();
        }
    }

    public void init(){
        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.editEmail);
        password = findViewById(R.id.editPassword);
        btnSignIn = findViewById(R.id.btnSignUp);
        tvSignUp = findViewById(R.id.tvSignUp);
        mDataBase = FirebaseDatabase.getInstance().getReference();
        mProgress = new ProgressDialog(this);
    }

    public void SignIn(View view) {
        String email = emailId.getText().toString();
        String pwd = password.getText().toString();

        if (email.isEmpty()){
            Toast.makeText(LoginActivity.this, "Please enter Email!", Toast.LENGTH_SHORT).show();
            emailId.requestFocus();
        }
        else if (pwd.isEmpty()){
            Toast.makeText(LoginActivity.this, "Please enter Password!", Toast.LENGTH_SHORT).show();
            password.requestFocus();
        }
        else if (email.isEmpty() && pwd.isEmpty()){
            Toast.makeText(LoginActivity.this, "Fields are Empty!", Toast.LENGTH_SHORT).show();
        }
        else if (!(email.isEmpty() && pwd.isEmpty()))
        {
            mProgress.setMessage("Wait, authorization in progress...");
            mProgress.show();
            mFirebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        mProgress.dismiss();
                        Toast.makeText(LoginActivity.this, "Login Error!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        mProgress.dismiss();
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    }
                }
            });
        }
        else{
            mProgress.dismiss();
            Toast.makeText(LoginActivity.this, "Error Occurred!", Toast.LENGTH_SHORT).show();
        }
    }

    public void GoogleSignIn(View view){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void GoToMain(View view)
    {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
    }
}
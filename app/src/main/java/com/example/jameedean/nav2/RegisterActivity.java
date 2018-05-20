package com.example.jameedean.nav2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jameedean.nav2.data.Reference;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class RegisterActivity extends AppCompatActivity{

    private Button BtnSignIn;
    private EditText txtEmailAddress;
    private EditText txtPassword;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        // Check user, if not exists then go login
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //redirect to main page
        BtnSignIn = (Button) findViewById(R.id.sign_in_button);
        txtEmailAddress = (EditText) findViewById(R.id.txtEmailRegistration);
        txtPassword = (EditText) findViewById(R.id.txtPasswordRegistration);
        firebaseAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    // NOTE: this Activity should get onpen only when the user is not signed in, otherwise
                    // the user will receive another verification email.
                    sendEmailVerification();
                } else {
                    // User is signed out

                }
                // ...
            }
        };

    }
@Override
protected void onStart(){
        super.onStart();
}

@Override
protected void onStop(){
    super.onStop();
}

@Override
protected void onDestroy(){
    super.onDestroy();
}

    public void btnRegistrationUser_Click(View v) {

        final ProgressDialog progressDialog = ProgressDialog.show(RegisterActivity.this, "Please wait...", "Processing...", true);
        (firebaseAuth.createUserWithEmailAndPassword(txtEmailAddress.getText().toString(), txtPassword.getText().toString()))
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            sendEmailVerification();
                            Toast.makeText(RegisterActivity.this, "Registration successful. Verification Email Send.", Toast.LENGTH_LONG).show();

                        }
                        else
                        {
                            Log.e("ERROR", task.getException().toString());
                            Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void sendEmailVerification(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // email sent

                        Toast.makeText(RegisterActivity.this,"Check your email to verify.",Toast.LENGTH_LONG).show();
                        // after email is sent just logout the user and finish this activity
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    }
                    else
                    {
                        // email not sent, so display message and restart the activity or do whatever you wish to do

                        //restart this activity
                        overridePendingTransition(0, 0);
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());

                    }
                }
            });

    }

    public void btnSignIn_Click(View view) {
        Intent i = new Intent(RegisterActivity.this, SignIn_Activity.class);
        startActivity(i);
    }
}

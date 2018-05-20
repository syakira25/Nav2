package com.example.jameedean.nav2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.jameedean.nav2.data.Reference;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignIn_Activity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private Button btnLogin, btnReset, btnSignUp;
    private FirebaseAuth firebaseAuth;

    // firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        mAuth = FirebaseAuth.getInstance();
        // Check user, if not exists then go login
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //redirect to main page
        if(currentUser != null) {
            startActivity(new Intent(SignIn_Activity.this, NavDrawerActivity.class));
            finish();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);

        firebaseAuth = FirebaseAuth.getInstance();

        btnLogin = (Button) findViewById(R.id.btn_login);
        btnReset = (Button) findViewById(R.id.btn_reset_password);
        btnSignUp = (Button) findViewById(R.id.btnRegistration);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignIn_Activity.this,RegisterActivity.class));
            }
        });


        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignIn_Activity.this, ResetPasswordActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                final ProgressDialog progressDialog = ProgressDialog.show(SignIn_Activity.this, "Please wait...", "Proccessing...", true);
                //authenticate user
                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignIn_Activity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                progressDialog.dismiss();
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        inputPassword.setError("Minimum length password 6");
                                    } else {
                                        Log.e("ERROR", task.getException().toString());

                                        Toast.makeText(SignIn_Activity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                   /* Toast.makeText(SignIn_Activity.this, "Login successful", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(SignIn_Activity.this, NavDrawerActivity.class);
                                    intent.putExtra("Email", firebaseAuth.getCurrentUser().getEmail());
                                    startActivity(intent);
                                    finish();*/
                                    checkIfEmailVerified();
                                }
                            }
                        });
            }
        });
    }
    private void checkIfEmailVerified()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user.isEmailVerified())
        {
            // user is verified, so you can finish this activity or send user to activity which you want.
            finish();
            Toast.makeText(SignIn_Activity.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SignIn_Activity.this,NavDrawerActivity.class);
            startActivity(intent);
        }
        else
        {
            // email is not verified, so just prompt the message to the user and restart this activity.
            // NOTE: don't forget to log out the user.
            Toast.makeText(SignIn_Activity.this, "Please verify your email first.", Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();

            //restart this activity

        }
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

}

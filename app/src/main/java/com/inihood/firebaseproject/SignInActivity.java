package com.inihood.firebaseproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.inihood.firebaseproject.font.RobotoTextView;
import com.inihood.firebaseproject.view.FloatLabeledEditText;

public class SignInActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;
    private static final String TAG = "SignInActivity";
    FirebaseAuth mAuth;
   private TextView welcometxt;
   private FloatLabeledEditText email, password;
   private RobotoTextView regBtn, Login;
   private ProgressDialog progressDialog;
   private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();
        initWidgets();
        initGoogleSignIn();

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterWithEmailAndPassword();
            }
        });

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

    }

    private void initGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void login() {
        //get text from input
        String email_in = email.getText().toString();
        String password_in = password.getText().toString();
        // set progressbar
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Logging in..");
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email_in, password_in).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    progressDialog.hide();
                    nextActivity();
                    Toast.makeText(SignInActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                }else {
                    progressDialog.hide();
                    Toast.makeText(SignInActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initWidgets() {
        welcometxt = findViewById(R.id.textView);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        regBtn = findViewById(R.id.register);
        Login = findViewById(R.id.login);
        progressDialog = new ProgressDialog(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // check if user already exist
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            // user exist
            Intent intent = new Intent(this, AccountActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void RegisterWithEmailAndPassword() {
        //get text from input
        String email_in = email.getText().toString();
        String password_in = password.getText().toString();
        // set progressbar
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Registering in..");
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email_in, password_in).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    progressDialog.hide();
                    nextActivity();
                    Toast.makeText(SignInActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                }else {
                    progressDialog.hide();
                    Toast.makeText(SignInActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void nextActivity(){
        Intent intent = new Intent(this, SetupActivity.class);
        intent.putExtra("check", "setup");
        startActivity(intent);
        finish();
    }

    public void googlesignin(View view) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        //Now using firebase we are signing in the user here
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(SignInActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                            nextActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    public void signinwithphone(View view) {
        Intent intent = new Intent(this, InputPhoneActivity.class);
        startActivity(intent);
    }
}








package com.inihood.firebaseproject;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {

   FirebaseAuth mAuth;
   private TextView welcometxt;
   private EditText email, password;
   private Button regBtn, Login;
   private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();
        initWidgets();

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

    private void login() {
        //get text from input
        String email_in = email.getText().toString();
        String password_in = password.getText().toString();
        // set progressbar
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Logging in..");
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email_in, password_in).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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
            nextActivity();
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
        Intent intent = new Intent(this, AccountActivity.class);
        startActivity(intent);
        finish();
    }
}








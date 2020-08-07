package com.inihood.firebaseproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.TaskExecutor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerificationCodeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText input;
    private Button startSignIn;
    private ProgressDialog progressDialog;
    private String mVerificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_code);

        mAuth = FirebaseAuth.getInstance();
        input = findViewById(R.id.code_input);
        startSignIn = findViewById(R.id.sign_in);
        progressDialog = new ProgressDialog(this);
        startSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = input.getText().toString();
                if (number != null){
                    VerifyCode(number);
                }
            }
        });

        //get intent from other activity
        String number = getIntent().getStringExtra("num");
        sendVerificationCode(number);
    }

    private void sendVerificationCode(String number) {
        Toast.makeText(this, "Sending code...", Toast.LENGTH_SHORT).show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+234" + number, 60, TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD, mCallbacks);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            // sometimes the code is not detected automatically
            if (code != null){
                input.setText(code);
                VerifyCode(code);
            }else {
                Toast.makeText(VerificationCodeActivity.this, "code is null", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(VerificationCodeActivity.this, "Fail to send code " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            mVerificationId = s;
            Toast.makeText(VerificationCodeActivity.this, "Code sent "
                    + mVerificationId, Toast.LENGTH_SHORT).show();
        }
    };

    private void VerifyCode(String code) {
        if (code != null){
            if (mVerificationId != null){
                progressDialog.setTitle("Working..");
                progressDialog.setMessage("verifying...");
                progressDialog.show();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                signWithCredentials(credential);
            }else {
                Toast.makeText(this, "verification code ID is null", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "code is null", Toast.LENGTH_SHORT).show();
        }
    }

    private void signWithCredentials(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                Intent intent = new Intent(VerificationCodeActivity.this, SetupActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("check", "setup");
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(VerificationCodeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
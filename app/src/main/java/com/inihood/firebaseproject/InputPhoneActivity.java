package com.inihood.firebaseproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

public class InputPhoneActivity extends AppCompatActivity {

    private EditText phone;
    private Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_phone);

        phone = findViewById(R.id.code_input);
        next = findViewById(R.id.sign_in);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPhoneNumber();
            }
        });

    }

    private void getPhoneNumber() {
        String number = phone.getText().toString().trim();
        if (number != null){
            Intent intent = new Intent(this, VerificationCodeActivity.class);
            intent.putExtra("num", number);
            startActivity(intent);
        }
    }
}
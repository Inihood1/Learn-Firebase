package com.inihood.firebaseproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SetupActivity extends AppCompatActivity {

    private EditText name, age, bio;
    private Button save;
    private FirebaseFirestore firebaseFirestore;
    private ProgressDialog progressDialog;
    private String getDocId;
    private boolean isEditing = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        init();
        firebaseFirestore = FirebaseFirestore.getInstance();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if (isEditing){
                   String name_string = name.getText().toString();
                   String age_string = age.getText().toString();
                   String bio_String = bio.getText().toString();

                   edit(name_string, age_string, bio_String);
               }else{
                   String name_string = name.getText().toString();
                   String age_string = age.getText().toString();
                   String bio_String = bio.getText().toString();

                   startWorking(name_string, age_string, bio_String);
               }
            }
        });

    }
    // edit profile method
    private void edit(String name_string, String age_string, String bio_string) {
        progressDialog.setTitle("Just a moment");
        progressDialog.setMessage("Setting up account...");
        progressDialog.show();

        Map<String, Object> map = new HashMap<>();
        map.put("name", name_string);
        map.put("age", age_string);
        map.put("biography", bio_string);

        firebaseFirestore.collection("Users").document(getDocId).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    progressDialog.dismiss();
                    Intent intent = new Intent(SetupActivity.this, AccountActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(SetupActivity.this, "error " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    // setup profile method
    private void startWorking(String name_string, String age_string, String bio_string) {
        progressDialog.setTitle("Just a moment");
        progressDialog.setMessage("Setting up account...");
        progressDialog.show();

        Map<String, Object> map = new HashMap<>();
        map.put("name", name_string);
        map.put("age", age_string);
        map.put("biography", bio_string);

        firebaseFirestore.collection("Users").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                // fetch the generated id
                String doc = documentReference.getId();
                progressDialog.dismiss();
                Intent intent = new Intent(SetupActivity.this, AccountActivity.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(SetupActivity.this, "Something went wrong " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init(){
        name = findViewById(R.id.name_in);
        age = findViewById(R.id.age_in);
        bio = findViewById(R.id.about_in);
        save = findViewById(R.id.save_btn);
        progressDialog = new ProgressDialog(this);

        String incoming = getIntent().getStringExtra("check");
        getDocId = getIntent().getStringExtra("docID");
        if (incoming.equals("setup")){
            save.setText("Setup Account");
            isEditing = false;
        }else{
            isEditing = true;
            save.setText("Edit Account Info");
        }
    }
}
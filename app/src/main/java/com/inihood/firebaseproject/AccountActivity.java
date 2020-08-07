package com.inihood.firebaseproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AccountActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private TextView name, age, bio;
    private ImageView profilePic;
    private FirebaseFirestore firebaseFirestore;
    private String documentId;
    private Uri imageUri;
    private ProgressDialog progressDialog;
    private StorageReference storageReference;
    private FirebaseStorage storage;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        mAuth = FirebaseAuth.getInstance();
        init();
        firebaseFirestore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);
        currentUserId = mAuth.getCurrentUser().getUid();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference().child(currentUserId);
        getDatat();
        Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show();
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
            }
        });
    }

    private void checkPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                openGallery();
        }else{
            requestPermission();
        }
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]
                {Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
    }

    private void openGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, 200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 200){
            imageUri = data.getData();
          //  profilePic.setImageURI(imageUri);
            uploadToStorage(imageUri);
        }
    }

    private void uploadToStorage(Uri imageUri) {
        progressDialog.setTitle("Uploading..");
        progressDialog.setMessage("please wait..");
        progressDialog.show();

        final StorageReference reference = storageReference.child("profile.jpg");
        UploadTask uploadTask = reference.putFile(imageUri);
        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()){
                    throw task.getException();

                }
                return reference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    progressDialog.hide();
                    Uri downloadUri = task.getResult();
                    updateProfilePic(downloadUri);
                }
            }
        });

    }

    private void updateProfilePic(Uri uri){
        Toast.makeText(this, "Updating...", Toast.LENGTH_LONG).show();
        Map<String, Object> map = new HashMap<>();
        map.put("profile_pic", uri.toString());
        firebaseFirestore.collection("Users").document(documentId).update(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(AccountActivity.this, "Done", Toast.LENGTH_SHORT).show();
                        getDatat();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
       if(requestCode == 100){
           if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
               openGallery();
           }else {
               Toast.makeText(this, "The permission was denied", Toast.LENGTH_SHORT).show();
           }
       }
    }

    private void getDatat() {
        firebaseFirestore.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                //check if the task was successful
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot documentSnapshot: task.getResult()){
                        documentId = documentSnapshot.getId();
                        String name = documentSnapshot.getString("name");
                        String age = documentSnapshot.getString("age");
                        String bio = documentSnapshot.getString("biography");
                        String image = documentSnapshot.getString("profile_pic");

                        setFields(name, age, bio, image);
                    }
                }
            }
        });
    }

    private void setFields(String name_, String age_, String bio_, String image) {
        name.setText(name_);
        age.setText(age_);
        bio.setText(bio_);
        Picasso.get()
                .load(image)
                .resize(200, 200)
                .centerCrop()
                .into(profilePic);
    }

    private void init(){
        name = findViewById(R.id.name_display);
        age = findViewById(R.id.age_display);
        bio = findViewById(R.id.bio_display);
        profilePic = findViewById(R.id.profile_pic);
    }

    public void logout(View view) {
        mAuth.signOut();
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }

    public void editInfo(View view) {
        Intent intent = new Intent(this, SetupActivity.class);
        intent.putExtra("check", "edit");
        intent.putExtra("docID", documentId);
        startActivity(intent);
    }
}
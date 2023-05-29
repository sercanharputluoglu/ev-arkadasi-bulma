package com.example.evarkadasibulma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ViewProfileFromAllProfiles extends AppCompatActivity {

    TextView viewName, viewSurname, viewDistanceToCampus, viewDepartment, viewGrade,
            viewStatus, viewPhoneNumber, viewEmail, viewDuration;


    FirebaseFirestore firebaseFirestore;

    String userID;

    ImageView profileImage;

    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile_from_all_profiles);

        userID = getIntent().getStringExtra("id");

        firebaseFirestore = FirebaseFirestore.getInstance();

        profileImage = findViewById(R.id.profile_image);

        storageReference = FirebaseStorage.getInstance().getReference();

        StorageReference profileRef = storageReference.child("users/"+userID+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed To Load Image.", Toast.LENGTH_SHORT).show();
            }
        });

        viewName = findViewById(R.id.name);
        viewSurname = findViewById(R.id.surname);
        viewDepartment = findViewById(R.id.department);
        viewGrade = findViewById(R.id.grade);
        viewStatus = findViewById(R.id.status);
        viewDistanceToCampus = findViewById(R.id.distance_to_campus);
        viewEmail = findViewById(R.id.email);
        viewPhoneNumber = findViewById(R.id.phone_number);
        viewDuration = findViewById(R.id.duration);

        DocumentReference docRef = firebaseFirestore.collection("users").document(userID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserModel userModel = documentSnapshot.toObject(UserModel.class);
                viewName.setText(userModel.getName());
                viewSurname.setText(userModel.getSurname());
                viewDepartment.setText(userModel.getDepartment());
                viewGrade.setText(userModel.getGrade());
                viewDistanceToCampus.setText(String.valueOf(userModel.getDistanceToCampus()));
                viewStatus.setText(String.valueOf(userModel.getStatus()));
                viewEmail.setText(userModel.getEmail());
                viewPhoneNumber.setText(userModel.getPhoneNumber());
                viewDuration.setText(userModel.getDuration());
            }
        });

    }
}
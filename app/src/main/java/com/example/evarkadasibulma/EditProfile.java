package com.example.evarkadasibulma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditProfile extends AppCompatActivity {

    public static final int ALL_PERMS_CODE = 104;

    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;

    RadioGroup radioGroup;
    RadioButton radioButtonAccommodation, radioButtonRoommate, radioButtonNotLooking;

    EditText editName, editSurname, editDepartment, editGrade, editDistanceToCampus,
            editPhoneNumber, editEmail, editDuration;

    ImageView profileImage;
    Button saveButton, takePictureButton;
    FirebaseAuth mAuth;
    FirebaseFirestore firebaseFirestore;
    StorageReference storageReference;

    String currentPhotoPath;

    String userID;

    StudentStatus studentStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        radioGroup = findViewById(R.id.radio_group_status);
        radioButtonAccommodation = findViewById(R.id.radio_looking_for_accommodation);
        radioButtonRoommate = findViewById(R.id.radio_looking_for_roommate);
        radioButtonNotLooking = findViewById(R.id.radio_not_looking);

        editName = findViewById(R.id.edit_name);
        editSurname = findViewById(R.id.edit_surname);
        editDepartment = findViewById(R.id.edit_department);
        editGrade = findViewById(R.id.edit_grade);
        editDistanceToCampus = findViewById(R.id.edit_distance_to_campus);
        editPhoneNumber = findViewById(R.id.edit_phone_number);
        editEmail = findViewById(R.id.edit_email);
        editDuration = findViewById(R.id.edit_duration);

        saveButton = findViewById(R.id.save);
        takePictureButton = findViewById(R.id.take_picture_from_camera);
        profileImage = findViewById(R.id.profile_image);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        userID = mAuth.getCurrentUser().getUid();

        StorageReference profileRef = storageReference.child("users/"+userID+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });

        DocumentReference docRef = firebaseFirestore.collection("users").document(userID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserModel userModel = documentSnapshot.toObject(UserModel.class);
                editName.setText(userModel.getName());
                editSurname.setText(userModel.getSurname());
                editDepartment.setText(userModel.getDepartment());
                editGrade.setText(userModel.getGrade());
                editDistanceToCampus.setText(String.valueOf(userModel.getDistanceToCampus()));
                editEmail.setText(userModel.getEmail());
                editPhoneNumber.setText(userModel.getPhoneNumber());
                editDuration.setText(userModel.getDuration());

                studentStatus = userModel.getStatus();
                if(studentStatus == StudentStatus.LOOKING_FOR_ACCOMMODATION){
                    radioButtonAccommodation.setChecked(true);
                } else if (studentStatus == StudentStatus.LOOKING_FOR_ROOMMATE) {
                    radioButtonRoommate.setChecked(true);

                } else if (studentStatus == StudentStatus.NOT_LOOKING) {
                    radioButtonNotLooking.setChecked(true);
                }
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent,1000);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editName.getText().toString().isEmpty() || editSurname.getText().toString().isEmpty()
                        || editDepartment.getText().toString().isEmpty() || editGrade.getText().toString().isEmpty()
                || editDistanceToCampus.getText().toString().isEmpty() || editEmail.getText().toString().isEmpty()
                || editPhoneNumber.getText().toString().isEmpty()){
                    Toast.makeText(EditProfile.this, "One or Many fields are empty.", Toast.LENGTH_SHORT).show();
                    System.out.println("hata");
                    return;
                }

                Map<String,Object> editedUserDetails = new HashMap<>();
                editedUserDetails.put("name", editName.getText().toString());
                editedUserDetails.put("surname", editSurname.getText().toString());
                editedUserDetails.put("department", editDepartment.getText().toString());
                editedUserDetails.put("grade", editGrade.getText().toString());
                editedUserDetails.put("distanceToCampus", Double.valueOf(editDistanceToCampus.getText().toString()));
                editedUserDetails.put("phoneNumber", editPhoneNumber.getText().toString());
                editedUserDetails.put("email", editEmail.getText().toString());
                editedUserDetails.put("duration", editDuration.getText().toString());

                if (radioButtonAccommodation.isChecked()){
                    studentStatus = StudentStatus.LOOKING_FOR_ACCOMMODATION;
                } else if (radioButtonRoommate.isChecked()) {
                    studentStatus = StudentStatus.LOOKING_FOR_ROOMMATE;

                } else if (radioButtonNotLooking.isChecked()) {
                    studentStatus = StudentStatus.NOT_LOOKING;
                }

                editedUserDetails.put("status", studentStatus);


                docRef.update(editedUserDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditProfile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),ProfilePage.class);
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfile.this,   e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askCameraPermissions();
            }
        });
    }

//    private void askCameraPermissions() {
//        if ((ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) && ((ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED))) {
//
//            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERM_CODE);
//        }
//        else {
//            dispatchTakePictureIntent();
//        }
//    }
//

        private void askCameraPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }else {
            dispatchTakePictureIntent();
        }

        }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Camera Permission is Required to Use camera.", Toast.LENGTH_SHORT).show();
            }
        }
    }

//        private void askCameraPermissions() {
//        List<String> listPermissionsNeeded = new ArrayList<>();
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            listPermissionsNeeded.add(Manifest.permission.CAMERA);
//            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
//        }
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
//            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, DISK_ACCESS_CODE);
//        }
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
//            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
//        }
//        if(!listPermissionsNeeded.isEmpty()){
//            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), ALL_PERMS_CODE);
//        } else {
//            dispatchTakePictureIntent();
//        }
//    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        Map<String, Integer> perms = new HashMap<>();
//        perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
//        perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
//        perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
//
//        switch (requestCode) {
//            case ALL_PERMS_CODE:
//                if (grantResults.length > 0) {
//                    for (int i = 0; i < permissions.length; i++) {
//                        perms.put(permissions[i], grantResults[i]);
//                    }
//                    if(perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
//                            perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
//                            perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//
//                        dispatchTakePictureIntent();
//                    }
//                }
//                break;
//            default:
//                Toast.makeText(this, "Some permissions are not available", Toast.LENGTH_SHORT).show();
//                break;
//        }
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp +"."+getFileExt(imageUri);

                //profileImage.setImageURI(imageUri);
                uploadImageToFirebase(imageFileName,imageUri);

//                uploadImageToFirebase(imageUri);


            }
        }

        if(requestCode == CAMERA_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                File f = new File(currentPhotoPath);
                profileImage.setImageURI(Uri.fromFile(f));


                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);

                uploadImageToFirebase(f.getName(),contentUri);



            }

        }

    }

    private String getFileExt(Uri contentUri) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//        String storageDir = this.getExternalFilesDir(null).getAbsolutePath();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent

        // Create the File where the photo should go
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {

        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this,
                    "sercan1",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
        }

    }



    private void uploadImageToFirebase(String name, Uri contentUri) {
        final StorageReference image = storageReference.child("users/"+mAuth.getCurrentUser().getUid()+"/profile.jpg");
        image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImage);

                    }
                });

                Toast.makeText(getApplicationContext(), "Image Is Uploaded.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Upload Failled.", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
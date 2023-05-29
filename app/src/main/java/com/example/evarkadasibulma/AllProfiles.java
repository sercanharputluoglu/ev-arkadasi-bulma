package com.example.evarkadasibulma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;

public class AllProfiles extends AppCompatActivity implements RecyclerViewInterface{

    ArrayList<UserModel> profileList = new ArrayList<>();
    FirebaseFirestore db;

    Button distanceFilterButton;

    AllProfiles_RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_profiles);
        distanceFilterButton = findViewById(R.id.distance_filter_button);
        db = FirebaseFirestore.getInstance();

        RecyclerView recyclerView = findViewById(R.id.allProfilesRecyclerView);

        setUpProfileList(recyclerView);

        distanceFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText minDistanceEditText = new EditText(view.getContext());
                EditText maxDistanceEditText = new EditText(view.getContext());
                minDistanceEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

                final AlertDialog.Builder distanceFilter = new AlertDialog.Builder(view.getContext());

                LinearLayout linearLayout = new LinearLayout(view.getContext());
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setPadding(50, 50, 50, 50);
                linearLayout.addView(minDistanceEditText);
                linearLayout.addView(maxDistanceEditText);

                distanceFilter.setTitle("Distance Filter");
                distanceFilter.setMessage("Enter Min And Max Distance");
                distanceFilter.setView(linearLayout);


                distanceFilter.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Double min = Double.valueOf(minDistanceEditText.getText().toString());
                        Double max = Double.valueOf(maxDistanceEditText.getText().toString());
                        profileList.clear();


                        db.collection("users")
                                .whereGreaterThanOrEqualTo("distanceToCampus", min)
                                .whereLessThanOrEqualTo("distanceToCampus", max)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {

                                                profileList.add(document.toObject(UserModel.class));

                                                System.out.println(profileList.get(0).toString());

                                            }

                                            adapter.notifyDataSetChanged();
                                        }
                                        else {

                                        }
                                    }
                                });



                    }
                });

                distanceFilter.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                distanceFilter.create().show();

            }
        });

    }

    private void setUpProfileList(RecyclerView recyclerView){

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                profileList.add(document.toObject(UserModel.class));

                                System.out.println(profileList.get(0).toString());
                            }

                            adapter = new AllProfiles_RecyclerViewAdapter(AllProfiles.this, AllProfiles.this, profileList);

                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(AllProfiles.this));


                        } else {
                            System.out.println("data eklenmedi");
                        }
                    }
                });
    }

    @Override
    public void onItemClick(int position) {

        Intent intent = new Intent(getApplicationContext(), ViewProfileFromAllProfiles.class);
        intent.putExtra("id", profileList.get(position).getId());
        startActivity(intent);

    }

}
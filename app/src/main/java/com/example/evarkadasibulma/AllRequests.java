package com.example.evarkadasibulma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AllRequests extends AppCompatActivity {

    ArrayList<MatchRequestModel> requestList = new ArrayList<>();

    FirebaseFirestore db;

    AllRequests_RecyclerViewAdapter adapter;


    FirebaseAuth mAuth;

    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_requests);

        mAuth = FirebaseAuth.getInstance();

        userID = mAuth.getCurrentUser().getUid();


        db = FirebaseFirestore.getInstance();

        RecyclerView recyclerView = findViewById(R.id.allRequestsRecyclerView);

        setUpProfileList(recyclerView);
    }


    private void setUpProfileList(RecyclerView recyclerView){

        db.collection("matchRequests")
                .whereEqualTo("approvalStatus", "Pending Approval")
                .whereEqualTo("receiverID", userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                requestList.add(document.toObject(MatchRequestModel.class));
                            }

                            adapter = new AllRequests_RecyclerViewAdapter(requestList, AllRequests.this);
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(AllRequests.this));
                        }
                        else {
                            System.out.println("data eklenmedi");
                        }
                    }
                });
    }
}
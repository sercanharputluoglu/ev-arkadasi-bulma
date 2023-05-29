package com.example.evarkadasibulma;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class AllProfiles_RecyclerViewAdapter extends RecyclerView.Adapter<AllProfiles_RecyclerViewAdapter.MyViewHolder>{

    private final RecyclerViewInterface recyclerViewInterface;

    Context context;
    ArrayList<UserModel> profileList;

    StorageReference storageReference;

    FirebaseFirestore db;

    FirebaseAuth mAuth;

    UserModel receiverUserModel, senderUserModel;
    MatchRequestModel matchRequestModel;



    public AllProfiles_RecyclerViewAdapter(RecyclerViewInterface recyclerViewInterface, Context context, ArrayList<UserModel> profileList) {
        this.recyclerViewInterface = recyclerViewInterface;
        this.context = context;
        this.profileList = profileList;
    }

    @NonNull
    @Override
    public AllProfiles_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_row, parent, false);


        return new AllProfiles_RecyclerViewAdapter.MyViewHolder(view , recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull AllProfiles_RecyclerViewAdapter.MyViewHolder holder, int position) {


        holder.viewName.setText(profileList.get(position).getName());
        holder.viewSurname.setText(profileList.get(position).getSurname());
        holder.viewDepartment.setText(profileList.get(position).getDepartment());
        holder.viewGrade.setText(profileList.get(position).getGrade());

        holder.viewDistanceToCampus.setText(String.valueOf(profileList.get(position).getDistanceToCampus()));
        holder.viewStatus.setText(String.valueOf(profileList.get(position).getStatus()));
        holder.viewEmail.setText(profileList.get(position).getEmail());
        holder.viewPhoneNumber.setText(profileList.get(position).getPhoneNumber());
        // image ekle
        storageReference = FirebaseStorage.getInstance().getReference();

        StorageReference profileRef = storageReference.child("users/"+profileList.get(position).getId()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(holder.profileImageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(getApplicationContext(), "Failed To Load Image.", Toast.LENGTH_SHORT).show();
                System.out.println("FOTO YUKLENMEDI");
            }
        });

        holder.viewEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
                intent.putExtra(Intent.EXTRA_SUBJECT, "Subject of email");
                intent.putExtra(Intent.EXTRA_TEXT, "Body of email");
                intent.setData(Uri.parse("mailto:" + holder.viewEmail.getText())); // or just "mailto:" for blank
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
                context.startActivity(intent);


            }
        });

        db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();

        String senderUserID = mAuth.getCurrentUser().getUid();

        String receiverUserID = profileList.get(holder.getAdapterPosition()).getId();


        holder.sendMatchRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CompletableFuture<Void> receiverFuture = new CompletableFuture<>();
                CompletableFuture<Void> senderFuture = new CompletableFuture<>();

                DocumentReference receiverDocRef = db.collection("users").document(receiverUserID);
                receiverDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        receiverUserModel = documentSnapshot.toObject(UserModel.class);
                        receiverFuture.complete(null);
                    }
                });

                DocumentReference senderDocRef = db.collection("users").document(senderUserID);
                senderDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        senderUserModel = documentSnapshot.toObject(UserModel.class);
                        senderFuture.complete(null);
                    }
                });


                CompletableFuture.allOf(receiverFuture, senderFuture).thenRun(() -> {
                    if (receiverUserModel != null && senderUserModel != null) {
                        if (receiverUserModel.getStatus() != null && senderUserModel.getStatus() != null) {
                            if (receiverUserModel.getStatus() != senderUserModel.getStatus()
                                    && receiverUserModel.getStatus() != StudentStatus.NOT_LOOKING
                                    && senderUserModel.getStatus() != StudentStatus.NOT_LOOKING) {

                                    DocumentReference documentReference = db.collection("matchRequests").document(receiverUserID);
                                    matchRequestModel = new MatchRequestModel(senderUserID, receiverUserID, "Pending Approval", senderUserModel.getName(), senderUserModel.getSurname(), senderUserModel.getEmail(), senderUserModel.getPhoneNumber());

                                    documentReference.set(matchRequestModel);

                                    Toast.makeText(context, "Request Sent",
                                            Toast.LENGTH_SHORT).show();

                            }
                            else {
                                Toast.makeText(context, "Request Failed To Sent",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return profileList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView profileImageView;
        TextView viewName, viewSurname, viewDistanceToCampus, viewDepartment, viewGrade,
                viewStatus, viewPhoneNumber, viewEmail;

        Button sendMatchRequestButton;


        public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.imageView);
            viewName = itemView.findViewById(R.id.all_profiles_name);
            viewSurname = itemView.findViewById(R.id.all_profiles_surname);
            viewDepartment = itemView.findViewById(R.id.all_profiles_department);
            viewGrade = itemView.findViewById(R.id.all_profiles_grade);
            viewStatus = itemView.findViewById(R.id.all_profiles_status);
            viewDistanceToCampus = itemView.findViewById(R.id.all_profiles_distance_to_campus);
            viewEmail = itemView.findViewById(R.id.all_profiles_email);
            viewPhoneNumber = itemView.findViewById(R.id.all_profiles_phone_number);
            sendMatchRequestButton = itemView.findViewById(R.id.send_match_request);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recyclerViewInterface != null){
                        int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemClick(pos);

                        }
                    }
                }
            });


        }

    }

}

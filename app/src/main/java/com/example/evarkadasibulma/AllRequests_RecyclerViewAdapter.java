package com.example.evarkadasibulma;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class AllRequests_RecyclerViewAdapter extends RecyclerView.Adapter<AllRequests_RecyclerViewAdapter.MyViewHolder> {

    ArrayList<MatchRequestModel> requestList;
    Context context;


    FirebaseFirestore db;

    UserModel receiverUserModel, senderUserModel;

    MatchRequestModel matchRequestModel;


    public AllRequests_RecyclerViewAdapter(ArrayList<MatchRequestModel> requestList, Context context) {
        this.requestList = requestList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.all_requests_recycler_view_row, parent, false);


        return new AllRequests_RecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.viewSenderName.setText(requestList.get(position).getSenderName());
        holder.viewSenderSurname.setText(requestList.get(position).getSenderSurname());

        holder.viewStatus.setText(requestList.get(position).getApprovalStatus());
        holder.viewSenderEmail.setText(requestList.get(position).getSenderEmail());
        holder.viewSenderPhoneNumber.setText(requestList.get(position).getSenderPhoneNumber());

        holder.viewSenderEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
                intent.putExtra(Intent.EXTRA_SUBJECT, "Subject of email");
                intent.putExtra(Intent.EXTRA_TEXT, "Body of email");
                intent.setData(Uri.parse("mailto:" + holder.viewSenderEmail.getText())); // or just "mailto:" for blank
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
                context.startActivity(intent);
            }
        });

        holder.viewSenderPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = holder.viewSenderPhoneNumber.getText().toString();
                try {
                    // Format the phone number by removing any non-numeric characters
                    phoneNumber = phoneNumber.replaceAll("\\D", "");

                    // Create an intent with the ACTION_SENDTO action and the WhatsApp URI
                    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phoneNumber));

                    // Set the message content
                    intent.putExtra("sms_body", "Merhaba");

                    // Set the package name to WhatsApp
                    intent.setPackage("com.whatsapp");

                    // Start the intent
                    context.startActivity(intent);
                } catch (Exception e) {
                    // Handle any exceptions, e.g., WhatsApp is not installed on the device
                    e.printStackTrace();
                }

            }
        });

        db = FirebaseFirestore.getInstance();

        String senderUserID = requestList.get(holder.getAdapterPosition()).getSenderID();

        String receiverUserID = requestList.get(holder.getAdapterPosition()).getReceiverID();

        holder.acceptRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Are You Sure To Accept The Request");
                builder.setMessage("Click Yes To Accept");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        CompletableFuture<Void> receiverFuture = new CompletableFuture<>();
                        CompletableFuture<Void> senderFuture = new CompletableFuture<>();
                        CompletableFuture<Void> matchRequestFuture = new CompletableFuture<>();

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

                        Query matchRequestDocRef = db.collection("matchRequests")
                                .whereEqualTo("approvalStatus", "Pending Approval")
                                .whereEqualTo("receiverID", receiverUserID);

                        matchRequestDocRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    // Get the document data and update it
                                    matchRequestModel = documentSnapshot.toObject(MatchRequestModel.class);
                                    String documentId = documentSnapshot.getId();
                                    DocumentReference documentRef = db.collection("matchRequests").document(documentId);
                                    matchRequestModel.setApprovalStatus("Accepted");

                                    Map<String, Object> matchRequestMap = new HashMap<>();
                                    matchRequestMap.put("approvalStatus", "Accepted");
                                    matchRequestMap.put("receiverID", receiverUserID);
                                    matchRequestMap.put("senderEmail", matchRequestModel.getSenderEmail());
                                    matchRequestMap.put("senderID", senderUserID);
                                    matchRequestMap.put("senderName", matchRequestModel.getSenderName());
                                    matchRequestMap.put("senderSurname", matchRequestModel.getSenderSurname());


                                    documentRef.update(matchRequestMap)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    matchRequestFuture.complete(null);

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // An error occurred while updating the document
                                                }
                                            });
                                }




                            }
                        });


                        CompletableFuture.allOf(receiverFuture, senderFuture, matchRequestFuture).thenRun(() -> {
                            receiverUserModel.setStatus(StudentStatus.NOT_LOOKING);
                            senderUserModel.setStatus(StudentStatus.NOT_LOOKING);

                            senderDocRef.set(senderUserModel);
                            receiverDocRef.set(receiverUserModel);
                            Toast.makeText(view.getContext(), "Request Accepted Profile Updated", Toast.LENGTH_SHORT).show();


                        });



                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.create().show();

            }
        });


        holder.rejectRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Are You Sure To Reject The Request");
                builder.setMessage("Click Yes To Reject");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        Query matchRequestDocRef = db.collection("matchRequests")
                                .whereEqualTo("approvalStatus", "Pending Approval")
                                .whereEqualTo("receiverID", receiverUserID);

                        matchRequestDocRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    // Get the document data and update it
                                    matchRequestModel = documentSnapshot.toObject(MatchRequestModel.class);
                                    String documentId = documentSnapshot.getId();
                                    DocumentReference documentRef = db.collection("matchRequests").document(documentId);
                                    matchRequestModel.setApprovalStatus("Rejected");

                                    Map<String, Object> matchRequestMap = new HashMap<>();
                                    matchRequestMap.put("approvalStatus", matchRequestModel.getApprovalStatus());
                                    matchRequestMap.put("receiverID", receiverUserID);
                                    matchRequestMap.put("senderEmail", matchRequestModel.getSenderEmail());
                                    matchRequestMap.put("senderID", senderUserID);
                                    matchRequestMap.put("senderName", matchRequestModel.getSenderName());
                                    matchRequestMap.put("senderSurname", matchRequestModel.getSenderSurname());


                                    documentRef.update(matchRequestMap)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(view.getContext(), "Request Rejected Profile Updated", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(view.getContext(), "Request Failed", Toast.LENGTH_SHORT).show();


                                                }
                                            });
                                }




                            }
                        });
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.create().show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView viewSenderName, viewSenderSurname, viewStatus, viewSenderPhoneNumber, viewSenderEmail;

        Button acceptRequestButton, rejectRequestButton;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            viewSenderName = itemView.findViewById(R.id.all_requests_name);
            viewSenderSurname = itemView.findViewById(R.id.all_requests_surname);
            viewStatus = itemView.findViewById(R.id.all_requests_status);
            viewSenderEmail = itemView.findViewById(R.id.all_requests_email);
            viewSenderPhoneNumber = itemView.findViewById(R.id.all_requests_phone_number);
            acceptRequestButton = itemView.findViewById(R.id.accept_match_request);
            rejectRequestButton = itemView.findViewById(R.id.reject_match_request);
        }

    }

}

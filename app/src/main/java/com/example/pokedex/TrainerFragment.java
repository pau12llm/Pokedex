package com.example.pokedex;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class TrainerFragment extends Fragment {

    private static final String TAG = "TrainerFragment";

    // UI components
    private TextView userNameTextView;
    private TextView userMoneyTextView;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trainer, container, false);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        // Initialize Cloud Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI views
        userNameTextView = view.findViewById(R.id.userNameTextView);
        userMoneyTextView = view.findViewById(R.id.userMoneyTextView);
        Button logoutButton = view.findViewById(R.id.logoutButton);

        // Get currently authenticated user
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            userID = user.getUid();
            // Set click listener for logout button
            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Sign out the user
                    mAuth.signOut();
                    // Navigate to login screen
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    // Finish current activity
                    getActivity().finish();
                }
            });
        } else {
            Toast.makeText(getActivity(), "No user is found!", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (userID != null) {
            getUserData();
        }
    }

    private void getUserData() {
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            for (DocumentSnapshot document : documents) {
                                if (document.getId().equals(userID)) {
                                    String name = document.getString("nombre");
                                    Long money = document.getLong("money");

                                    userNameTextView.setText("Trainer Name: " + name);
                                    userMoneyTextView.setText("Money: " + String.valueOf(money));
                                    break;
                                }
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error getting user data: " + e.getMessage());
                    }
                });
    }
}
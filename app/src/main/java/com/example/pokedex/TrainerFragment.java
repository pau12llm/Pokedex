package com.example.pokedex;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class TrainerFragment extends Fragment {

    private static final String TAG = "TrainerFragment";

    // UI components
    private TextView userNameTextView;
    private TextView userMoneyTextView;
    private ImageButton changeNameButton;
    private Button logoutButton;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userEmail;
    private String documentId;

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
        logoutButton = view.findViewById(R.id.logoutButton);
        changeNameButton = view.findViewById(R.id.changeNameButton);

        // Get currently authenticated user
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            userEmail = user.getEmail();
            Log.d(TAG, "User Email: " + userEmail); // Log the user email

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

            // Set click listener for change name button
            changeNameButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showNameChangePopup();
                }
            });

            // Get initial user data and add snapshot listener
            getUserData();
            addSnapshotListener();
        } else {
            Toast.makeText(getActivity(), "No user is found!", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (userEmail != null) {
            getUserData();
        }
    }

    private void getUserData() {
        db.collection("users")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty()) {
                                DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                                documentId = document.getId();
                                String name = document.getString("nombre");
                                Long money = document.getLong("money");

                                Log.d(TAG, "Document data: " + document.getData()); // Log the entire document data

                                // Set the retrieved data to the TextView
                                userNameTextView.setText(name);
                                userMoneyTextView.setText("Money: " + String.valueOf(money));
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error getting user data: " + e.getMessage());
                    }
                });
    }

    private void showNameChangePopup() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater.inflate(R.layout.name_change_popup, null);

        final EditText popupUserNameEditText = popupView.findViewById(R.id.popupUserNameEditText);
        Button popupSaveButton = popupView.findViewById(R.id.popupSaveButton);

        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setElevation(90);

        final View rootView = getActivity().findViewById(android.R.id.content);
        final ViewGroup rootViewGroup = (ViewGroup) rootView;
        final ColorDrawable dimDrawable = new ColorDrawable(Color.BLACK);
        dimDrawable.setAlpha(150);
        rootViewGroup.getOverlay().add(dimDrawable);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                rootViewGroup.getOverlay().remove(dimDrawable);
            }
        });

        popupSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserData(popupUserNameEditText.getText().toString(), popupWindow);
            }
        });

        popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
    }

    private void saveUserData(String newName, final PopupWindow popupWindow) {
        if (documentId != null) {
            DocumentReference docRef = db.collection("users").document(documentId);

            docRef.update("nombre", newName)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                            Toast.makeText(getActivity(), "Name updated", Toast.LENGTH_SHORT).show();

                            // Update the TextView with the new name
                            userNameTextView.setText(newName);
                            // Close the popup
                            popupWindow.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error updating document", e);
                            Toast.makeText(getActivity(), "Failed to update name", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(getActivity(), "No document ID found", Toast.LENGTH_SHORT).show();
        }
    }

    private void addSnapshotListener() {
        if (documentId != null) {
            DocumentReference docRef = db.collection("users").document(documentId);
            docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        Long money = documentSnapshot.getLong("money");
                        userMoneyTextView.setText("Money: " + String.valueOf(money));
                    } else {
                        Log.d(TAG, "Current data: null");
                    }
                }
            });
        }
    }
}

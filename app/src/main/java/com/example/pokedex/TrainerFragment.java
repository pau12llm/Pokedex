package com.example.pokedex;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TrainerFragment extends Fragment {

    private static final String TAG = "TrainerFragment";

    // UI components
    private ListView pokemonListView;
    private PokemonAdapter adapter;
    private TextView userNameTextView;
    private TextView userMoneyTextView;
    private ImageButton changeNameButton;
    private Button logoutButton;


    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userEmail;
    private String documentId;

    // Lista de Pokémon capturados
    private List<Pokemon> capturedPokemonList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trainer, container, false);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI views
        pokemonListView = view.findViewById(R.id.pokemonListView);
        userNameTextView = view.findViewById(R.id.userNameTextView);
        userMoneyTextView = view.findViewById(R.id.userMoneyTextView);
        logoutButton = view.findViewById(R.id.logoutButton);
        changeNameButton = view.findViewById(R.id.changeNameButton);

        // Initialize the list and adapter
        capturedPokemonList = new ArrayList<>();
        adapter = new PokemonAdapter(getContext(), capturedPokemonList);
        pokemonListView.setAdapter(adapter);

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
    private void showNameChangePopup() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater.inflate(R.layout.name_change_popup, null);

        final EditText popupUserNameEditText = popupView.findViewById(R.id.popupUserNameEditText);
        Button popupSaveButton = popupView.findViewById(R.id.popupSaveButton);

// Calcular el 50% de la pantalla
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int) (displayMetrics.widthPixels * 0.9);
        int height = (int) (displayMetrics.heightPixels * 0.5);

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

// Cambiar el fondo a negro transparente
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        final View rootView = getActivity().findViewById(android.R.id.content);
        popupSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserData(popupUserNameEditText.getText().toString(), popupWindow);
            }
        });

        popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);

    }

    private void saveUserData(final String newName, final PopupWindow popupWindow) {
        if (documentId != null) {
            DocumentReference docRef = db.collection("users").document(documentId);

            docRef.update("nombre", newName)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                            Toast.makeText(getActivity(), "Name updated", Toast.LENGTH_SHORT).show();

                            // Actualizar el TextView con el nuevo nombre
                            userNameTextView.setText(newName);
                            // Cerrar el popup
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
                                loadCapturedPokemons(document);
                                String name = document.getString("nombre");
                                Long money = document.getLong("money");
                                Log.d(TAG, "Document data: " + document.getData());
                                userNameTextView.setText(name);
                                userMoneyTextView.setText("Money: " + String.valueOf(money));
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
    }

    private void addSnapshotListener() {
        if (documentId != null) {
            db.collection("users")
                    .document(documentId)
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w(TAG, "Listen failed.", e);
                                return;
                            }

                            if (documentSnapshot != null && documentSnapshot.exists()) {
                                loadCapturedPokemons(documentSnapshot);
                            } else {
                                Log.d(TAG, "Current data: null");
                            }
                        }
                    });
        }
    }

    private void loadCapturedPokemons(DocumentSnapshot document) {
        Map<String, Object> pokemonsMap = (Map<String, Object>) document.get("pokemons");
        if (pokemonsMap != null) {
            capturedPokemonList.clear(); // Clear the current list
            for (Map.Entry<String, Object> entry : pokemonsMap.entrySet()) {
                Map<String, Object> pokemonData = (Map<String, Object>) entry.getValue();
                String name = (String) pokemonData.get("name");
                String url_front_default = (String) pokemonData.get("url_front_default");
                String pokeball = (String) pokemonData.get("pokeball");

                Pokemon pokemon = new Pokemon(0, name, "");
                pokemon.setUrl_front_default(url_front_default);
                pokemon.setPokeball(pokeball);
                capturedPokemonList.add(pokemon);
            }
            adapter.notifyDataSetChanged(); // Notify the adapter to refresh the list
        }
    }
}
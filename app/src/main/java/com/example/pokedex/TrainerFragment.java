package com.example.pokedex;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
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

    private ListView pokemonListView;
    private PokemonAdapter adapter;
    private TextView userNameTextView;
    private TextView userMoneyTextView;

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

        pokemonListView = view.findViewById(R.id.pokemonListView);
        userNameTextView = view.findViewById(R.id.userNameTextView);
        userMoneyTextView = view.findViewById(R.id.userMoneyTextView);

        capturedPokemonList = new ArrayList<>();
        adapter = new PokemonAdapter(getContext(), capturedPokemonList);
        pokemonListView.setAdapter(adapter);

        pokemonListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showReleasePokemonPopup(capturedPokemonList.get(position));
            }
        });

        ImageView changeNameButton = view.findViewById(R.id.changeNameButton);
        changeNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeNamePopup();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "TrainerFragment is now visible");

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            userEmail = user.getEmail();
            Log.d(TAG, "User Email: " + userEmail); // Log the user email

            // Get initial user data and add snapshot listener
            getUserData();
            addSnapshotListener();
        } else {
            Toast.makeText(getActivity(), "No user is found!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "TrainerFragment is no longer visible");
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
                                userNameTextView.setText(document.getString("nombre"));
                                userMoneyTextView.setText("Money: " + document.getLong("money"));
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
                                userNameTextView.setText(documentSnapshot.getString("nombre"));
                                userMoneyTextView.setText("Money: " + documentSnapshot.getLong("money"));
                            } else {
                                Log.d(TAG, "Current data: null");
                            }
                        }
                    });
        }
    }

    public void loadCapturedPokemons(DocumentSnapshot document) {
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

    public void addCapturedPokemon(Pokemon pokemon) {
        capturedPokemonList.add(pokemon);
        adapter.notifyDataSetChanged();
    }

    private void showReleasePokemonPopup(Pokemon pokemon) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_release_pokemon, null);
        dialogBuilder.setView(dialogView);

        ImageView pokemonImageView = dialogView.findViewById(R.id.pokemonImageView);
        Glide.with(this)
                .load(pokemon.getUrl_front_default())
                .into(pokemonImageView);

        Button confirmReleaseButton = dialogView.findViewById(R.id.confirmReleaseButton);
        confirmReleaseButton.setOnClickListener(v -> {
            releasePokemon(pokemon.getName());
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private void releasePokemon(String pokemonName) {
        if (documentId != null) {
            DocumentReference userDocRef = db.collection("users").document(documentId);
            userDocRef.update("pokemons." + pokemonName, null)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getActivity(), "Pokémon released", Toast.LENGTH_SHORT).show();
                        // Update local list and notify adapter
                        for (Pokemon p : capturedPokemonList) {
                            if (p.getName().equals(pokemonName)) {
                                capturedPokemonList.remove(p);
                                break;
                            }
                        }
                        adapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error releasing Pokémon", Toast.LENGTH_SHORT).show());
        }
    }

    private void showChangeNamePopup() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.name_change_popup, null);
        dialogBuilder.setView(dialogView);

        EditText popupUserNameEditText = dialogView.findViewById(R.id.popupUserNameEditText);
        Button popupSaveButton = dialogView.findViewById(R.id.popupSaveButton);
        popupSaveButton.setOnClickListener(v -> {
            String newName = popupUserNameEditText.getText().toString();
            changeTrainerName(newName);
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private void changeTrainerName(String newName) {
        if (documentId != null) {
            DocumentReference userDocRef = db.collection("users").document(documentId);
            userDocRef.update("nombre", newName)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getActivity(), "Trainer name updated", Toast.LENGTH_SHORT).show();
                        userNameTextView.setText(newName);
                    })
                    .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error updating trainer name", Toast.LENGTH_SHORT).show());
        }
    }
}

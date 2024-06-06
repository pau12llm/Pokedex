package com.example.pokedex;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

        // Initialize the list and adapter
        capturedPokemonList = new ArrayList<>();
        adapter = new PokemonAdapter(getContext(), capturedPokemonList);
        pokemonListView.setAdapter(adapter);

        // Get currently authenticated user
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

        return view;
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

package com.example.pokedex;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TrainerFragment extends Fragment {
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DocumentReference entrenadorDoc = FirebaseFirestore.getInstance().collection("trainers").document(userId);

    private TextView trainerNameTextView;
    private TextView trainerMoneyTextView;
    private RecyclerView itemRecyclerView;
    private RecyclerView pokemonRecyclerView;
    private List<Pokemon> pokemonList;

    private PokemonAdapter pokemonAdapter;
    private RequestQueue requestQueue;

    public TrainerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trainer, container, false);

        entrenadorDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String nombreEntrenador = documentSnapshot.getString("nombre");
            }
        });

        return view;
    }

    private void fetchPokemonData() {
        String apiEndpoint = "https://pokeapi.co/api/v2/pokemon?limit=151";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, apiEndpoint, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray results = response.getJSONArray("results");

                    for (int i = 0; i < results.length(); i++) {
                        JSONObject pokemon = results.getJSONObject(i);

                        String name = pokemon.getString("name");
                        String url = pokemon.getString("url");

                        fetchPokemonDetails(name, url, i);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    private void fetchPokemonDetails(String name, String url, int index) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray types = response.getJSONArray("types");
                    JSONObject type = types.getJSONObject(0);
                    String typeName = type.getJSONObject("type").getString("name");

                    int id = response.getInt("id");
                    String imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/"+ id +".png";

                    //pokemonList.add(new Pokemon(name, typeName, imageUrl));
                    //pokemonAdapter.notifyItemInserted(index);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }
}

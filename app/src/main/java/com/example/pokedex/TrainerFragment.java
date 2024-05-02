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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TrainerFragment extends Fragment {

    private TextView trainerNameTextView;
    private TextView trainerMoneyTextView;
    private RecyclerView itemRecyclerView;
    private RecyclerView pokemonRecyclerView;
    private List<Item> itemList;
    private List<Pokemon> pokemonList;
    private ItemAdapter itemAdapter;
    private PokemonAdapter pokemonAdapter;
    private RequestQueue requestQueue;

    public TrainerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trainer, container, false);

        // Initialize UI elements
        trainerNameTextView = view.findViewById(R.id.trainer_name_text_view);
        trainerMoneyTextView = view.findViewById(R.id.trainer_money_text_view);
        itemRecyclerView = view.findViewById(R.id.item_recycler_view);
        pokemonRecyclerView = view.findViewById(R.id.pokemon_recycler_view);

        // Initialize item list and adapter
        itemList = new ArrayList<>();
        itemAdapter = new ItemAdapter(itemList);
        itemRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        itemRecyclerView.setAdapter(itemAdapter);

        // Initialize pokemon list and adapter
        pokemonList = new ArrayList<>();
        pokemonAdapter = new PokemonAdapter(pokemonList);
        pokemonRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        pokemonRecyclerView.setAdapter(pokemonAdapter);

        // Initialize Volley request queue
        requestQueue = Volley.newRequestQueue(getContext());

        // Fetch trainer data from API
        fetchTrainerData();

        return view;
    }

    private void fetchTrainerData() {
        // TODO: Replace with actual API endpoint for fetching trainer data
        String apiEndpoint = "https://pokeapi.co/api/v2/pokemon/1";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, apiEndpoint, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // TODO: Replace with actual JSON keys for fetching trainer data
                    String trainerName = response.getString("name");
                    int trainerMoney = response.getInt("money");

                    // Update UI with trainer data
                    trainerNameTextView.setText(trainerName);
                    trainerMoneyTextView.setText(String.valueOf(trainerMoney));

                    // Fetch item data from API
                    fetchItemData();

                    // Fetch pokemon data from API
                    fetchPokemonData();

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

    private void fetchItemData() {
        // TODO: Replace with actual API endpoint for fetching item data
        String apiEndpoint = "https://pokeapi.co/api/v2/item/1";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, apiEndpoint, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // TODO: Replace with actual JSON keys for fetching item data
                    String itemName = response.getString("name");
                    String itemDescription = response.getString("description");

                    // Create new item object
                    Item item = new Item(itemName, itemDescription);

                    // Add item to item list
                    itemList.add(item);

                    // Update item adapter
                    itemAdapter.notifyDataSetChanged();

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

    private void fetchPokemonData() {
        // TODO: Replace with actual API endpoint for fetching pokemon data
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

                        // Fetch more details about the pokemon
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

                    // Add pokemon to pokemon list
                    pokemonList.add(new Pokemon(name, typeName, imageUrl));

                    // Update pokemon adapter
                    pokemonAdapter.notifyItemInserted(index);

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


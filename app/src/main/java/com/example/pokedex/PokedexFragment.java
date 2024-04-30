package com.example.pokedex;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PokedexFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PokedexFragment extends Fragment {
    private static final String TAG = "PokedexFragment";
    private static final String BASE_URL = "https://pokeapi.co/api/v2/";

    private RequestQueue requestQueue;
    private GridView gridView;
    private PokemonAdapter adapter;
    private List<String> pokemonNames;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pokedex, container, false);

        // Inicializar la cola de solicitudes Volley
        requestQueue = Volley.newRequestQueue(requireContext());

        // Obtener la referencia al GridView desde el layout
        gridView = view.findViewById(R.id.gridView);

        // Realizar la solicitud para obtener la lista de nombres de Pokémon
        stringRequest();

        return view;
    }


    private void stringRequest() {
        String allPokemonUrl = BASE_URL + "pokemon?limit=1000";

        StringRequest request = new StringRequest(
                Request.Method.GET,
                allPokemonUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray results = jsonResponse.getJSONArray("results");
                            System.out.println("Vamos a probar!! " );
                            pokemonNames = new ArrayList<>();
                            for (int i = 0; i < results.length(); i++) {
                                JSONObject pokemon = results.getJSONObject(i);
                                String name = pokemon.getString("name");
                                pokemonNames.add(name);
                                System.out.println("name: " + name);
                            }

                            // Configurar el adaptador con la lista de nombres de Pokémon
                            adapter = new PokemonAdapter(requireContext(), pokemonNames);

                            // Vincular el adaptador al GridView
                            gridView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "Error al analizar la respuesta JSON: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error en la solicitud: " + error.toString());
                    }
                }
        );

        // Agregar la solicitud a la cola de solicitudes
        requestQueue.add(request);
    }


}
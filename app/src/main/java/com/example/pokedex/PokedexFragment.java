package com.example.pokedex;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.

 * create an instance of this fragment.
 */
public class PokedexFragment extends Fragment {
    private static final String TAG = "PokedexFragment";
    private static final String BASE_URL = "https://pokeapi.co/api/v2/";

    private RequestQueue requestQueue;
    private GridView gridView;
    private PokemonAdapter adapter;
    private List<Pokemon> pokemonList;

    private EditText searchEditText;

    private int offset = 0;
    private final int limit = 15;
    private boolean isLoading = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pokedex, container, false);

        // Inicializar la cola de solicitudes Volley
        requestQueue = Volley.newRequestQueue(requireContext());

        // Obtener la referencia al GridView desde el layout
        gridView = view.findViewById(R.id.gridView);

        // Inicializar la lista de Pokémon
        pokemonList = new ArrayList<>();

        // Configurar el adaptador con la lista de nombres de Pokémon
        adapter = new PokemonAdapter(requireContext(), pokemonList);

        // Vincular el adaptador al GridView
        gridView.setAdapter(adapter);

        // Realizar la primera solicitud para cargar los primeros Pokémon
        pokemonListRequest();

        searchEditText = view.findViewById(R.id.searchEditText);
        // Configurar el listener para la acción de teclado "Intro"
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        (event != null && event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    // Se ha presionado la tecla "Intro" en el teclado
                    if (event == null || !event.isShiftPressed()) {
                        // Realizar la búsqueda cuando se presiona "Intro" sin mantener presionado Shift
                        performSearch();
                        return true; // Indicar que se ha manejado el evento
                    }
                }
                return false; // No se ha manejado el evento
            }
        });

        return view;
    }


    private void pokemonListRequest() {
        if (isLoading) {
            return; // Evitar solicitudes duplicadas mientras se carga
        }
        isLoading = true;

        String allPokemonUrl = BASE_URL + "pokemon/?offset=" + offset + "&limit=" + limit;

        StringRequest request = new StringRequest(
                Request.Method.GET,
                allPokemonUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray results = jsonResponse.getJSONArray("results");

                            for (int i = 0; i < results.length(); i++) {
                                JSONObject pokemonJSON = results.getJSONObject(i);
                                String name = pokemonJSON.getString("name");
                                name = name.substring(0, 1).toUpperCase() + name.substring(1);
                                String url = pokemonJSON.getString("url");
                                Pokemon pokemon = new Pokemon(offset + i + 1, name, url);
                                pokemonList.add(pokemon);

                                // Obtener los detalles del Pokémon para extraer las URL de las imágenes
                                pokemonDetailRequest(pokemon);
                            }

                            // Incrementar el offset para la siguiente página de Pokémon
                            offset += limit;
                            isLoading = false;

                            // Notificar al adaptador que los datos han cambiado
                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "Error al analizar la respuesta JSON: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isLoading = false;
                        Log.e(TAG, "Error en la solicitud: " + error.toString());
                    }
                }
        );

        // Agregar la solicitud a la cola de solicitudes
        requestQueue.add(request);


        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // No es necesario implementar nada aquí para la carga progresiva
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount >= totalItemCount) {
                    // El usuario ha llegado al final de la lista, cargar más Pokémon
                    pokemonListRequest();
                }
            }
        });
    }


    private void pokemonDetailRequest(final Pokemon pokemon) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                pokemon.getUrl_API(),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject sprites = response.getJSONObject("sprites");
                            String defaultImageUrl = sprites.getString("front_default");
                            String shinyImageUrl = sprites.getString("front_shiny");
                            pokemon.setUrl_default(defaultImageUrl);
                            pokemon.setUrl_shiny(shinyImageUrl);
                            adapter.notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "Error al analizar la respuesta JSON del detalle del Pokémon: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error en la solicitud del detalle del Pokémon: " + error.toString());
                    }
                }
        );

        // Agregar la solicitud a la cola de solicitudes
        requestQueue.add(request);
    }


    private void performSearch() {
        String searchTerm = searchEditText.getText().toString().toLowerCase().trim();

        if (!searchTerm.isEmpty()) {
            // Limpiar la lista antes de realizar una nueva búsqueda
            pokemonList.clear();
            adapter.notifyDataSetChanged();

            // Realizar la solicitud de búsqueda
            String searchUrl = BASE_URL + "pokemon/" + searchTerm;

            System.out.println("searchUrl==" + searchUrl);
            JsonObjectRequest searchRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    searchUrl,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String name = response.getString("name");
                                name = name.substring(0, 1).toUpperCase() + name.substring(1);
                                String url = response.getString("url");
                                Pokemon pokemon = new Pokemon(0, name, url); // Ajusta el ID según sea necesario
                                pokemonList.add(pokemon);

                                // Obtener detalles del Pokémon buscado
                                pokemonDetailRequest(pokemon);

                                adapter.notifyDataSetChanged(); // Actualizar el adaptador
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e(TAG, "Error al analizar la respuesta de búsqueda JSON: " + e.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "Error en la solicitud de búsqueda: " + error.toString());
                        }
                    }
            );

            // Agregar la solicitud de búsqueda a la cola
            requestQueue.add(searchRequest);
        }
    }

}
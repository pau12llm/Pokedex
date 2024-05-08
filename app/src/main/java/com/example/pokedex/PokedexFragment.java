package com.example.pokedex;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

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
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||
                        keyEvent != null && keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                                keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    // Acción a realizar cuando se presiona la tecla "Intro"
                    System.out.println("pokelist =" +pokemonList.toString());

//                    performSearch(); // Por ejemplo, una función para iniciar la búsqueda
                    return true; // Indicar que se ha manejado el evento
                }
                return false; // Devolver false para permitir que el sistema maneje el evento también
            }
        });

        // Configurar el listener para el clic en los elementos del GridView
//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                // Manejar el clic en un elemento del GridView
//                Pokemon clickedPokemon = pokemonList.get(position);
//                String pokemonName = clickedPokemon.getName();
//
//                // Mostrar un Toast con el nombre del Pokémon clicado
//                Toast.makeText(requireContext(), "Clic en: " + pokemonName, Toast.LENGTH_SHORT).show();
//
//                // Aquí puedes abrir una nueva actividad o fragmento para mostrar detalles del Pokémon seleccionado
//            }
//        });

        // Configurar el listener para el clic en los elementos del GridView
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Manejar el clic en un elemento del GridView

                Pokemon clickedPokemon = pokemonList.get(position);

                showPokemonDetailsPopup(clickedPokemon);
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
                            String defaultBackImageUrl = sprites.getString("back_default");
                            String shinyBackImageUrl = sprites.getString("back_shiny");

                            pokemon.setUrl_front_default(defaultImageUrl);
                            pokemon.setUrl_front_shiny(shinyImageUrl);
                            pokemon.setUrl_back_default(defaultBackImageUrl);
                            pokemon.setUrl_back_shiny(shinyBackImageUrl);
                            pokemonDetailInfoRequest(pokemon);
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

    private void pokemonDetailInfoRequest(final Pokemon pokemon) {
        String allPokemonUrl = BASE_URL + "pokemon-species/" + pokemon.getNumber() + "/" ;
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                allPokemonUrl,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Obtener el flavor_text en inglés ("en")
                            JSONArray flavorTextEntries = response.getJSONArray("flavor_text_entries");
                            String flavorText = null;
                            for (int i = 0; i < flavorTextEntries.length(); i++) {
                                JSONObject entry = flavorTextEntries.getJSONObject(i);
                                JSONObject language = entry.getJSONObject("language");
                                String languageName = language.getString("name");

                                if (languageName.equals("en")) {
                                    // Acceder al flavor_text de la entrada en inglés
                                    flavorText = processFlavorText(entry.getString("flavor_text"));

                                    // Romper el bucle una vez que se encuentre el flavor_text en inglés
                                    break;
                                }
                            }

                            pokemon.setDescription(flavorText);

//                            adapter.notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
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

    private String processFlavorText(String flavorText) {
        // Reemplazar secuencias de escape para manejar saltos de línea y otros caracteres especiales
        flavorText = flavorText.replace("\n", " "); // Reemplazar saltos de línea con espacios
        flavorText = flavorText.replace("\f", " "); // Eliminar saltos de página (\f)

        // Eliminar caracteres adicionales no deseados (como espacios duplicados)
        flavorText = flavorText.trim(); // Eliminar espacios en blanco al inicio y al final

        return flavorText;
    }

    private void showPokemonDetailsPopup(Pokemon pokemon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(pokemon.getName());

        // Inflar el layout del popup personalizado
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View popupView = inflater.inflate(R.layout.popup_pokemon_details, null);

        // Obtener referencias a los elementos del layout del popup
        ImageView imageViewFront = popupView.findViewById(R.id.imageViewFront);
        ImageView imageViewBack = popupView.findViewById(R.id.imageViewBack);
        TextView textViewName = popupView.findViewById(R.id.textViewName);
        TextView textViewDescription = popupView.findViewById(R.id.textViewDescription);

        // Establecer el nombre del Pokémon en el TextView
        textViewName.setText(pokemon.getName());
        textViewDescription.setText(pokemon.getDescription());

        // Cargar la imagen del Pokémon usando Glide
        Glide.with(requireContext())
                .load(pokemon.getUrl_front_default())
//                .placeholder(R.drawable.placeholder_image) // Placeholder mientras se carga la imagen
//                .error(R.drawable.error_image) // Imagen a mostrar si hay un error de carga
                .into(imageViewFront);
        Glide.with(requireContext())
                .load(pokemon.getUrl_back_default())

//                .placeholder(R.drawable.placeholder_image) // Placeholder mientras se carga la imagen
//                .error(R.drawable.error_image) // Imagen a mostrar si hay un error de carga
                .into(imageViewBack);

        // Configurar el contenido del popup
        builder.setView(popupView);

        // Mostrar el popup
        AlertDialog dialog = builder.create();
        dialog.show();
    }



//    private void performSearch() {
//        String searchTerm = searchEditText.getText().toString().toLowerCase().trim();
//
//        if (!searchTerm.isEmpty()) {
//            // Limpiar la lista antes de realizar una nueva búsqueda
//            pokemonList.clear();
//            adapter.notifyDataSetChanged();
//
//            // Realizar la solicitud de búsqueda
//            String searchUrl = BASE_URL + "pokemon/" + searchTerm;
//
//            JsonObjectRequest searchRequest = new JsonObjectRequest(
//                    Request.Method.GET,
//                    searchUrl,
//                    null,
//                    new Response.Listener<JSONObject>() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            try {
//                                pokemonList.clear();
//                                String name = response.getString("name");
//                                name = name.substring(0, 1).toUpperCase() + name.substring(1);
//                                String url = response.getString("url");
//
//                                System.out.println("name="+ name);
//                                Pokemon pokemon = new Pokemon(0, name, url); // Ajusta el ID según sea necesario
//
//                                // Obtener detalles del Pokémon buscado
//                                pokemonDetailRequest(pokemon);
//
//                                // Agregar el Pokémon a la lista y notificar al adaptador
//                                pokemonList.add(pokemon);
//                                adapter.notifyDataSetChanged();
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                                Log.e(TAG, "Error al analizar la respuesta de búsqueda JSON: " + e.getMessage());
//                            }
//                        }
//                    },
//                    new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            Log.e(TAG, "Error en la solicitud de búsqueda: " + error.toString());
//                        }
//                    }
//            );
//
//            // Agregar la solicitud de búsqueda a la cola
//            requestQueue.add(searchRequest);
//        }
//    }
}
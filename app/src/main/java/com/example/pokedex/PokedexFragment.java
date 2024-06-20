package com.example.pokedex;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class PokedexFragment extends Fragment {
    private static final String TAG = "PokedexFragment";
    private static final String BASE_URL = "https://pokeapi.co/api/v2/";

    private RequestQueue requestQueue;
    private GridView gridView;

    private PokemonAdapter adapter;
    private List<Pokemon> pokemonList;
    private List<Pokemon> pokemonListSearch;
    private List<Pokemon> capturedPokemonList;
    private EditText searchEditText;

    private int offset = 0;
    private final int limit = 15;
    private boolean isLoading = false;
    private boolean listSearch = false;

    private ProgressBar hpBar;
    private ProgressBar attackBar;
    private ProgressBar defenseBar;
    private ProgressBar specialAttackBar;
    private ProgressBar specialDefenseBar;
    private ProgressBar speedBar;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String documentId;
    private View popupView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pokedex, container, false);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        capturedPokemonList = new ArrayList<Pokemon>();
        getUserData();
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
//        pokemonListRequest();

        searchEditText = view.findViewById(R.id.searchEditText);

        // Configurar el listener para la acción de teclado "Intro"
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||
                        (keyEvent != null && keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                                keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    // presiona la tecla "Intro"
                    String query = searchEditText.getText().toString().trim();
                    if (!query.isEmpty()) {
                        pokemonListRequest(query);
                        listSearch = true;
                    } else {
                        listSearch = false;
                        adapter.setPokemonList(pokemonList);
                        adapter.notifyDataSetChanged();
                    }
                    return true;
                }
                return false;
            }

        });

        // Configurar el listener para el clic en los elementos del GridView
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // clic en un elemento del GridView
                Pokemon clickedPokemon;
                if (listSearch) {
                    clickedPokemon = pokemonListSearch.get(position);
                } else {
                    clickedPokemon = pokemonList.get(position);
                }
                showPokemonDetailsPopup(clickedPokemon);
            }
        });

        return view;
    }


    private void pokemonListRequest(String query) {
        if (isLoading) {
            return; // Evitar solicitudes duplicadas mientras se carga
        }
        isLoading = true;

        String pokemonUrl = BASE_URL + "pokemon/" + query.toLowerCase();
        System.out.println(" parse JSON response0:" + pokemonUrl);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                pokemonUrl,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONArray abilitiesArray = response.getJSONArray("forms");
                            int idPokemon = response.getInt("id");

                            for (int i = 0; i < abilitiesArray.length(); i++) {
                                JSONObject abilityObject = abilitiesArray.getJSONObject(i);

                                // Obtener el nombre de la habilidad desde el objeto de habilidad
                                String abilityName = abilityObject.getString("name");
                                String abilityUrl = "https://pokeapi.co/api/v2/pokemon/" + idPokemon + "/";


                                Pokemon pokemon = new Pokemon(151, abilityName, abilityUrl);
                                pokemonDetailRequest(pokemon);


                                // Agregar el Pokémon a la lista
                                pokemonListSearch = new ArrayList<>();
                                pokemonListSearch.clear();
                                pokemonListSearch.add(pokemon);
                            }


                            // Notificar al adaptador que los datos han cambiado
                            adapter.setPokemonList(pokemonListSearch);
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            System.out.println("Error parsing JSON 2 response: " + e.getMessage());
                        }
                        isLoading = false;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isLoading = false;
                        Log.e(TAG, "Request Error: " + error.toString());
                    }
                }
        );

        // Agregar la solicitud a la cola de solicitudes
        requestQueue.add(request);
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

                                // Verificar si el Pokémon está en la lista de capturados y actualizar pokeball y habilidad
                                for (Pokemon capturedPokemon : capturedPokemonList) {

                                    if (capturedPokemon.getName().equalsIgnoreCase(pokemon.getName())) {
                                        pokemon.setPokeball(capturedPokemon.getPokeball());
                                        pokemon.setAbility(capturedPokemon.getAbility());
                                        break;
                                    }
                                }
                                pokemonList.add(pokemon);

                                pokemonDetailRequest(pokemon);
                            }

                            // Incrementar el offset para la siguiente página de Pokémon
                            offset += limit;
                            isLoading = false;

                            // Notificar al adaptador que los datos han cambiado
                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "Error parsing JSON response: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isLoading = false;
                        Log.e(TAG, "Request error: " + error.toString());
                    }
                }
        );

        // Agregar la solicitud a la cola de solicitudes
        requestQueue.add(request);

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
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

    private void pokemonDetailRequest(@NonNull final Pokemon pokemon) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                pokemon.getUrl_API(),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray typesJson = response.getJSONArray("types");

                            List<String> types = new ArrayList<>();
                            for (int i = 0; i < typesJson.length(); i++) {
                                JSONObject entryType = typesJson.getJSONObject(i);
                                JSONObject type = entryType.getJSONObject("type");
                                String strTypes = type.getString("name");
                                strTypes = "https://veekun.com/dex/media/types/en/"+strTypes+".png";
                                types.add(strTypes);
                            }

                            JSONObject sprites = response.getJSONObject("sprites");
                            String defaultImageUrl = sprites.getString("front_default");
                            String shinyImageUrl = sprites.getString("front_shiny");
                            String defaultBackImageUrl = sprites.getString("back_default");
                            String shinyBackImageUrl = sprites.getString("back_shiny");

                            JSONArray stats = response.getJSONArray("stats");

                            int hp = 0;
                            int attack = 0;
                            int defense = 0;
                            int special_attack = 0;
                            int special_defense = 0;
                            int speed = 0;

                            for (int i = 0; i < stats.length(); i++) {
                                JSONObject entry = stats.getJSONObject(i);
                                JSONObject stat = entry.getJSONObject("stat");
                                String statName = stat.getString("name");

                                if (statName.equals("hp")) {
                                    hp = entry.getInt("base_stat");
                                }
                                if (statName.equals("attack")) {
                                    attack = entry.getInt("base_stat");
                                }
                                if (statName.equals("defense")) {
                                    defense = entry.getInt("base_stat");
                                }
                                if (statName.equals("special-attack")) {
                                    special_attack = entry.getInt("base_stat");
                                }
                                if (statName.equals("special-defense")) {
                                    special_defense = entry.getInt("base_stat");
                                }
                                if (statName.equals("speed")) {
                                    speed = entry.getInt("base_stat");
                                }
                            }
                            pokemon.setType(types);
                            pokemon.setUrl_front_default(defaultImageUrl);
                            pokemon.setUrl_front_shiny(shinyImageUrl);
                            pokemon.setUrl_back_default(defaultBackImageUrl);
                            pokemon.setUrl_back_shiny(shinyBackImageUrl);
                            pokemon.setHp(hp);
                            pokemon.setAttack(attack);
                            pokemon.setDefense(defense);
                            pokemon.setSpecial_attack(special_attack);
                            pokemon.setSpecial_defense(special_defense);
                            pokemon.setSpeed(speed);

                            pokemonDetailInfoRequest(pokemon);
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "Error parsing Pokémon detail JSON response: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error in requesting Pokémon details: " + error.toString());
                    }
                }
        );

        // Agregar la solicitud a la cola de solicitudes
        requestQueue.add(request);
    }

    private void pokemonDetailInfoRequest(final Pokemon pokemon) {
        String allPokemonUrl = BASE_URL + "pokemon-species/" + pokemon.getNumber() + "/";
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

                                    break;
                                }
                            }

                            pokemon.setDescription(flavorText);

//                            adapter.notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "Error parsing Pokémon detail JSON response: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error in requesting Pokémon details: " + error.toString());
                    }
                }
        );

        // Agregar la solicitud a la cola de solicitudes
        requestQueue.add(request);
    }

    private String processFlavorText(String flavorText) {
        flavorText = flavorText.replace("\n", " "); // Reemplazar saltos de línea con espacios
        flavorText = flavorText.replace("\f", " "); // Eliminar saltos de página (\f)

        flavorText = flavorText.trim(); // Eliminar espacios en blanco al inicio y al final

        return flavorText;
    }

    private void showPokemonDetailsPopup(Pokemon pokemon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(pokemon.getName());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        popupView = inflater.inflate(R.layout.popup_pokemon_details, null);

        ImageView imageViewFront = popupView.findViewById(R.id.imageViewFront);
        ImageView imageViewBack = popupView.findViewById(R.id.imageViewBack);
        ImageView pokeballImg = popupView.findViewById(R.id.pokeballImg);

        TextView textViewAbility = popupView.findViewById(R.id.textViewAbility);
        TextView textViewDescription = popupView.findViewById(R.id.textViewDescription);
        TextView textViewHp = popupView.findViewById(R.id.textViewHp);
        TextView textViewAttack = popupView.findViewById(R.id.textViewAttack);
        TextView textViewDefense = popupView.findViewById(R.id.textViewDefense);
        TextView textViewSpecialAttack = popupView.findViewById(R.id.textViewSpecialAttack);
        TextView textViewSpecialDefense = popupView.findViewById(R.id.textViewSpecialDefense);
        TextView textViewSpeed = popupView.findViewById(R.id.textViewSpeed);

        if (!pokemon.getAbility().equals("")) {
            textViewAbility.setText(pokemon.getAbility());
        }


        textViewDescription.setText(pokemon.getDescription());

        // Actualiza el texto de las estadísticas
        String hpText = getString(R.string.hp_stats, pokemon.getHp());
        textViewHp.setText(hpText);
        String attackText = getString(R.string.attack_stats, pokemon.getAttack());
        textViewAttack.setText(attackText);
        String defenseText = getString(R.string.defense_stats, pokemon.getDefense());
        textViewDefense.setText(defenseText);
        String specialAttackText = getString(R.string.special_attack_stats, pokemon.getSpecial_attack());
        textViewSpecialAttack.setText(specialAttackText);
        String specialDefenseText = getString(R.string.special_defense_stats, pokemon.getSpecial_defense());
        textViewSpecialDefense.setText(specialDefenseText);
        String speedText = getString(R.string.speed_stats, pokemon.getSpeed());
        textViewSpeed.setText(speedText);

        // Configurar los ProgressBar de estadísticas
        hpBar = popupView.findViewById(R.id.hpBar);
        attackBar = popupView.findViewById(R.id.attackBar);
        defenseBar = popupView.findViewById(R.id.defenseBar);
        specialAttackBar = popupView.findViewById(R.id.specialAttackBar);
        specialDefenseBar = popupView.findViewById(R.id.specialDefenseBar);
        speedBar = popupView.findViewById(R.id.speedBar);

        // Actualizar el progreso del ProgressBar
        hpBar.setProgress(pokemon.getHp());
        attackBar.setProgress(pokemon.getAttack());
        defenseBar.setProgress(pokemon.getDefense());
        specialAttackBar.setProgress(pokemon.getSpecial_attack());
        specialDefenseBar.setProgress(pokemon.getSpecial_defense());
        speedBar.setProgress(pokemon.getSpeed());

        // Cargar la imagen del Pokémon usando Glide
        Glide.with(requireContext())
                .load(pokemon.getUrl_front_default())
                .into(imageViewFront);
        Glide.with(requireContext())
                .load(pokemon.getUrl_back_default())
                .into(imageViewBack);
        Glide.with(requireContext())
                .load(pokemon.getPokeball())
                .into(pokeballImg);

        // Añadir imágenes de los tipos al contenedor
        LinearLayout typeImagesContainer = popupView.findViewById(R.id.typeImagesContainer);
        typeImagesContainer.removeAllViews(); // Limpiar cualquier vista existente

        for (String typeUrl : pokemon.getType()) {
            ImageView typeImageView = new ImageView(requireContext());

            // Configurar el tamaño de la imagen
            int imageSize = getResources().getDimensionPixelSize(R.dimen.type_image_size); // Tamaño deseado en píxeles
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(imageSize, imageSize);
            typeImageView.setLayoutParams(layoutParams);

            Glide.with(requireContext())
                    .load(typeUrl)
                    .into(typeImageView);

            typeImagesContainer.addView(typeImageView);
        }

        // Configurar el contenido del popup
        builder.setView(popupView);

        // Mostrar el popup
        AlertDialog dialog = builder.create();
        dialog.show();

        // Configurar el botón de captura
        Button btn_capture = popupView.findViewById(R.id.buttonCapture);
        if (pokemon.getPokeball() != null) {
            btn_capture.setVisibility(View.INVISIBLE);
        }
        btn_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CaptureActivity.class);

                // Pasar datos adicionales a la nueva actividad
                intent.putExtra("pokemon", pokemon);

                // Iniciar la actividad usando el Intent con captureActivityResultLauncher
                captureActivityResultLauncher.launch(intent);
            }
        });
    }

    private ActivityResultLauncher<Intent> captureActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {

                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();

                    if (data != null) {

                        Pokemon capturedPokemon = (Pokemon) data.getSerializableExtra("capturedPokemon");
                        if (capturedPokemon != null) {
                            // Actualizar el nombre del Pokémon en el fragmento
                            Pokemon originalPokemon = pokemonList.get(capturedPokemon.getNumber()-1); // Asegúrate de tener el índice correcto
                            originalPokemon.setAbility(capturedPokemon.getAbility());
                            originalPokemon.setPokeball(capturedPokemon.getPokeball());

                            // Notificar al adaptador que los datos han cambiado
                            adapter.notifyDataSetChanged();
                            // Actualizar el contenido del popup
                            updatePokemonDetailsPopup(originalPokemon);
                        }
                    }
                }
            }
    );

    private void updatePokemonDetailsPopup(Pokemon pokemon) {

        TextView textViewAbility = popupView.findViewById(R.id.textViewAbility);
        ImageView pokeballImg = popupView.findViewById(R.id.pokeballImg);

        textViewAbility.setText(pokemon.getAbility());
        Glide.with(requireContext())
                .load(pokemon.getPokeball())
                .into(pokeballImg);
    }
    private void getUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        String userEmail = user.getEmail();

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
                                documentId = document.getId();  // Asignar el valor a la variable de instancia
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

    private void loadCapturedPokemons(DocumentSnapshot document) {
        Map<String, Object> pokemonsMap = (Map<String, Object>) document.get("pokemons");
        if (pokemonsMap != null) {
            capturedPokemonList = new ArrayList<>(); // Inicializar la lista
            for (Map.Entry<String, Object> entry : pokemonsMap.entrySet()) {
                Map<String, Object> pokemonData = (Map<String, Object>) entry.getValue();
                String name = (String) pokemonData.get("name");
                String url_front_default = (String) pokemonData.get("url_front_default");
                String pokeball = (String) pokemonData.get("pokeball");
                String ability = (String) pokemonData.get("ability");

                Pokemon pokemon = new Pokemon(0, name, "");
                pokemon.setUrl_front_default(url_front_default);
                pokemon.setPokeball(pokeball);
                pokemon.setAbility(ability);
                capturedPokemonList.add(pokemon);

            }
        }
        // Realizar la primera solicitud para cargar los primeros Pokémon
        pokemonListRequest();
    }

}
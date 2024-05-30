package com.example.pokedex;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CaptureActivity extends AppCompatActivity implements OnItemUseClickListener {

    private List<Item> itemList;
    private BackpackAdapter adapter;
    private int type_pokemon;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userEmail;
    private String documentId;
    private Pokemon pokemon;
    private PokeApiService pokeApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_capture);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        itemList = new ArrayList<>();
        adapter = new BackpackAdapter(this, itemList, this);

        // Obtener el objeto Pokemon de los extras del Intent
        pokemon = (Pokemon) getIntent().getSerializableExtra("pokemon");

        boolean esShiny = calcularProbabilidadShiny();
        ImageView imageViewFront = findViewById(R.id.imageViewFront);
        if (esShiny) {
            Glide.with(this)
                    .load(pokemon.getUrl_front_shiny())
                    .into(imageViewFront);
            pokemon.setShiny(true);
        } else {
            Glide.with(this)
                    .load(pokemon.getUrl_front_default())
                    .into(imageViewFront);
        }

        pokeApiService = new PokeApiService(this);
        pokeApiService.getPokemonInfo(pokemon, new PokeApiService.VolleyCallback() {
            @Override
            public void onSuccess(String pokemonName, int evolutionStage, boolean isLegendary) {
                String info = "Nombre: " + pokemonName + "\n" +
                        "Etapa de evolución: " + evolutionStage + "\n" +
                        "Es legendario: " + isLegendary;
                System.out.println(info);
                pokemon.setLegendary(isLegendary);
                pokemon.setEvolution(evolutionStage);
                type_pokemon = obtenerType_pokemon(pokemon);
            }
        });

        // Configurar la lista de items de la mochila
        ListView listView = findViewById(R.id.listViewBackpack);
        listView.setAdapter(adapter);

        Button btn_escape = findViewById(R.id.btn_escape);
        btn_escape.setOnClickListener(v -> finish());

        // Fetch user items
        fetchUserItems();
    }

    private void fetchUserItems() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            userEmail = user.getEmail();

            db.collection("users")
                    .whereEqualTo("email", userEmail)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            documentId = document.getId();

                            // Obtener el mapa de items directamente del documento
                            Map<String, Object> userData = document.getData();
                            if (userData != null) {
                                Map<String, Object> itemsMap = (Map<String, Object>) userData.get("items");
                                if (itemsMap != null && !itemsMap.isEmpty()) {
                                    itemList.clear(); // Limpiamos la lista de items actual antes de agregar los nuevos
                                    for (Map.Entry<String, Object> entry : itemsMap.entrySet()) {
                                        String itemName = entry.getKey();
                                        int quantity = ((Long) entry.getValue()).intValue();
                                        // Agregar el item a la lista de items
                                        if (quantity > 0) {
                                            fetchItemDetails(itemName, quantity);
                                        }
                                    }
                                    // Notificar al adaptador que los datos han cambiado
                                    adapter.notifyDataSetChanged();
                                } else {
                                    // No se encontraron items para este usuario
                                }
                            }
                        } else {
                            // No se encontró ningún usuario con el correo electrónico específico
                        }
                    });
        }
    }

    private void fetchItemDetails(String itemName, int quantity) {
        String url = "https://pokeapi.co/api/v2/item/" + itemName;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, response -> {
            try {
                String category = response.getJSONObject("category").getString("name");
                String imageUrl = response.getJSONObject("sprites").getString("default");
                String description = response.getJSONArray("effect_entries").getJSONObject(0).getString("effect");
                String shortDescription = response.getJSONArray("effect_entries").getJSONObject(0).getString("short_effect");
                String descriptionMotivation = response.getJSONArray("flavor_text_entries").getJSONObject(0).getString("text");

                Item item = new Item(itemName, category, 0, imageUrl, description, shortDescription, descriptionMotivation, quantity);
                itemList.add(item);
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(CaptureActivity.this, "Failed to fetch item details: " + error.getMessage(), Toast.LENGTH_SHORT).show());

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onItemUseClick(Item item) {
        System.out.println("Usando el item: " + item.getName());

        // Calcular la probabilidad de captura
        boolean capturado = calcularProbabilidadCaptura(item.getName());
        itemRefactor(item);

        System.out.println("capturado? " + capturado);
        // Mostrar un Toast dependiendo del resultado de la captura
        if (capturado) {
            showToast("Genial, ¡has capturado al Pokémon!");
            int money = 400 + 100 * type_pokemon;
            moneyRefactor(money);

            pokemon.setPokeball(item.getImageUrl());
            // Registrar el Pokémon capturado en la base de datos
            registerPokemon(item);

            finish();
        } else {
            showToast("Uy, ha faltado poco para capturar al Pokémon.");
        }
    }

    public static boolean calcularProbabilidadShiny() {
        Random random = new Random();
        int randomNumber = random.nextInt(500) + 1;
        return randomNumber == 1;
    }

    public static int obtenerType_pokemon(Pokemon pokemon) {
        Random random = new Random();
        int valorIntermedio = 0;
        if (pokemon.isLegendary()) {
            valorIntermedio = 350 + random.nextInt(151);
        } else {
            switch (pokemon.getEvolution()) {
                case 1:
                    valorIntermedio = 20 + random.nextInt(61);
                    break;
                case 2:
                    valorIntermedio = 80 + random.nextInt(121);
                    break;
                case 3:
                    valorIntermedio = 200 + random.nextInt(151);
                    break;
            }
        }
        return valorIntermedio;
    }

    public boolean calcularProbabilidadCaptura(String tipoPokeball) {
        Random random = new Random();
        double randomNumber = random.nextDouble();

        switch (tipoPokeball.toLowerCase()) {
            case "poke-ball":
                return randomNumber < ((600 - type_pokemon) / 600.0);
            case "super-ball":
                return randomNumber < ((600 - type_pokemon) / 600.0 * 1.5);
            case "ultra-ball":
                return randomNumber < ((600 - type_pokemon) / 600.0 * 2);
            case "master-ball":
                return true;
            default:
                return randomNumber < ((600 - type_pokemon) / 600.0 * 1.8);
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void moneyRefactor(int money) {
        db.collection("users")
                .document(documentId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Long userMoney = task.getResult().getLong("money");
                        if (userMoney != null) {
                            DocumentReference userDocRef = db.collection("users").document(documentId);
                            userDocRef.update("money", userMoney + money);
                        }
                    }
                });
    }

    private void itemRefactor(Item item) {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            userEmail = user.getEmail();

            db.collection("users")
                    .whereEqualTo("email", userEmail)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            documentId = document.getId();

                            DocumentReference userDocRef = db.collection("users").document(documentId);

                            Map<String, Object> updates = new HashMap<>();

                            // Decrementar la cantidad del item usado
                            int newQuantity = item.getQuantity() - 1;
                            if (newQuantity > 0) {
                                updates.put("items." + item.getName(), newQuantity);
                            } else {
                                updates.put("items." + item.getName(), 0); // Eliminar el item si la cantidad es 0
                            }

                            userDocRef.update(updates)
                                    .addOnSuccessListener(aVoid -> {
                                        item.setQuantity(newQuantity);
                                        if (newQuantity == 0) {
                                            itemList.remove(item); // Eliminar el item de la lista si la cantidad es 0
                                        }
                                        adapter.notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(CaptureActivity.this, "Error al actualizar la cantidad de items", Toast.LENGTH_SHORT).show());
                        }
                    });
        }
    }

    private void registerPokemon(Item item) {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            userEmail = user.getEmail();

            db.collection("users")
                    .whereEqualTo("email", userEmail)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            documentId = document.getId();

                            DocumentReference userDocRef = db.collection("users").document(documentId);

                            // Obtener el mapa de Pokémon del usuario
                            Map<String, Object> pokemonsMap = (Map<String, Object>) document.get("pokemons");
                            if (pokemonsMap == null) {
                                pokemonsMap = new HashMap<>();
                            }

                            // Agregar el nuevo Pokémon capturado
                            pokemonsMap.put(pokemon.getName(), pokemon);

                            // Actualizar el documento del usuario en Firestore
                            userDocRef.update("pokemons", pokemonsMap)
                                    .addOnSuccessListener(aVoid -> Toast.makeText(CaptureActivity.this, "Pokémon registrado exitosamente", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast.makeText(CaptureActivity.this, "Error al registrar el Pokémon", Toast.LENGTH_SHORT).show());
                        }
                    });
        }
    }
}

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
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CaptureActivity extends AppCompatActivity implements OnItemUseClickListener {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private List<Item> itemList;
    private BackpackAdapter adapter;
    private int type_pokemon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        EdgeToEdge.enable(this);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        itemList = new ArrayList<>();
        adapter = new BackpackAdapter(this, itemList, this);

        ListView listView = findViewById(R.id.listViewBackpack);
        listView.setAdapter(adapter);

        // Obtener el objeto Pokemon de los extras del Intent
        Pokemon pokemon = (Pokemon) getIntent().getSerializableExtra("pokemon");

        boolean esShiny = calcularProbabilidadShiny();
        ImageView imageViewFront = findViewById(R.id.imageViewFront);
        if (esShiny) {
            System.out.println("El Pokémon es shiny!");
            Glide.with(this)
                    .load(pokemon.getUrl_front_shiny())
                    .into(imageViewFront);
            pokemon.setShiny(true);
        } else {
            System.out.println("El Pokémon no es shiny.");
            Glide.with(this)
                    .load(pokemon.getUrl_front_default())
                    .into(imageViewFront);
        }

        PokeApiService pokeApiService = new PokeApiService(this);
        pokeApiService.getPokemonInfo(pokemon, new PokeApiService.VolleyCallback() {
            @Override
            public void onSuccess(String pokemonName, int evolutionStage, boolean isLegendary) {
                String info = "Nombre: " + pokemonName + "\n" +
                        "Etapa de evolución: " + evolutionStage + "\n" +
                        "Es legendario: " + isLegendary;
                System.out.println(info);
                pokemon.setLegendary(isLegendary);
                pokemon.setEvolution(evolutionStage);
            }
        });

        type_pokemon = obtenerType_pokemon(pokemon);
        fetchUserItems();

        Button btn_escape = findViewById(R.id.btn_escape);
        btn_escape.setOnClickListener(v -> finish());
    }

    private void fetchUserItems() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            //wait
            DocumentReference userRef = db.collection("users").document(user.getUid());
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Map<String, Long> items = (Map<String, Long>) documentSnapshot.get("items");
                    if (items != null) {
                        for (Map.Entry<String, Long> entry : items.entrySet()) {
                            String itemName = entry.getKey();
                            int quantity = entry.getValue().intValue();
                            fetchItemDetails(itemName, quantity);
                        }
                    } else {
                        Toast.makeText(CaptureActivity.this, "No items found for the user.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CaptureActivity.this, "User document does not exist.", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> Toast.makeText(CaptureActivity.this, "Failed to fetch items: " + e.getMessage(), Toast.LENGTH_SHORT).show());
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

    public void onItemUseClick(Item item) {
        System.out.println("Usando el item: " + item.getName());

        // Calcular la probabilidad de captura
        boolean capturado = calcularProbabilidadCaptura(item.getName());

        System.out.println("capturado? " + capturado);
        // Mostrar un Toast dependiendo del resultado de la captura
        if (capturado) {
            showToast("Genial, ¡has capturado al Pokémon!");
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

        if (pokemon.isLegendary()) { // Legendarios
            valorIntermedio = 350 + random.nextInt(151); // 350 + (0-150)
        } else {
            switch (pokemon.getEvolution()) {
                case 1: // Primera evolución
                    valorIntermedio = 20 + random.nextInt(61); // 20 + (0-60)
                    break;
                case 2: // Segunda evolución
                    valorIntermedio = 80 + random.nextInt(121); // 80 + (0-120)
                    break;
                case 3: // Tercera evolución
                    valorIntermedio = 200 + random.nextInt(151); // 200 + (0-150)
                    break;
            }
        }
        return valorIntermedio;
    }

    public boolean calcularProbabilidadCaptura(String tipoPokeball) {
        Random random = new Random();
        int randomNumber = random.nextInt(600);

        switch (tipoPokeball) {
            case "pokeball":
                return randomNumber < (600 - type_pokemon) / 1;
            case "superball":
                return randomNumber < (600 - type_pokemon) / 1.5;
            case "ultraball":
                return randomNumber < (600 - type_pokemon) / 2;
            case "masterball":
                return true;
            default:
                return false;
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

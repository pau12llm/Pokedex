package com.example.pokedex;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CaptureActivity extends AppCompatActivity implements OnItemUseClickListener {
    int type_pokemon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_capture);

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

        // Configurar la lista de items de la mochila
        List<Item> itemList = new ArrayList<>();
        itemList.add(new Item(
                "Pokeball","Category",100,"https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/items/master-ball.png","description","short Desc", "..."
        ));

        BackpackAdapter adapter = new BackpackAdapter(this, itemList, this);
        ListView listView = findViewById(R.id.listViewBackpack);
        listView.setAdapter(adapter);

        Button btn_escape = findViewById(R.id.btn_escape);
        btn_escape.setOnClickListener(v -> {
            finish();
        });

    }


    public void onItemUseClick(Item item) {
        System.out.println("Usando el item: " + item.getName());


        // Calcular la probabilidad de captura
        boolean capturado = calcularProbabilidadCaptura(item.getName());

        System.out.println("capturado?"+capturado);
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
        //int randomNumber = random.nextInt(2) + 1;

        return randomNumber == 1;
    }
    public static int obtenerType_pokemon(Pokemon pokemon) {
        Random random = new Random();
        int valorIntermedio = 0;

        if (pokemon.isLegendary()){// Legendarios

            valorIntermedio = 350 + random.nextInt(151); // 350 + (0-150)
        }else {
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
        int randomNumber;
        randomNumber = random.nextInt(600);

        System.out.println( "el randomNumber" + randomNumber);
        System.out.println( "el calculo es" + ((600 - type_pokemon) / 600.0 * 1));

        switch (tipoPokeball) {
            case "Pokeball":
                return randomNumber > (600 - type_pokemon) / 600.0 * 1;
            case "Superball":
                return randomNumber > (600 - type_pokemon) / 600.0 * 1.5;
            case "Ultraball":
                return randomNumber > (600 - type_pokemon) / 600.0 * 2;
            case "Masterball":
                return true;
            default:
                return false;
        }
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
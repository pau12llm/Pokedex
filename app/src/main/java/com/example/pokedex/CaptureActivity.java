package com.example.pokedex;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CaptureActivity extends AppCompatActivity {

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

        // Configurar la lista de items de la mochila
        List<Item> itemList = new ArrayList<>();
        itemList.add(new Item(
                "Pokeball","Category",100,"https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/items/master-ball.png","description","short Desc", "..."
        ));

        BackpackAdapter adapter = new BackpackAdapter(this, itemList);
        ListView listView = findViewById(R.id.listViewBackpack);
        listView.setAdapter(adapter);

        Button btn_escape = findViewById(R.id.btn_escape);
        btn_escape.setOnClickListener(v -> {
            finish();
        });

    }

    public static boolean calcularProbabilidadShiny() {
        Random random = new Random();
//        int randomNumber = random.nextInt(500) + 1;
        int randomNumber = random.nextInt(2) + 1;

        return randomNumber == 1;
    }



}
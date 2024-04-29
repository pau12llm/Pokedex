package com.example.pokedex;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.example.pokedex.PokeApiService;
import com.example.pokedex.Pokemon;
import com.example.pokedex.R;

public class ShopFragment extends Fragment {

    private static final String TAG = "ShopFragment";
    private static final String BASE_URL = "https://pokeapi.co/api/v2/";

    private PokeApiService pokeApiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop, container, false);

        // Inicializar Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Crear instancia de PokeApiService
        pokeApiService = retrofit.create(PokeApiService.class);

        // Hacer una solicitud para obtener información de un Pokémon (por ejemplo, el Pokémon con ID 1)
        getPokemonInfo(1);

        return view;
    }
    public ShopFragment() {
        // Required empty public constructor
    }

    private void getPokemonInfo(int pokemonId) {
        Call<Pokemon> call = pokeApiService.getPokemonInfo(pokemonId);
        call.enqueue(new Callback<Pokemon>() {
            @Override
            public void onResponse(Call<Pokemon> call, Response<Pokemon> response) {
                if (response.isSuccessful()) {
                    Pokemon pokemon = response.body();
                    if (pokemon != null) {
                        // Si la respuesta es exitosa, obtenemos el objeto Pokemon
                        Log.d(TAG, "Pokemon name: " + pokemon.getName());
                        Log.d(TAG, "Pokemon number: " + pokemon.getNumber());

                        // Aquí puedes hacer lo que necesites con la información del Pokémon
                    }
                } else {
                    // Si la respuesta no es exitosa, mostramos el código de error
                    Log.e(TAG, "Error al obtener información del Pokémon. Código de error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Pokemon> call, Throwable t) {
                // Si hay un error en la solicitud, mostramos un mensaje de error
                Log.e(TAG, "Error al realizar la solicitud para obtener información del Pokémon", t);
            }
        });
    }
}

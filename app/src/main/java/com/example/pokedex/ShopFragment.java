package com.example.pokedex;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
//pokemon inmplementación
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShopFragment#//newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShopFragment extends Fragment {
    private static final String TAG = "ShopFragment";

    private static final String BASE_URL = "https://pokeapi.co/api/v2/";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shop, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final TextView pokemonNameTextView = view.findViewById(R.id.pokemonNameTextView);

        // Crear una instancia de Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // Crear una instancia de Gson con un InstanceCreator para Type
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Type.class, new TypeInstanceCreator())
                .create();

        // Crear una instancia de la interfaz PokeApiService
        PokeApiService service = retrofit.create(PokeApiService.class);

        // Realizar una solicitud para obtener información sobre un Pokémon
        Call<Pokemon> call = service.getPokemonByName("pikachu");
        call.enqueue(new Callback<Pokemon>() {
            @Override
            public void onResponse(Call<Pokemon> call, Response<Pokemon> response) {
                if (response.isSuccessful()) {
                    Pokemon pokemon = response.body();

                    // Mostrar el nombre del Pokémon en el TextView
                    pokemonNameTextView.setText("Nombre del Pokémon: " + pokemon.getName());


                } else {
                    // Manejar el error de la solicitud HTTP
                    Log.e(TAG, "Error en la solicitud: " + response.code());
                    Toast.makeText(getContext(), "Error en la solicitud: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Pokemon> call, Throwable t) {
                // Manejar el error de la solicitud
                Log.e(TAG, "Error en la solicitud: " + t.getMessage());
                Toast.makeText(getContext(), "Error en la solicitud: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

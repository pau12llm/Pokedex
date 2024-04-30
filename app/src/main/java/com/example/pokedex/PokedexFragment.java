package com.example.pokedex;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pokedex.PokeApiService;
import com.example.pokedex.Pokemon;
import com.example.pokedex.R;

import org.checkerframework.common.returnsreceiver.qual.This;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PokedexFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PokedexFragment extends Fragment {
    RequestQueue requestQueue;

    private static final String TAG = "PokedexFragment";
    private static final String BASE_URL = "https://pokeapi.co/api/v2/";
//    private PokeApiService pokeApiService;

//    public PokedexFragment() {
//        // Required empty public constructor
//    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PokedexFragment newInstance(String param1, String param2) {
        PokedexFragment fragment = new PokedexFragment();

        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop, container, false);

        requestQueue = Volley.newRequestQueue(requireContext());
        stringRequest();

//        // Inicializar Retrofit
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        // Crear instancia de PokeApiService
//        pokeApiService = retrofit.create(PokeApiService.class);
//
//        // Hacer una solicitud para obtener información de un Pokémon (por ejemplo, el Pokémon con ID 1)
//        getPokemonInfo(2);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pokedex, container, false);
    }

    private void stringRequest(){
        StringRequest request = new StringRequest(
                Request.Method.GET,
                BASE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Pokemon name: " + response);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                }


        );
    }


//    private void getPokemonInfo(int pokemonId) {
//        Call<Pokemon> call = pokeApiService.getPokemonInfo(pokemonId);
//        call.enqueue(new Callback<Pokemon>() {
//            @Override
//            public void onResponse(Call<Pokemon> call, Response<Pokemon> response) {
//                if (response.isSuccessful()) {
//                    Pokemon pokemon = response.body();
//                    if (pokemon != null) {
//                        // Si la respuesta es exitosa, obtenemos el objeto Pokemon
//                        Log.d(TAG, "Pokemon name: " + pokemon.getName());
//                        Log.d(TAG, "Pokemon number: " + pokemon.getNumber());
//
//                        // Aquí puedes hacer lo que necesites con la información del Pokémon
//                    }
//                } else {
//                    // Si la respuesta no es exitosa, mostramos el código de error
//                    Log.e(TAG, "Error al obtener información del Pokémon. Código de error: " + response.code());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Pokemon> call, Throwable t) {
//                // Si hay un error en la solicitud, mostramos un mensaje de error
//                Log.e(TAG, "Error al realizar la solicitud para obtener información del Pokémon", t);
//            }
//        });
//    }

}
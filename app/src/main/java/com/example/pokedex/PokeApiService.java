package com.example.pokedex;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PokeApiService {
    @GET("pokemon/{pokemonName}")
    Call<Pokemon> getPokemonByName(@Path("pokemonName") String pokemonName);
}
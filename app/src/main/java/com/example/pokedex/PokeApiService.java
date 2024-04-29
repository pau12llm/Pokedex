package com.example.pokedex;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;


public interface PokeApiService {
    @GET("pokemon/{id}")
    Call<Pokemon> getPokemonInfo(@Path("id") int id);
}
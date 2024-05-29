package com.example.pokedex;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PokeApiService {
    private RequestQueue requestQueue;

    public PokeApiService(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public void getPokemonInfo(Pokemon pokemon, final VolleyCallback callback) {
        String url = "https://pokeapi.co/api/v2/pokemon-species/" + pokemon.getName().toLowerCase() + "/";

        JsonObjectRequest speciesRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean isLegendary = response.getBoolean("is_legendary");
                    String evolutionChainUrl = response.getJSONObject("evolution_chain").getString("url");
                    getEvolutionChain(evolutionChainUrl, pokemon.getName(), isLegendary, callback);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(speciesRequest);
    }

    private void getEvolutionChain(String url, String pokemonName, boolean isLegendary, final VolleyCallback callback) {
        JsonObjectRequest evolutionRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject chain = response.getJSONObject("chain");
                    int evolutionStage = findEvolutionStage(chain, pokemonName, 1);
                    callback.onSuccess(pokemonName, evolutionStage, isLegendary);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(evolutionRequest);
    }

    private int findEvolutionStage(JSONObject chain, String pokemonName, int stage) throws JSONException {
        if (chain.getJSONObject("species").getString("name").equals(pokemonName.toLowerCase())) {
            return stage;
        }
        JSONArray evolvesTo = chain.getJSONArray("evolves_to");
        for (int i = 0; i < evolvesTo.length(); i++) {
            int result = findEvolutionStage(evolvesTo.getJSONObject(i), pokemonName, stage + 1);
            if (result != -1) {
                return result;
            }
        }
        return -1;
    }

    public interface VolleyCallback {
        void onSuccess(String pokemonName, int evolutionStage, boolean isLegendary);
    }
}
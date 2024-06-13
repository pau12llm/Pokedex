package com.example.pokedex;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShopFragment extends Fragment {

    private RecyclerView recyclerView;
    private ShopItemAdapter itemAdapter;
    private List<Item> itemList;

    private static final String TAG = "ShopFragment";
    private int money = 0; // Variable para almacenar el dinero
    private TextView textNumberTotal; // TextView para mostrar el total de dinero

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop, container, false);

        // Obtener una referencia al RecyclerView en el layout
        recyclerView = view.findViewById(R.id.recyclerView);

        // Inicializar la lista de items
        itemList = new ArrayList<>();

        // Configurar el RecyclerView con un GridLayoutManager
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        // Crear un adaptador vacío por ahora
        itemAdapter = new ShopItemAdapter(getContext(), itemList);
        recyclerView.setAdapter(itemAdapter);

        // Hacer la solicitud a la API para obtener los items
        fetchItems();

        return view;
    }

    private void fetchItems() {
        String url = "https://pokeapi.co/api/v2/item";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray results = response.getJSONArray("results");
                            Log.d(TAG, "Number of items received from API: " + results.length());
                            Log.d(TAG, "JSON Response: " + response.toString());
                            for (int i = 0; i < results.length(); i++) {
                                JSONObject itemObject = results.getJSONObject(i);
                                String itemName = itemObject.getString("name");
                                String itemUrl = itemObject.getString("url");
                                // Suponiendo que el precio está representado como una cadena en el JSON
                                if (itemName.contains("ball")) {
                                    fetchItemImage(itemName, itemUrl);
                                }

                            }
                            // Notificar al adaptador que los datos han cambiado
                            itemAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "Error parsing JSON", e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        // Agregar la solicitud a la cola de solicitudes
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);
    }

    private void fetchItemImage(String itemName, String itemUrl) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, itemUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Obtener la descripción corta del ítem
                            JSONArray effectEntries = response.getJSONArray("effect_entries");
                            String shortdescription = "";
                            for (int i = 0; i < effectEntries.length(); i++) {
                                JSONObject effectEntry = effectEntries.getJSONObject(i);
                                shortdescription = effectEntry.getString("short_effect");
                                // Solo se toma la primera descripción corta
                                if (!shortdescription.isEmpty()) {
                                    break;
                                }
                            }


                            // Obtener la descripción de texto de sabor del ítem
                            JSONArray flavorTextEntries = response.getJSONArray("flavor_text_entries");
                            String descriptionMotivation = "";
                            for (int i = 0; i < flavorTextEntries.length(); i++) {
                                JSONObject flavorTextEntry = flavorTextEntries.getJSONObject(i);
                                descriptionMotivation = flavorTextEntry.getString("text");
                                // Solo se toma la primera descripción de texto de sabor
                                if (!descriptionMotivation.isEmpty()) {
                                    break;
                                }
                            }

                            // Obtener la URL de la imagen del ítem
                            JSONObject spritesObject = response.getJSONObject("sprites");
                            String imageUrl = spritesObject.getString("default");

                            // Obtener la categoría y el precio del ítem
                            JSONObject categoryObject = response.getJSONObject("category");
                            String category = categoryObject.getString("name");
//                            int price = response.getInt("cost");
                            int price = calcularPrecioItem(itemName);

                            // Obtener la descripción anterior del ítem
                            String description = response.getJSONArray("effect_entries").getJSONObject(0).getString("effect");

                            // Agregar el ítem a la lista y notificar al adaptador
                            itemList.add(new Item(itemName, category, price, imageUrl, description, shortdescription, descriptionMotivation, 0)); // La cantidad es 0
                            itemAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "Error parsing JSON", e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        // Agregar la solicitud a la cola de solicitudes
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);
    }


    public int calcularPrecioItem(String tipoPokeball) {

        switch (tipoPokeball.toLowerCase()) {
            case "poke-ball":
                return 200;
            case "super-ball":
                return 500;
            case "ultra-ball":
                return 1500;
            case "master-ball":
                return 100000;
            default:
                return 1000;
        }
    }
}

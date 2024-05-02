package com.example.pokedex;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.pokedex.R;
import com.example.pokedex.ShopItemAdapter;
import com.example.pokedex.item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShopFragment extends Fragment {

    private RecyclerView recyclerView;
    private ShopItemAdapter itemAdapter;
    private List<item> itemList;

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
                            for (int i = 0; i < results.length(); i++) {
                                JSONObject itemObject = results.getJSONObject(i);
                                String itemName = itemObject.getString("name");
                                String itemUrl = itemObject.getString("url");
                                // Crear un nuevo Item y agregarlo a la lista
                                itemList.add(new item(itemName, "Category", itemUrl));
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
}

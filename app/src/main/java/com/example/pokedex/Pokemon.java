package com.example.pokedex;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Pokemon {
        private int number;
        private String name;
        private String url_API;
        private String url_default;
        private String url_shiny;
        private boolean shiny;
        private RequestQueue requestQueue;

        public Pokemon(int number, String name, String url_API) {
                this.number = number;
                this.name = name;
                this.url_API = url_API;
                this.shiny = false;
                this.url_default = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/25.png";

        }

        public int getNumber() {
                return number;
        }

        public void setNumber(int number) {
                this.number = number;
        }

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public String getUrl_API() {
                return url_API;
        }

        public void setUrl_API(String url_API) {
                this.url_API = url_API;
        }

        public String getUrl_default() {
                return url_default;
        }

        public void setUrl_default(String url_default) {
                this.url_default = url_default;
        }

        public String getUrl_shiny() {
                return url_shiny;
        }

        public void setUrl_shiny(String url_shiny) {
                this.url_shiny = url_shiny;
        }

        public boolean isShiny() {
                return shiny;
        }

        public void setShiny(boolean shiny) {
                this.shiny = shiny;
        }

        // Método para extraer el número del Pokémon desde la URL
        public void extractNumberFromUrl() {
                if (url_API != null) {
                        String[] urlParts = url_API.split("/");
                        String lastPart = urlParts[urlParts.length - 1];
                        this.number = Integer.parseInt(lastPart);
                }
        }



}

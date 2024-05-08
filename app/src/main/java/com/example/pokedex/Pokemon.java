package com.example.pokedex;

import com.android.volley.RequestQueue;

public class Pokemon {
        private int number;
        private String name;
        private String url_API;
        private String url_front_default;
        private String url_front_shiny;
        private String url_back_default;
        private String url_back_shiny;
        private String description;
        private boolean shiny;
        private RequestQueue requestQueue;

        public Pokemon(int number, String name, String url_API) {
                this.number = number;
                this.name = name;
                this.url_API = url_API;
                this.shiny = false;
                this.url_front_default = "https://www.pngall.com/wp-content/uploads/4/Pokeball-PNG-Free-Download.png";

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

        public String getUrl_front_default() {
                return url_front_default;
        }

        public void setUrl_front_default(String url_front_default) {
                this.url_front_default = url_front_default;
        }

        public String getUrl_front_shiny() {
                return url_front_shiny;
        }

        public void setUrl_front_shiny(String url_front_shiny) {
                this.url_front_shiny = url_front_shiny;
        }

        public boolean isShiny() {
                return shiny;
        }

        public void setShiny(boolean shiny) {
                this.shiny = shiny;
        }

        public String getUrl_back_default() {
                return url_back_default;
        }

        public void setUrl_back_default(String url_back_default) {
                this.url_back_default = url_back_default;
        }

        public String getUrl_back_shiny() {
                return url_back_shiny;
        }

        public void setUrl_back_shiny(String url_back_shiny) {
                this.url_back_shiny = url_back_shiny;
        }

        public String getDescription() {
                return description;
        }

        public void setDescription(String description) {
                this.description = description;
        }

        // Método para extraer el número del Pokémon desde la URL
        public void extractNumberFromUrl() {
                if (url_API != null) {
                        String[] urlParts = url_API.split("/");
                        String lastPart = urlParts[urlParts.length - 1];
                        this.number = Integer.parseInt(lastPart);
                }
        }

        @Override
        public String toString() {
                return "\nPokemon{" +
                        "number=" + number +
                        ", name='" + name + '\'' +
                        ", url_API='" + url_API + '\'' +
                        ", url_default='" + url_front_default + '\'' +
                        ", url_shiny='" + url_front_shiny + '\'' +
                        ", shiny=" + shiny +
                        '}';
        }
}

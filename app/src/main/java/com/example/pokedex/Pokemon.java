package com.example.pokedex;

import java.lang.reflect.Type;
import java.util.List;

public class Pokemon {
        private int number;
        private String name;
        private String url;

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

        public String getUrl() {
                return url;
        }

        public void setUrl(String url) {
                this.url = url;
        }

        // Método para extraer el número del Pokémon desde la URL
        public void extractNumberFromUrl() {
                if (url != null) {
                        String[] urlParts = url.split("/");
                        String lastPart = urlParts[urlParts.length - 1];
                        this.number = Integer.parseInt(lastPart);
                }
        }
}

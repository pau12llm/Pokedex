package com.example.pokedex;
import java.util.List;

public class Trainer {

        private String name;
        private int money;
        private List<item> items;
        private List<Pokemon> pokemons;

        public Trainer(String name, int money, List<item> items, List<Pokemon> pokemons) {
            this.name = name;
            this.money = money;
            this.items = items;
            this.pokemons = pokemons;
        }

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getMoney() {
            return money;
        }

        public void setMoney(int money) {
            this.money = money;
        }

        public List<item> getItems() {
            return items;
        }

        public void setItems(List<item> items) {
            this.items = items;
        }

        public List<Pokemon> getPokemons() {
            return pokemons;
        }

        public void setPokemons(List<Pokemon> pokemons) {
            this.pokemons = pokemons;
        }


}

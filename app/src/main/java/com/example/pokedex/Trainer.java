package com.example.pokedex;
import java.util.List;

public class Trainer {

        private String name;
        private int money;
        private List<Item> Items;
        private List<Pokemon> pokemons;

        public Trainer(String name, int money, List<Item> Items, List<Pokemon> pokemons) {
            this.name = name;
            this.money = money;
            this.Items = Items;
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

        public List<Item> getItems() {
            return Items;
        }

        public void setItems(List<Item> Items) {
            this.Items = Items;
        }

        public List<Pokemon> getPokemons() {
            return pokemons;
        }

        public void setPokemons(List<Pokemon> pokemons) {
            this.pokemons = pokemons;
        }


}

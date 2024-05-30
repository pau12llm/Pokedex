package com.example.pokedex;

import com.android.volley.RequestQueue;

import java.io.Serializable;

public class Pokemon implements Serializable{
        private int number;
        private String name;
        private String url_API;
        private String url_front_default;
        private String url_front_shiny;
        private String url_back_default;
        private String url_back_shiny;
        private String description;
        private boolean shiny;
        private int hp;
        private int attack;
        private int special_attack;
        private int defense;
        private int special_defense;
        private int speed;
        private String[][] ability;
        private boolean legendary;
        private int evolution;

        private String pokeball;
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


        public int getHp() {
                return hp;
        }

        public void setHp(int hp) {
                this.hp = hp;
        }

        public int getAttack() {
                return attack;
        }

        public void setAttack(int attack) {
                this.attack = attack;
        }

        public int getSpecial_attack() {
                return special_attack;
        }

        public void setSpecial_attack(int special_attack) {
                this.special_attack = special_attack;
        }

        public int getDefense() {
                return defense;
        }

        public void setDefense(int defense) {
                this.defense = defense;
        }

        public int getSpecial_defense() {
                return special_defense;
        }

        public void setSpecial_defense(int special_defense) {
                this.special_defense = special_defense;
        }

        public int getSpeed() {
                return speed;
        }

        public void setSpeed(int speed) {
                this.speed = speed;
        }

        public boolean isLegendary() {
                return legendary;
        }

        public void setLegendary(boolean legendary) {
                this.legendary = legendary;
        }

        public int getEvolution() {
                return evolution;
        }

        public void setEvolution(int evolution) {
                this.evolution = evolution;
        }

        public String[][] getAbility() {
                return ability;
        }

        public void setAbility(String[][] ability) {
                this.ability = ability;
        }

        public String getPokeball() {
                return pokeball;
        }

        public void setPokeball(String pokeball) {
                this.pokeball = pokeball;
        }

        @Override
        public String toString() {
                return "Pokemon{" +
                        "number=" + number +
                        ", name='" + name + '\'' +
                        ", url_API='" + url_API + '\'' +
                        ", url_front_default='" + url_front_default + '\'' +
                        ", url_front_shiny='" + url_front_shiny + '\'' +
                        ", url_back_default='" + url_back_default + '\'' +
                        ", url_back_shiny='" + url_back_shiny + '\'' +
                        ", description='" + description + '\'' +
                        ", shiny=" + shiny +
                        ", requestQueue=" + requestQueue +
                        ", hp=" + hp +
                        ", attack=" + attack +
                        ", special_attack=" + special_attack +
                        ", defense=" + defense +
                        ", special_defense=" + special_defense +
                        ", speed=" + speed +
                        '}';
        }
}

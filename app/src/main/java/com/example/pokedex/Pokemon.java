package com.example.pokedex;

import java.lang.reflect.Type;
import java.util.List;

public class Pokemon {
    private String name;
    private int id;
    private List<PokemonType> types;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<PokemonType> getTypes() {
        return types;
    }

    public void setTypes(List<PokemonType> types) {
        this.types = types;
    }

    public static class PokemonType {
        private Type type;

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }
    }
}
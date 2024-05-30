package com.example.pokedex;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class PokemonAdapter extends BaseAdapter {

    private List<Pokemon> pokemonList;
    private List<Pokemon> filteredPokemonList = pokemonList;
    private static final String TAG = "PokedexFragment";
    private Context context;

    public PokemonAdapter(Context context, List<Pokemon> pokemonList) {
        this.context = context;
        this.pokemonList = pokemonList;
        this.filteredPokemonList = new ArrayList<>(pokemonList);
    }

    public List<Pokemon> getPokemonList() {
        return pokemonList;
    }

    public void setPokemonList(List<Pokemon> pokemonList) {
        this.pokemonList = pokemonList;
    }

    @Override
    public int getCount() {
        return pokemonList.size();
    }

    @Override
    public Object getItem(int position) {
        return pokemonList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_pokemon, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.textViewName = convertView.findViewById(R.id.textViewName);
            viewHolder.imageViewPokemon = convertView.findViewById(R.id.imageView);
            viewHolder.pokeballImg = convertView.findViewById(R.id.pokeballImg);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Pokemon pokemon = pokemonList.get(position);
        viewHolder.textViewName.setText(pokemon.getName());

        // Obtener la URL de la imagen del Pokémon (por defecto o brillante)
        String imageUrl;
        if (pokemon.isShiny()) {
            imageUrl = pokemon.getUrl_front_shiny();
        } else {
            imageUrl = pokemon.getUrl_front_default();
        }

        // Cargar la imagen utilizando Glide desde la URL
        Glide.with(context)
                .load(imageUrl)
                .into(viewHolder.imageViewPokemon);

        // Cargar la imagen de la Poké Ball utilizando Glide
        Glide.with(context)
                .load(pokemon.getPokeball())
                .into(viewHolder.pokeballImg);

        return convertView;
    }

    private static class ViewHolder {
        TextView textViewName;
        ImageView imageViewPokemon;
        ImageView pokeballImg;
    }
}

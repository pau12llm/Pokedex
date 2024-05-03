package com.example.pokedex;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class PokemonAdapter extends BaseAdapter {

    private List<Pokemon> pokemonList;
    private Context context;

    public PokemonAdapter(Context context, List<Pokemon> pokemonList) {
        this.context = context;
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
            imageUrl = pokemon.getUrl_shiny();
        } else {
            imageUrl = pokemon.getUrl_default();
        }

        // Cargar la imagen utilizando Glide desde la URL
        Glide.with(context)
                .load(imageUrl)
                .into(viewHolder.imageViewPokemon);


        Glide.with(context)
                .load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/items/master-ball.png")
                .into(viewHolder.pokeballImg);

        return convertView;
    }


    private static class ViewHolder {
        TextView textViewName;
        ImageView imageViewPokemon;
        ImageView pokeballImg;
    }
}

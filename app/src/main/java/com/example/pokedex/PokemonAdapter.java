package com.example.pokedex;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PokemonAdapter extends BaseAdapter {

    private List<Pokemon> pokemonList;
    private Context context;

    public PokemonAdapter(Context context, List<Pokemon> pokemonNames) {
        this.context = context;
        this.pokemonList = pokemonNames;
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

            convertView.setTag(viewHolder);


        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Pokemon pokemon = pokemonList.get(position);
        viewHolder.textViewName.setText(pokemon.getName());

        // Cargar la imagen utilizando Glide desde la URL
        Glide.with(context)
                .load(pokemon.getUrl_default())
//                .placeholder(R.drawable.placeholder_image) // Imagen de marcador de posición opcional
//                .error(R.drawable.error_image) // Imagen de error opcional
                .into(viewHolder.imageViewPokemon);

        return convertView;
    }

    private static class ViewHolder {
        TextView textViewName;
        ImageView imageViewPokemon;
    }
}